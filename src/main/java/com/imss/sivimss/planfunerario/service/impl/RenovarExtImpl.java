package com.imss.sivimss.planfunerario.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.planfunerario.beans.RenovarExt;
import com.imss.sivimss.planfunerario.model.request.FiltrosConvenioExtRequest;
import com.imss.sivimss.planfunerario.model.response.BenefResponse;
import com.imss.sivimss.planfunerario.model.response.RenovacionExtResponse;
import com.imss.sivimss.planfunerario.service.RenovarExtService;
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
	private static final String BAJA = "baja";
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
	@Value("${formato-fecha}")
	private String fecFormat;

	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	Gson gson = new Gson();
	
	RenovarExt renovarExt= new RenovarExt();
	
	
	@Override
	public Response<?> buscarRenovacionExt(DatosRequest request, Authentication authentication) throws IOException {
		String datosJson = String.valueOf(request.getDatos().get("datos"));
		FiltrosConvenioExtRequest filtros = gson.fromJson(datosJson, FiltrosConvenioExtRequest.class);
		 Integer pagina = Integer.valueOf(Integer.parseInt(request.getDatos().get("pagina").toString()));
	        Integer tamanio = Integer.valueOf(Integer.parseInt(request.getDatos().get("tamanio").toString()));
	        filtros.setTamanio(tamanio.toString());
	        filtros.setPagina(pagina.toString());
		Response<?> response = providerRestTemplate.consumirServicio(renovarExt.buscarConvenioExt(request, filtros).getDatos(), urlPaginado,
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
		RenovacionExtResponse datosRenovacionExt = new RenovacionExtResponse();
		Response<?> responseConsultaDetalle = providerRestTemplate.consumirServicio(renovarExt.verDetalle(request, palabra).getDatos(), urlConsulta,
				authentication);
		if (responseConsultaDetalle.getCodigo() == 200) {
			renovacionExtResponse = Arrays.asList(modelMapper.map(responseConsultaDetalle.getDatos(), RenovacionExtResponse[].class));
			beneficiarios = Arrays.asList(modelMapper.map(providerRestTemplate.consumirServicio(renovarExt.buscarBeneficiarios(request, palabra).getDatos(), urlConsulta, authentication).getDatos(), BenefResponse[].class));  
			datosRenovacionExt = renovacionExtResponse.get(0);
			datosRenovacionExt.setBeneficiarios(beneficiarios);
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"DETALLE CONVENIO OK", CONSULTA, authentication);
		}
		 response.setCodigo(200);
         response.setError(false);
         response.setMensaje("Exito");
		response.setDatos(ConvertirGenerico.convertInstanceOfObject(datosRenovacionExt));
		return response;
	}


}
