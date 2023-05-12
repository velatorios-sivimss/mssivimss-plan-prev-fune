package com.imss.sivimss.planfunerario.service.impl;

import java.io.IOException;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.planfunerario.beans.BeneficiariosBean;
import com.imss.sivimss.planfunerario.beans.RenovarBean;
import com.imss.sivimss.planfunerario.exception.BadRequestException;
import com.imss.sivimss.planfunerario.model.request.FiltrosConvenioPFRequest;
import com.imss.sivimss.planfunerario.model.request.PersonaRequest;
import com.imss.sivimss.planfunerario.model.request.RenovarPlanPFRequest;
import com.imss.sivimss.planfunerario.model.request.UsuarioDto;
import com.imss.sivimss.planfunerario.service.RenovarPlanService;
import com.imss.sivimss.planfunerario.util.AppConstantes;
import com.imss.sivimss.planfunerario.util.DatosRequest;
import com.imss.sivimss.planfunerario.util.LogUtil;
import com.imss.sivimss.planfunerario.util.ProviderServiceRestTemplate;
import com.imss.sivimss.planfunerario.util.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RenovarPlanImpl implements RenovarPlanService {

	private static final String ALTA = "alta";
	private static final String BAJA = "baja";
	private static final String MODIFICACION = "modificacion";
	private static final String CONSULTA = "consulta";
	
	@Autowired
	private LogUtil logUtil;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Value("${endpoints.dominio-consulta}")
	private String urlConsulta;
	
	@Value("${endpoints.ms-reportes}")
	private String urlReportes;
	
	 private static final String PATH_CONSULTA="generico/consulta";
	 private static final String PATH_ACTUALIZAR="generico/actualizar";
	 
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	Gson gson = new Gson();
	
	RenovarBean renovarBean = new RenovarBean();
	
	
	@Override
	public Response<?> buscarConvenioNuevo(DatosRequest request, Authentication authentication) throws IOException {		 
		String datosJson = String.valueOf(request.getDatos().get("datos"));
		FiltrosConvenioPFRequest filtros = gson.fromJson(datosJson, FiltrosConvenioPFRequest .class);
		if(filtros.getFolio()==null && filtros.getRfc()==null && filtros.getNumIne()==null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta ");	
		}
			Response<?> response = providerRestTemplate.consumirServicio(renovarBean.buscarNuevo(request, filtros).getDatos(), urlConsulta + PATH_CONSULTA,
					authentication);
			Object rst = response.getDatos();
		      if(rst.toString().equals("[]")){
		    		logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"45 No se encontro informacion relacionada a tu busqueda " +filtros.getFolio(), CONSULTA, authentication);
		    		response.setMensaje("45");
		      }else {
		    	  if(!validarVigenciaCto(filtros.getFolio(), authentication)) {
		    		if(!validarFallecido(filtros.getRfc(), authentication)) {
		    		return response;
		    		}else {
		    			logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"39 TITULAR DEL CONVENIO FALLECIO NO PUEDE RENOVAR EL CONVENIO", CONSULTA, authentication);
		    			response.setMensaje("39");
		    			throw new BadRequestException(HttpStatus.BAD_REQUEST, "TITULAR DEL CONVENIO FALLECIO NO PUEDE RENOVAR EL CONVENIO");
		    		}
		    	  }else {
		  			logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"36 EL CONVENIO NO SE ENCUENTRA EN PERIODO DE RENOVACION", CONSULTA, authentication);
		  			response.setMensaje("36");
		  			throw new BadRequestException(HttpStatus.BAD_REQUEST, "EL CONVENIO NO SE ENCUENTRA EN PERIODO DE RENOVACION");
				}
				}
			return response;    		
	}

	public Response<?> buscarConvenioAnterior(DatosRequest request, Authentication authentication) throws IOException {
		String datosJson = String.valueOf(request.getDatos().get("datos"));
		FiltrosConvenioPFRequest filtros = gson.fromJson(datosJson, FiltrosConvenioPFRequest .class);
		if(filtros.getNumeroContratante()==null && filtros.getNumeroConvenio()==null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta ");	
		}
		Response<?> response = providerRestTemplate.consumirServicio(renovarBean.buscarAnterior(request, filtros).getDatos(), urlConsulta + PATH_CONSULTA,
				authentication);
	      if(response.getDatos().toString().equals("[]")){
	    		logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"45 No se encontro informacion relacionada a tu busqueda " +filtros.getNumeroConvenio(), CONSULTA, authentication);
	    		response.setMensaje("45");
	      }else {
			    	  if(!validarVigenciaCtoAnterior(filtros.getNumeroConvenio(), authentication)) {
			    		if(!validarFallecidoCtoAnterior(filtros.getNumeroContratante(), authentication)) {
			    		return response;
			    		}else {
			    			logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"39 TITULAR DEL CONVENIO FALLECIO NO PUEDE RENOVAR EL CONVENIO", CONSULTA, authentication);
			    			response.setMensaje("39");
			    			throw new BadRequestException(HttpStatus.BAD_REQUEST, "TITULAR DEL CONVENIO FALLECIO NO PUEDE RENOVAR EL CONVENIO");
			    		}
			    	  }else {
			  			logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"36 EL CONVENIO NO SE ENCUENTRA EN PERIODO DE RENOVACION", CONSULTA, authentication);
			  			response.setMensaje("36");
			  			throw new BadRequestException(HttpStatus.BAD_REQUEST, "EL CONVENIO NO SE ENCUENTRA EN PERIODO DE RENOVACION");
					}
		}
		return response;
	}
	
	@Override
	public Response<?> renovarConvenio(DatosRequest request, Authentication authentication) throws IOException {
		Response<?> response;
		try {
			String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		    RenovarPlanPFRequest renovarRequest = gson.fromJson(datosJson, RenovarPlanPFRequest .class);	
			UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
			renovarBean = new RenovarBean(renovarRequest);
			renovarBean.setUsuarioModifica(usuarioDto.getIdUsuario());
			
				response = providerRestTemplate.consumirServicio(renovarBean.renovarPlan().getDatos(), urlConsulta + PATH_ACTUALIZAR,
						authentication);
				logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"Estatus OK", MODIFICACION, authentication);
				
					return response;						
		}catch (Exception e) {
			String consulta = renovarBean.renovarPlan().getDatos().get("query").toString();
			String encoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error("Error al ejecutar la query " +encoded);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"Fallo al ejecutar la query", MODIFICACION, authentication);
			throw new IOException("5", e.getCause()) ;
		}
	}

	private boolean validarFallecidoCtoAnterior(Integer numContratante, Authentication authentication) throws IOException {
		Response<?> response= providerRestTemplate.consumirServicio(renovarBean.validarFallecido(numContratante).getDatos(), urlConsulta + PATH_CONSULTA,
				authentication);
	Object rst=response.getDatos();
	return !rst.toString().equals("[]");
	}

	private boolean validarVigenciaCtoAnterior(Integer numConvenio, Authentication authentication) throws IOException {
		Response<?> response= providerRestTemplate.consumirServicio(renovarBean.validaVigCtoAnterior(numConvenio).getDatos(), urlConsulta + PATH_CONSULTA,
				authentication);
	Object rst=response.getDatos();
	log.info("-> " +rst.toString());
	return !rst.toString().equals("[]");
	}

	private boolean validarFallecido(String rfc, Authentication authentication) throws IOException {	
		Response<?> response= providerRestTemplate.consumirServicio(renovarBean.validarFallecido(rfc).getDatos(), urlConsulta + PATH_CONSULTA,
				authentication);
	Object rst=response.getDatos();
	return !rst.toString().equals("[]");
	}
	
	private boolean validarVigenciaCto(String folio, Authentication authentication) throws IOException {
		Response<?> response= providerRestTemplate.consumirServicio(renovarBean.validarVigencia(folio).getDatos(), urlConsulta + PATH_CONSULTA,
				authentication);
	Object rst=response.getDatos();
	log.info("-> " +rst.toString());
	return !rst.toString().equals("[]");
	}

	
	
}
