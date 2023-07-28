package com.imss.sivimss.planfunerario.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.planfunerario.beans.RenovarExt;
import com.imss.sivimss.planfunerario.exception.BadRequestException;
import com.imss.sivimss.planfunerario.model.request.FiltrosConvenioExtRequest;
import com.imss.sivimss.planfunerario.model.request.RenovarPlanExtRequest;
import com.imss.sivimss.planfunerario.model.request.UsuarioDto;
import com.imss.sivimss.planfunerario.model.response.BenefResponse;
import com.imss.sivimss.planfunerario.model.response.RenovacionExtResponse;
import com.imss.sivimss.planfunerario.service.RenovarExtService;
import com.imss.sivimss.planfunerario.util.AppConstantes;
import com.imss.sivimss.planfunerario.util.ConvertirGenerico;
import com.imss.sivimss.planfunerario.util.DatosRequest;
import com.imss.sivimss.planfunerario.util.LogUtil;
import com.imss.sivimss.planfunerario.util.ProviderServiceRestTemplate;
import com.imss.sivimss.planfunerario.util.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RenovarExtImpl implements RenovarExtService{
	
	private static final String ALTA = "alta";
	private static final String MODIFICACION = "modificacion";
	private static final String CONSULTA = "consulta";
	private static final String INFORMACION_INCOMPLETA = "Informacion incompleta";
	
	@Autowired
	private LogUtil logUtil;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Value("${endpoints.rutas.dominio-consulta}")
	private String urlConsulta;
	@Value("${endpoints.rutas.dominio-consulta-paginado}")
	private String urlPaginado;
	@Value("${endpoints.rutas.dominio-actualizar}")
	private String urlActualizar;
	@Value("${endpoints.rutas.dominio-insertar-multiple}")
	private String urlInsertarMultiple;
	@Value("${formato-fecha}")
	private String fecFormat;

	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	Gson gson = new Gson();
	
	RenovarExt renovarExt= new RenovarExt();
	
	
	@Override
	public Response<?> buscarRenovacionExt(DatosRequest request, Authentication authentication) throws IOException {
		String datosJson = String.valueOf(request.getDatos().get("datos"));
		UsuarioDto usuario = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		FiltrosConvenioExtRequest filtros = gson.fromJson(datosJson, FiltrosConvenioExtRequest.class);
		 Integer pagina = Integer.valueOf(Integer.parseInt(request.getDatos().get("pagina").toString()));
	        Integer tamanio = Integer.valueOf(Integer.parseInt(request.getDatos().get("tamanio").toString()));
	        filtros.setTamanio(tamanio.toString());
	        filtros.setPagina(pagina.toString());
	        if(usuario.getIdOficina()==3) {
	        	usuario.setIdDelegacion(filtros.getIdDelegacion());
	        	usuario.setIdVelatorio(filtros.getIdVelatorio());
	        }
		Response<?> response = providerRestTemplate.consumirServicio(renovarExt.buscarConvenioExt(request, filtros, fecFormat).getDatos(), urlPaginado,
					authentication);
		if(response.getDatos().toString().contains("id")){
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"CONSULTA CONVENIO RENOVACION EXT OK", CONSULTA, authentication);
			return response;
		}else {
			logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"No se encontro información relacionada a tu busqueda.", CONSULTA, authentication);
			response.setDatos(null);
			response.setMensaje("45");
			return response;
		}
	}


	@Override
	public Response<?> verDetalleRenovacionExt(DatosRequest request, Authentication authentication) throws IOException {
		String palabra = request.getDatos().get("palabra").toString();
		Response<?> response = new Response<>();
		List<RenovacionExtResponse> renovacionExtResponse;
		List<BenefResponse> beneficiarios;
		Response<?> responseConsultaDetalle = providerRestTemplate.consumirServicio(renovarExt.verDetalle(request, palabra, fecFormat).getDatos(), urlConsulta,
				authentication);
		if(validarFallecido(palabra, authentication)) {
			logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"39 TITULAR DEL CONVENIO FALLECIO NO PUEDE RENOVAR EL CONVENIO", CONSULTA, authentication);
			response.setCodigo(200);
	         response.setError(false);
  			response.setDatos(null);
  			 response.setMensaje("39");
  			return response;
		  }
		if (responseConsultaDetalle.getCodigo() == 200 && responseConsultaDetalle.getDatos().toString().contains("id")) {
			renovacionExtResponse = Arrays.asList(modelMapper.map(responseConsultaDetalle.getDatos(), RenovacionExtResponse[].class));
			beneficiarios = Arrays.asList(modelMapper.map(providerRestTemplate.consumirServicio(renovarExt.buscarBeneficiarios(request, palabra).getDatos(), urlConsulta, authentication).getDatos(), BenefResponse[].class));  
			RenovacionExtResponse datosRenovacionExt = renovacionExtResponse.get(0);
			datosRenovacionExt.setBeneficiarios(beneficiarios);
			response.setDatos(ConvertirGenerico.convertInstanceOfObject(datosRenovacionExt));
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"DETALLE CONVENIO OK", CONSULTA, authentication);
		}
		 response.setCodigo(200);
         response.setError(false);
         response.setMensaje("Exito");
		
		return response;
	}


	private boolean validarFallecido(String palabra, Authentication authentication) throws IOException {
		Response<?> response= providerRestTemplate.consumirServicio(renovarExt.validarFallecido(palabra).getDatos(), urlConsulta,
				authentication);
	Object rst=response.getDatos();
	return !rst.toString().equals("[]");
	}


	@Override
	public Response<?> actualizarEstatus(DatosRequest request, Authentication authentication) throws IOException {
		Response<?> response;
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
	    RenovarPlanExtRequest extRequest = gson.fromJson(datosJson, RenovarPlanExtRequest.class);	
		UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		if (extRequest.getIndRenovacion() == null || extRequest.getIdConvenio()==null ) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, INFORMACION_INCOMPLETA);
		}
		try {
			if(extRequest.getIndRenovacion()==1) {
				response = providerRestTemplate.consumirServicio(renovarExt.actualizarVigenciaConvenio(extRequest.getIdConvenio(), usuarioDto.getIdUsuario()).getDatos(), urlActualizar,
						authentication);
				logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"SE ACTUALIZO CORECTAMENTE LA VIGENCIA DEL CONVENIO", MODIFICACION, authentication);
				if(response.getCodigo()==200) {
					providerRestTemplate.consumirServicio(renovarExt.actualizarEstatusConvenio(extRequest, usuarioDto.getIdUsuario()).getDatos(), urlInsertarMultiple,
							authentication);
					logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"SE ACTUALIZO EL ESTATUS DEL CONVENIO OK", MODIFICACION, authentication);
			}
		
		}else {
			response = providerRestTemplate.consumirServicio(renovarExt.actualizarEstatusConvenio(extRequest, usuarioDto.getIdUsuario()).getDatos(), urlInsertarMultiple,
					authentication);
		}
		}catch (Exception e) {
			String consulta = renovarExt.actualizarVigenciaConvenio(extRequest.getIdConvenio(), usuarioDto.getIdUsuario()).getDatos().get("query").toString();
			String encoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error("Error al ejecutar la query " +encoded);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"ERROR AL ACTUALIZAR EL ESTATUS DEL CONVENIO: Fallo al ejecutar la query", MODIFICACION, authentication);
			throw new IOException("5", e.getCause());
		}
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"SE AGREGO CORRECTAMENTE LA JUSTIFICACION", ALTA, authentication);
		return response;
	}


}
