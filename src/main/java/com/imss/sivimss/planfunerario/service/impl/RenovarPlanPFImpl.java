package com.imss.sivimss.planfunerario.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.planfunerario.beans.RenovarPlanPFBean;
import com.imss.sivimss.planfunerario.exception.BadRequestException;
import com.imss.sivimss.planfunerario.model.request.AltaBeneficiarioRequest;
import com.imss.sivimss.planfunerario.model.request.FiltrosBeneficiariosRequest;
import com.imss.sivimss.planfunerario.model.request.UsuarioDto;
import com.imss.sivimss.planfunerario.service.RenovarPlanPFService;
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
public class RenovarPlanPFImpl implements RenovarPlanPFService{
	
	private static final String ALTA = "alta";
	private static final String BAJA = "baja";
	private static final String MODIFICACION = "modificacion";
	private static final String CONSULTA = "consulta";
	
	@Autowired
	private LogUtil logUtil;
	
	@Value("${endpoints.dominio-consulta}")
	private String urlConsulta;
	
	@Value("${endpoints.dominio-consulta-paginado}")
	private String urlPaginado;
	
	@Value("${endpoints.dominio-crear-multiple}")
	private String urlCrearMultiple;
	
	@Value("${endpoints.dominio-actualizar}")
	private String urlActualizar;
	
	@Value("${endpoints.ms-reportes}")
	private String urlReportes;
	
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	Gson gson = new Gson();
	
	RenovarPlanPFBean renovarBean = new RenovarPlanPFBean();

	@Override
	public Response<?> buscarBeneficiarios(DatosRequest request, Authentication authentication) throws IOException {
		return providerRestTemplate.consumirServicio(renovarBean.beneficiarios(request).getDatos(), urlConsulta,
				authentication);
	}

	@Override
	public Response<?> detalleBeneficiario(DatosRequest request, Authentication authentication) throws IOException {
		String datosJson = String.valueOf(request.getDatos().get("datos"));
	FiltrosBeneficiariosRequest filtros = gson.fromJson(datosJson, FiltrosBeneficiariosRequest.class);
	log.info("convenio: " +filtros.getIdConvenioPF());
		return providerRestTemplate.consumirServicio(renovarBean.detalleBeneficiarios(request, filtros.getIdBeneficiario(), filtros.getIdConvenioPF()).getDatos(), urlConsulta,
				authentication);
	}

	@Override
	public Response<?> crearBeneficiario(DatosRequest request, Authentication authentication) throws IOException {
		Response<?> response;
		try {
			String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		    AltaBeneficiarioRequest benefRequest = gson.fromJson(datosJson, AltaBeneficiarioRequest.class);	
			UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
			renovarBean = new RenovarPlanPFBean(benefRequest);
			renovarBean.setUsuarioAlta(usuarioDto.getIdUsuario());
			
			if(benefRequest.getIdConvenioPF()==null || benefRequest.getIdParentesco()==null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta ");	
			}
				response = providerRestTemplate.consumirServicio(renovarBean.insertarPersona().getDatos(), urlCrearMultiple,
						authentication);
				logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"Todo correcto", ALTA, authentication);
				return response;		
		}catch (Exception e) {
			String consulta = renovarBean.insertarPersona().getDatos().get("query").toString();
			String encoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error("Error al ejecutar la query" +encoded);
			logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"Fallo al ejecutar la query", CONSULTA, authentication);
			throw new IOException("5", e.getCause()) ;
		}
		
			
		}

	@Override
	public Response<?> editarBeneficiario(DatosRequest request, Authentication authentication) throws IOException {
		Response<?> response;
		try {
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
	    AltaBeneficiarioRequest benefRequest = gson.fromJson(datosJson, AltaBeneficiarioRequest.class);	
		UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		renovarBean = new RenovarPlanPFBean(benefRequest);
		renovarBean.setUsuarioAlta(usuarioDto.getIdUsuario());
		
		if(benefRequest.getIdPersona()==null) {
		throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta ");	
		}
			response = providerRestTemplate.consumirServicio(renovarBean.editarPersona().getDatos(), urlActualizar,
					authentication);
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"Todo correcto", MODIFICACION, authentication);
			if(response.getCodigo()==200) {
				providerRestTemplate.consumirServicio(renovarBean.editarBeneficiario(benefRequest.getIdPersona(), usuarioDto.getIdUsuario(),
						benefRequest.getIdParentesco(), benefRequest.getActaNac()).getDatos(), urlActualizar,
						authentication);
			}else {
				logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"FALLO AL ACTUALIZAR EL BENEFICIARIO", MODIFICACION, authentication);
				throw new BadRequestException(HttpStatus.BAD_REQUEST, "FALLO AL ACTUALIZAR EL BENEFICIARIO ");		
			}
			
			return response;		
	}catch (Exception e) {
		String consulta = renovarBean.editarPersona().getDatos().get("query").toString();
		String encoded = new String(DatatypeConverter.parseBase64Binary(consulta));
		log.error("Error al ejecutar la query" +encoded);
		logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"Fallo al ejecutar la query", MODIFICACION, authentication);
		throw new IOException("5", e.getCause()) ;
	}
	}
		

}
