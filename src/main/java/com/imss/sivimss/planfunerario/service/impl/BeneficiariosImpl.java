package com.imss.sivimss.planfunerario.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.planfunerario.beans.BeneficiariosBean;
import com.imss.sivimss.planfunerario.exception.BadRequestException;
import com.imss.sivimss.planfunerario.model.request.PersonaRequest;
import com.imss.sivimss.planfunerario.model.request.FiltrosBeneficiariosRequest;
import com.imss.sivimss.planfunerario.model.request.UsuarioDto;
import com.imss.sivimss.planfunerario.service.BeneficiariosService;
import com.imss.sivimss.planfunerario.util.AppConstantes;
import com.imss.sivimss.planfunerario.util.DatosRequest;
import com.imss.sivimss.planfunerario.util.LogUtil;
import com.imss.sivimss.planfunerario.util.ProviderServiceRestTemplate;
import com.imss.sivimss.planfunerario.util.Response;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

@Slf4j
@Service
public class BeneficiariosImpl implements BeneficiariosService{
	
	private static final String ALTA = "alta";
	private static final String BAJA = "baja";
	private static final String MODIFICACION = "modificacion";
	private static final String CONSULTA = "consulta";
	private static final String ERROR = "Fallo al ejecutar la query ";
	
	@Autowired
	private LogUtil logUtil;
	
	@Value("${endpoints.dominio-consulta}")
	private String urlConsulta;
	
	@Value("${endpoints.ms-reportes}")
	private String urlReportes;
	
	 private static final String PATH_CONSULTA="generico/consulta";
	 private static final String PATH_ACTUALIZAR="generico/actualizar";
	 
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	Gson gson = new Gson();
	
	BeneficiariosBean benefBean = new BeneficiariosBean();

	@Override
	public Response<?> buscarBeneficiarios(DatosRequest request, Authentication authentication) throws IOException {
		return providerRestTemplate.consumirServicio(benefBean.beneficiarios(request).getDatos(), urlConsulta + PATH_CONSULTA,
				authentication);
	}

	@Override
	public Response<?> detalleBeneficiario(DatosRequest request, Authentication authentication) throws IOException {
		String datosJson = String.valueOf(request.getDatos().get("datos"));
	FiltrosBeneficiariosRequest filtros = gson.fromJson(datosJson, FiltrosBeneficiariosRequest.class);
	log.info("convenio: " +filtros.getIdConvenioPF());
		return providerRestTemplate.consumirServicio(benefBean.detalleBeneficiarios(request, filtros.getIdBeneficiario(), filtros.getIdConvenioPF()).getDatos(), urlConsulta + PATH_CONSULTA,
				authentication);
	}

	@Override
	public Response<?> crearBeneficiario(DatosRequest request, Authentication authentication) throws IOException {
		Response<?> response;
		try {
			String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		    PersonaRequest benefRequest = gson.fromJson(datosJson, PersonaRequest.class);	
			UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
			benefBean = new BeneficiariosBean(benefRequest);
			benefBean.setUsuarioAlta(usuarioDto.getIdUsuario());
			
			if(benefRequest.getBeneficiario().getIdContratanteConvenioPf()==null || benefRequest.getBeneficiario().getIdParentesco()==null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta ");	
			}
				response = providerRestTemplate.consumirServicio(benefBean.insertarPersona().getDatos(), urlConsulta + "/generico/crearMultiple",
						authentication);
				logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"Estatus OK", ALTA, authentication);
				return response;		
		}catch (Exception e) {
			String consulta = benefBean.insertarPersona().getDatos().get(""+AppConstantes.QUERY+"").toString();
			String encoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error("Error al ejecutar la query " +encoded);
			logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),ERROR, CONSULTA, authentication);
			throw new IOException("5", e.getCause()) ;
		}
		
			
		}

	@Override
	public Response<?> editarBeneficiario(DatosRequest request, Authentication authentication) throws IOException {
		Response<?> response;
		try {
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
	    PersonaRequest benefRequest = gson.fromJson(datosJson, PersonaRequest.class);	
		UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		benefBean = new BeneficiariosBean(benefRequest);
		benefBean.setUsuarioAlta(usuarioDto.getIdUsuario());
		
		if(benefRequest.getIdPersona()==null) {
		throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta ");	
		}
			response = providerRestTemplate.consumirServicio(benefBean.editarPersona().getDatos(), urlConsulta + PATH_ACTUALIZAR,
					authentication);
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"Todo correcto", MODIFICACION, authentication);
			if(response.getCodigo()==200) {
				providerRestTemplate.consumirServicio(benefBean.editarBeneficiario(benefRequest.getIdPersona(), usuarioDto.getIdUsuario(),
						benefRequest.getBeneficiario().getIdParentesco(), benefRequest.getBeneficiario().getActaNac()).getDatos(), urlConsulta + PATH_ACTUALIZAR,
						authentication);
			}else {
				String consulta = benefBean.editarBeneficiario(benefRequest.getIdPersona(), usuarioDto.getIdUsuario(),
						benefRequest.getBeneficiario().getIdParentesco(), benefRequest.getBeneficiario().getActaNac()).getDatos().get(""+AppConstantes.QUERY+"").toString();
				String encoded = new String(DatatypeConverter.parseBase64Binary(consulta));
				log.error("Error al ejecutar la query" +encoded);
				logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),ERROR, MODIFICACION, authentication);
				throw new BadRequestException(HttpStatus.BAD_REQUEST, " 5 FALLO AL ACTUALIZAR EL BENEFICIARIO ");		
			}
			
			return response;		
	}catch (Exception e) {
		String consulta = benefBean.editarPersona().getDatos().get("query").toString();
		String encoded = new String(DatatypeConverter.parseBase64Binary(consulta));
		log.error("Error al ejecutar la query" +encoded);
		logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),ERROR, MODIFICACION, authentication);
		throw new IOException("5", e.getCause()) ;
	}
	}

	@Override
	public Response<?> estatusBeneficiario(DatosRequest request, Authentication authentication) throws IOException {
		UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		benefBean.setUsuarioBaja(usuarioDto.getIdUsuario());
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
	  PersonaRequest benefRequest = gson.fromJson(datosJson, PersonaRequest.class);	
	  Response<?> response = providerRestTemplate.consumirServicio(benefBean.cambiarEstatus(benefRequest.getIdBeneficiario()).getDatos(), urlConsulta +PATH_ACTUALIZAR,
				authentication);
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"Todo correcto", BAJA, authentication);
	return response;
	}

	@Override
	public Response<?> buscarBeneficiariosPlanAnterior(DatosRequest request, Authentication authentication)
			throws IOException {
		return providerRestTemplate.consumirServicio(benefBean.beneficiariosPlanAnterior(request).getDatos(), urlConsulta + PATH_CONSULTA,
				authentication);
	}
		

}
