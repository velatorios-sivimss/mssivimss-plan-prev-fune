package com.imss.sivimss.planfunerario.service.impl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.planfunerario.beans.RenovarBean;
import com.imss.sivimss.planfunerario.model.request.FiltrosConvenioPFRequest;
import com.imss.sivimss.planfunerario.model.request.RenovarPlanPFRequest;
import com.imss.sivimss.planfunerario.model.request.ReporteDto;
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
	 private static final String PATH_CREAR="generico/crear";
	 private static final String PATH_ACTUALIZAR="generico/actualizar";
	 
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	Gson gson = new Gson();
	
	RenovarBean renovarBean = new RenovarBean();
	
	
	
	@Override
	public Response<?> buscarConvenioNuevo(DatosRequest request, Authentication authentication) throws IOException {		 
		String datosJson = String.valueOf(request.getDatos().get("datos"));
		FiltrosConvenioPFRequest filtros = gson.fromJson(datosJson, FiltrosConvenioPFRequest .class);
	//	UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
			Response<?> response = providerRestTemplate.consumirServicio(renovarBean.buscarNuevo(request, filtros).getDatos(), urlConsulta + PATH_CONSULTA,
					authentication);
			Object rst = response.getDatos();
		      if(rst.toString().equals("[]")){
		    		logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"45 No se encontro informacion relacionada a tu busqueda " +filtros.getFolio(), CONSULTA, authentication);
		    		response.setMensaje("45");
		      }else {
		    	  if(!validarPeriodoRenovacion(filtros, authentication)) {
		    		  logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"36 EL CONVENIO NO SE ENCUENTRA EN PERIODO DE RENOVACION", CONSULTA, authentication);
		    		  response.setMensaje("36");
		    			response.setDatos(null);
		    			return response;
			  			//throw new BadRequestException(HttpStatus.BAD_REQUEST, "EL CONVENIO NO SE ENCUENTRA EN PERIODO DE RENOVACION");
		    	  }
		    		// if(!validarVigencia(filtros, authentication)) {
		    	  if(fechaHoy()>31) {
		    		    	logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"36 CONVENIO INACTIVO", CONSULTA, authentication);
		  				  //	 providerRestTemplate.consumirServicio(renovarBean.cambiarEstatusPlan(filtros.getFolio(), usuarioDto.getIdUsuario()).getDatos(), urlConsulta + PATH_ACTUALIZAR,authentication);
		    		    	response.setMensaje("36 CONVENIO INACTIVO");
				  			response.setDatos(null);
				  			return response;
		    		    } 
		    		    	
		    		if(validarFallecido(filtros, authentication)) {
		    			logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"39 TITULAR DEL CONVENIO FALLECIO NO PUEDE RENOVAR EL CONVENIO", CONSULTA, authentication);
		    			response.setMensaje("39");
			  			response.setDatos(null);
			  			return response;
		    		  }
				}
			return response;    		
	}

	public Response<?> buscarConvenioAnterior(DatosRequest request, Authentication authentication) throws IOException {
		String datosJson = String.valueOf(request.getDatos().get("datos"));
		FiltrosConvenioPFRequest filtros = gson.fromJson(datosJson, FiltrosConvenioPFRequest .class);
		UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
	//	if(filtros.getNumeroContratante()==null && filtros.getNumeroConvenio()==null) {
		//	throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta ");	
	//	}
		Response<?> response = providerRestTemplate.consumirServicio(renovarBean.buscarAnterior(request, filtros).getDatos(), urlConsulta + PATH_CONSULTA,
				authentication);
	      if(response.getDatos().toString().equals("[]")){
	    		logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"45 No se encontro informacion relacionada a tu busqueda " +filtros.getNumeroConvenio(), CONSULTA, authentication);
	    		response.setMensaje("45");
	      }else {
			    	  if(!validarPeriodoCtoAnterior(filtros.getNumeroContratante(), filtros.getNumeroConvenio(), authentication)) {
			    		  logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"36 EL CONVENIO NO SE ENCUENTRA EN PERIODO DE RENOVACION", CONSULTA, authentication);
			    		    response.setMensaje("36");
			    			response.setDatos(null);
			    			return response;
				  			//throw new BadRequestException(HttpStatus.BAD_REQUEST, "EL CONVENIO NO SE ENCUENTRA EN PERIODO DE RENOVACION");
			    	  }
			    		//if(!validarVigenciaCtoAnterior(filtros.getNumeroContratante(), filtros.getNumeroConvenio(), authentication)) {
			    		 if(fechaHoy()>31) {
			    	         logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"OK CAMBIO DE ESTATUS A INHABILITADO", MODIFICACION, authentication);
			    			 providerRestTemplate.consumirServicio(renovarBean.cambiarEstatusPlanAnterior(filtros.getNumeroContratante(), filtros.getNumeroConvenio(), usuarioDto.getIdUsuario()).getDatos(), urlConsulta + PATH_ACTUALIZAR,
					 					authentication);
			    			 logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"36 EL CONVENIO SE ENCUENTRA INACTIVO", CONSULTA, authentication);
			    			    response.setMensaje("36 CONVENIO INACTIVO");
			    				response.setDatos(null);
			    				return response;
			    			 //throw new BadRequestException(HttpStatus.BAD_REQUEST, "EL CONVENIO SE ENCUENTRA INACTIVO"); 
			    		}
			    		  if(validarFallecidoCtoAnterior(filtros.getNumeroContratante(),filtros.getNumeroConvenio(), authentication)) {
			    			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"OK CAMBIO DE ESTATUS A CERRADO", MODIFICACION, authentication);
			    			 providerRestTemplate.consumirServicio(renovarBean.cambiarEstatusACerrado(filtros.getNumeroContratante(), filtros.getNumeroConvenio(), usuarioDto.getIdUsuario()).getDatos(), urlConsulta + PATH_ACTUALIZAR,
					 					authentication);
			    			logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"39 TITULAR DEL CONVENIO FALLECIO NO PUEDE RENOVAR EL CONVENIO", CONSULTA, authentication);
			    			response.setMensaje("39");
			    			response.setDatos(null);
			    			return response;
			    			//throw new BadRequestException(HttpStatus.BAD_REQUEST, "TITULAR DEL CONVENIO FALLECIO NO PUEDE RENOVAR EL CONVENIO");
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
			renovarBean.setUsuarioAlta(usuarioDto.getIdUsuario());
			Date dateF = new SimpleDateFormat("dd-MM-yyyy").parse(renovarRequest.getVigencia());
	        DateFormat anioMes = new SimpleDateFormat("yyyy-MM-dd", new Locale("es", "MX"));
	        String fecha=anioMes.format(dateF);
	        log.info("-> "+fecha);
	        renovarBean.setVigencia(fecha);
			String velatorio= renovarRequest.getVelatorio().substring(0,3).toUpperCase();
		Integer contador = contadorRenovaciones(renovarRequest.getIdConvenioPf(), authentication);
	//Integer contador=0;
	Integer folio=101+contador;	
				 String folioAdenda=buildFolio(velatorio,folio);
				 renovarBean.setFolioAdenda(folioAdenda);
				    log.info("->" +folioAdenda);
			
				  
			
				response = providerRestTemplate.consumirServicio(renovarBean.renovarPlan().getDatos(), urlConsulta + PATH_CREAR,
						authentication);
				logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"Estatus OK", ALTA, authentication);
				if(response.getCodigo()==200 && renovarRequest.getIndRenovacion()==0) {
					providerRestTemplate.consumirServicio(renovarBean.actualizarEstatusConvenio(renovarRequest.getIdConvenioPf()).getDatos(), urlConsulta + PATH_ACTUALIZAR,
							authentication);
				}else if(response.getCodigo()==200 && renovarRequest.getIndRenovacion()==1) {
					providerRestTemplate.consumirServicio(renovarBean.actualizarEstatusRenovacionConvenio(renovarRequest.getIdConvenioPf(), fecha).getDatos(), urlConsulta + PATH_ACTUALIZAR,
							authentication);
				}
					return response;						
		}catch (Exception e) {
			String consulta = renovarBean.renovarPlan().getDatos().get("query").toString();
			String encoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error("Error al ejecutar la query " +encoded);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"ERRO AL RENOVAR EL CONVENIO: Fallo al ejecutar la query", ALTA, authentication);
			throw new IOException("5", e.getCause()) ;
		}
	}

	private int contadorRenovaciones(Integer idConvenioPf, Authentication authentication) throws IOException {
		Response<?> response = providerRestTemplate.consumirServicio(renovarBean.contador(idConvenioPf).getDatos(), urlConsulta + PATH_CONSULTA,
				authentication);
		
		String resultado=response.getDatos().toString();
		Integer contador = 0;
	
		Pattern pattern = Pattern.compile("COUNT\\(\\*\\)=(\\d+)");
		Matcher matcher = pattern.matcher(resultado);
		if (matcher.find()) {
		    contador = Integer.parseInt(matcher.group(1));
		}
		log.info("contador{} ", contador);
		return contador;
	}



	private String buildFolio(String velatorio, Integer folio) {
	    String formatearConvenioCeros = String.format("%08d", folio);
	    String folioConvenio= formatearConvenioCeros.substring(0,6);
	    log.info("-> "+folioConvenio);
	    String formatearnumConvenio = formatearConvenioCeros.substring(6,8);
		return velatorio +"-"+folioConvenio+"-"+formatearnumConvenio;
	}



	private boolean validarFallecidoCtoAnterior(Integer numContratante, Integer numConvenio, Authentication authentication) throws IOException {
		Response<?> response= providerRestTemplate.consumirServicio(renovarBean.validarFallecido(numContratante, numConvenio).getDatos(), urlConsulta + PATH_CONSULTA,
				authentication);
	Object rst=response.getDatos();
	return !rst.toString().equals("[]");
	}

	private boolean validarPeriodoCtoAnterior(Integer numContratante, Integer numConvenio, Authentication authentication) throws IOException {
		Response<?> response= providerRestTemplate.consumirServicio(renovarBean.validaPeriodoCtoAnterior(numContratante, numConvenio).getDatos(), urlConsulta + PATH_CONSULTA,
				authentication);
	Object rst=response.getDatos();
	log.info("-> " +rst.toString());
	return !rst.toString().equals("[]");
	}

	private boolean validarFallecido(FiltrosConvenioPFRequest filtros, Authentication authentication) throws IOException {	
		Response<?> response= providerRestTemplate.consumirServicio(renovarBean.validarFallecido(filtros).getDatos(), urlConsulta + PATH_CONSULTA,
				authentication);
	Object rst=response.getDatos();
	return !rst.toString().equals("[]");
	}
	
	private boolean validarPeriodoRenovacion(FiltrosConvenioPFRequest filtros, Authentication authentication) throws IOException {
		Response<?> response= providerRestTemplate.consumirServicio(renovarBean.validarPeriodo(filtros).getDatos(), urlConsulta + PATH_CONSULTA,
				authentication);
	Object rst=response.getDatos();
	log.info("-> " +rst.toString());
	return !rst.toString().equals("[]");
	}

