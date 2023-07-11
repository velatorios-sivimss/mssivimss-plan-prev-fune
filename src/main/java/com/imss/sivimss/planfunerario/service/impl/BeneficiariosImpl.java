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
import com.imss.sivimss.planfunerario.model.request.CatalogosRequest;
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
	private static final String ERROR = "Fallo al ejecutar la query";
	private static final String INFORMACION_INCOMPLETA = "Informacion Incompleta";
	
	@Autowired
	private LogUtil logUtil;
	
	@Value("${endpoints.rutas.dominio-consulta}")
	private String urlConsulta;
	@Value("${endpoints.rutas.dominio-crear-multiple}")
	private String urlCrearMultiple;
	@Value("${endpoints.rutas.dominio-crear}")
	private String urlCrear;
	@Value("${endpoints.rutas.dominio-actualizar}")
	private String urlActualizar;
	@Value("${formato-fecha}")
	private String fecFormat;

	@Value("${endpoints.ms-reportes}")
	private String urlReportes;

	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	Gson gson = new Gson();
	
	BeneficiariosBean benefBean = new BeneficiariosBean();

	@Override
	public Response<?> buscarBeneficiarios(DatosRequest request, Authentication authentication) throws IOException {
		return providerRestTemplate.consumirServicio(benefBean.beneficiarios(request).getDatos(), urlConsulta,
				authentication);
	}

	@Override
	public Response<?> detalleBeneficiario(DatosRequest request, Authentication authentication) throws IOException {
		String datosJson = String.valueOf(request.getDatos().get("datos"));
	FiltrosBeneficiariosRequest filtros = gson.fromJson(datosJson, FiltrosBeneficiariosRequest.class);
	if(filtros.getIdBeneficiario()==null) {
		throw new BadRequestException(HttpStatus.BAD_REQUEST, INFORMACION_INCOMPLETA);	
		}	
	return providerRestTemplate.consumirServicio(benefBean.detalleBeneficiarios(request, filtros.getIdBeneficiario()).getDatos(), urlConsulta,
				authentication);
	}

	@Override
	public Response<?> crearBeneficiario(DatosRequest request, Authentication authentication) throws IOException {
		Response<?> response = new Response<>();
		try {
			String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		    PersonaRequest benefRequest = gson.fromJson(datosJson, PersonaRequest.class);	
			UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
			benefBean = new BeneficiariosBean(benefRequest);
			benefBean.setUsuarioAlta(usuarioDto.getIdUsuario

	
			if(benefRequest.getBeneficiario().getIdContratanteConvenioPf()==null || benefRequest.getBeneficiario().getIdParentesco()==null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, INFORMACION_INCOMPLETA);	
			}
			i

	benefBean.setIndActaMatrimonio(benefRequest

	getDocPlanAnterior().getIndActaMatrimonio());
				benefBean.setIndDeclaracionConcubinato(benefRequest.getDocPlanAnterior().getIndDeclaracionConcubinato());
				response = providerRestTemplate.consumirServicio(benefBean.insertarPersonaPlanAnterior().getDatos(), urlCrear,
						authentication);
				logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"Estatus OK", ALTA, authentication);
				if(response.getCodigo()==200) {
					Integer id=(Integer) response.getDatos();
					providerRestTemplate.consumirServicio(benefBean.insertarBeneficiarioPlanAnterior(id).getDatos(), urlCrearMultiple,
							authentication);
				}
			}else {
				response = providerRestTemplate.consumirServicio(benefBean.insertarPersona().getDatos(), urlCrearMultiple,
						authentication);
				logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"Estatus OK", ALTA, authentication);
			}
			  ret
	rn response;
						
		}catch (Exception e) {
			String consulta = benefBean.insertarPersona().getDatos().get(""+AppConstantes.QUERY+"").toString();
			String encoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			lgUtil.crearArchi

	
	
	
			
		}

	@Override
	public Response<?> editarBeneficiario(DatosRequest request, Authentication authentication) throws IOException {
		R

	 

		benefBea 	
		if(benefRequest.getIdPersona()==null && benefRequest.getIdBeneficiario()==null) {
		throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta 
	
		response = providerRestTemplate.consumirServicio(benefBean.editarPersona().getDatos(), urlActua			autheniation);	logUtil.crearArchivoLog(Level.INFO.toString(), this

		providerRestTemplate.consumirServico(enefBean.editarBeneficiario(benefReques			benefRequest.getBeneficiario().getIdParentesco(, benefRequest.getBenefici
		 efBean.setIndCom

	enefBean.setIndDeclaracionConcubinato(benefRequest.getDocPlanAnterior().getIndDeclaracionConcubinato());
			if(response.getCodigo()==200 && benefRequest.getDocPlanAnterior()!=null) {
				providerRestTemplate.consumirServicio(benefBean.editarDocPlanAnterior().getDatos(), urlActualizar,
						authentication);	
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
	  Response<?> response = providerRestTemplate.consumirServicio(benefBean.cambiarEstatus(benefRequest.getIdBeneficiario(), benefRequest.getEstatusBenefic()).getDatos(), urlActualizar,
				authentication);
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"Todo correcto", BAJA, authentication);
	re

	verride blic 

	esponse<?> buscarBeneficiariosPlanAnterior(DatosRequest request, Authentication authentication)
			throws IOException {
		return providerRestTemplate.consumirServicio(benefBean.beneficiariosPlanAnterior(request).getDatos(), urlConsulta,
				authentication);
	}

	@Override
	public Response<?> buscarCatalogos(DatosRequest request, Authentication authentication) throws IOException {
		String datosJson = String.valueOf(request.getDatos().get("datos"));
		CatalogosRequest filtros = gson.fromJson(datosJson, CatalogosRequest.class);
		

				auth

			return providerRestTemplate.consumirServicio(benefBean.buscarCatalogosInfoConvenioActual(request, filtros.getIdConvenio(), fecFormat).getDatos(), urlConsulta,
					authentication);

		f(filtros.getIdCatalogo()==3) {
				
			return providerRestTemplate.consumirServicio(benefBean.buscarCatalogosParentescos(request).getDatos(), urlConsulta,
					authentication);
		}else {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "INFORMACION INCOMPLETA");			
		}
			

		
     
}
					     
					
					
					
		

		   
					
					
		 

			
		

	