/*	private boolean validarVigencia(FiltrosConvenioPFRequest filtros, Authentication authentication) throws IOException {
		Response<?> response= providerRestTemplate.consumirServicio(renovarBean.validaVigencia(filtros).getDatos(), urlConsulta + PATH_CONSULTA,
				authentication);
	Object rst=response.getDatos();
	log.info("-> " +rst.toString());
	return !rst.toString().equals("[]");
	} 
	
	private boolean validarVigenciaCtoAnterior(Integer numContratante, Integer numConvenio,
			Authentication authentication) throws IOException {
		Response<?> response= providerRestTemplate.consumirServicio(renovarBean.validarVigenciaCtoAnterior(numContratante, numConvenio).getDatos(), urlConsulta + PATH_CONSULTA,
				authentication);
	Object rst=response.getDatos();
	log.info("-> " +rst.toString());
	return !rst.toString().equals("[]");
	} */
	
	private int fechaHoy() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		String date = sdf.format(new Date());
		return Integer.parseInt(date);
	}


	@Override
	public Response<?> descargarAdendaRenovacionAnual(DatosRequest request, Authentication authentication) throws IOException {
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		ReporteDto reporteDto= gson.fromJson(datosJson, ReporteDto.class);
		Map<String, Object> envioDatos = new RenovarBean().generarAdendaAnual(reporteDto);
		return providerRestTemplate.consumirServicioReportes(envioDatos, urlReportes ,
				authentication);
	}


	@Override
	public Response<?> descargarConvenioPlanAnterior(DatosRequest request, Authentication authentication) throws IOException {
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		ReporteDto reporteDto= gson.fromJson(datosJson, ReporteDto.class);
		Map<String, Object> envioDatos = new RenovarBean().generarConvenioAnterior(reporteDto);
		return providerRestTemplate.consumirServicioReportes(envioDatos, urlReportes ,
				authentication);
	}


	@Override
	public Response<?> descargarHojaAfiliacion(DatosRequest request, Authentication authentication) throws IOException {
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		ReporteDto reporteDto= gson.fromJson(datosJson, ReporteDto.class);
		Map<String, Object> envioDatos = new RenovarBean().generarHojaAfiliacion(reporteDto);
		return providerRestTemplate.consumirServicioReportes(envioDatos, urlReportes ,
				authentication);
	}

}
