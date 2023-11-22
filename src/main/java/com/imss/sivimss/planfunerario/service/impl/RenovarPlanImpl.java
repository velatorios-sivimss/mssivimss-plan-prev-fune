package com.imss.sivimss.planfunerario.service.impl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.planfunerario.beans.RenovarBean;
import com.imss.sivimss.planfunerario.exception.BadRequestException;
import com.imss.sivimss.planfunerario.model.request.FiltrosConvenioPFRequest;
import com.imss.sivimss.planfunerario.model.request.RenovarPlanPFRequest;
import com.imss.sivimss.planfunerario.model.request.ReporteDto;
import com.imss.sivimss.planfunerario.model.response.DatosConvenioResponse;
import com.imss.sivimss.planfunerario.model.response.BenefResponse;
import com.imss.sivimss.planfunerario.model.request.UsuarioDto;
import com.imss.sivimss.planfunerario.model.request.VerificarDocumentacionRequest;
import com.imss.sivimss.planfunerario.service.PagosService;
import com.imss.sivimss.planfunerario.service.RenovarPlanService;
import com.imss.sivimss.planfunerario.util.AppConstantes;
import com.imss.sivimss.planfunerario.util.ConvertirGenerico;
import com.imss.sivimss.planfunerario.util.DatosRequest;
import com.imss.sivimss.planfunerario.util.LogUtil;
import com.imss.sivimss.planfunerario.util.MensajeResponseUtil;
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
	private static final String IMPRIMIR = "imprimir";
	private static final String INFORMACION_INCOMPLETA = "Informacion incompleta";
	private static final String EXITO = "EXITO";
	private static final String SIN_INFORMACION = "45 NO HAY INFORMACION RELACIONADA A TU BUSQUEDA";
	
	@Autowired
	private LogUtil logUtil;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private PagosService pagosService;
	
	@Value("${endpoints.rutas.dominio-consulta}")
	private String urlConsulta;
	@Value("${endpoints.rutas.dominio-crear}")
	private String urlCrear;
	@Value("${endpoints.rutas.dominio-crear-multiple}")
	private String urlCrearMultiple;
	@Value("${endpoints.rutas.dominio-insertar-multiple}")
	private String urlInsertarMultiple;
	@Value("${endpoints.rutas.dominio-actualizar}")
	private String urlActualizar;
	@Value("${endpoints.ms-reportes}")
	private String urlReportes;
	@Value("${formato-fecha}")
	private String fecFormat;

	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	Gson gson = new Gson();
	
	RenovarBean renovarBean = new RenovarBean();
	
	Integer anioMesVigencia = 0;
	
	@Override
	public Response<?> buscarConvenioNuevo(DatosRequest request, Authentication authentication) throws IOException {		 
		Response<?> response = new Response<>();
		List<DatosConvenioResponse> convenioResponse;
		List<BenefResponse> benefResponse;
		String datosJson = String.valueOf(request.getDatos().get("datos"));
		FiltrosConvenioPFRequest filtros = gson.fromJson(datosJson, FiltrosConvenioPFRequest .class);
		UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		if(filtros.getFolio()==null && filtros.getRfc()==null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, INFORMACION_INCOMPLETA);	
		}  
		filtros.setTipoPrevision(1);
		DatosConvenioResponse datosConvenio = new DatosConvenioResponse();
		try {
		Response<?>	responseDatosConvenio = providerRestTemplate.consumirServicio(renovarBean.buscarConvenio(request, filtros, fecFormat).getDatos(), urlConsulta,
					authentication);
			Object rst = responseDatosConvenio.getDatos();
		      if(rst.toString().equals("[]")){
		    		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),SIN_INFORMACION +filtros.getFolio(), CONSULTA, authentication);
		    		return devuelveVacio(response); 
		      }
		    	  convenioResponse = Arrays.asList(modelMapper.map(responseDatosConvenio.getDatos(), DatosConvenioResponse[].class));
			        benefResponse = Arrays.asList(modelMapper.map(providerRestTemplate.consumirServicio(renovarBean.buscarBeneficiarios(filtros.getFolio(), filtros.getNumeroConvenio()).getDatos(), urlConsulta, authentication).getDatos(), BenefResponse[].class));
			        datosConvenio = convenioResponse.get(0);
			        datosConvenio.setBeneficiarios(benefResponse);
			        if(filtros.getFolio()==null) {
			        	filtros.setFolio(datosConvenio.getFolio());
			        }
			        if(validarFallecido(filtros, authentication)) {
			        	if(!finadoVigencias(datosConvenio.getFecActual(), datosConvenio.getFecVigencia())){
							providerRestTemplate.consumirServicio(renovarBean.cambiarEstatusPlan(filtros.getFolio(), filtros.getNumeroConvenio(), usuarioDto.getIdUsuario()).getDatos(), urlActualizar, authentication);
							logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"OK CAMBIO DE ESTATUS A CERRADO", MODIFICACION, authentication);
						}
		    			logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"39 TITULAR DEL CONVENIO FALLECIO NO PUEDE RENOVAR EL CONVENIO", CONSULTA, authentication);
			  			return responseFallecido(response);
		    		  }
		    	  if(!validarPeriodoRenovacion(filtros, authentication)) {
		    		  logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"36 EL CONVENIO NO SE ENCUENTRA EN PERIODO DE RENOVACION", CONSULTA, authentication);
		    			return fueraPeriodo(response);
		    	  }
		    	  Integer vig = obtieneVigencia(datosConvenio.getFecVigencia());
		    	  if(getDia()>20 && getDia()>vig || anioMesActual()>anioMesVigencia) {		  
		    			  logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"OK CAMBIO DE ESTATUS A INHABILITADO", MODIFICACION, authentication);
		    		    	providerRestTemplate.consumirServicio(renovarBean.cambiarEstatusPlan(filtros.getFolio(), filtros.getNumeroConvenio() , usuarioDto.getIdUsuario()).getDatos(), urlActualizar,authentication);
		    		    	logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"36 CONVENIO INACTIVO ", CONSULTA, authentication);
		    		    	 log.info("ESTATUS INACTIVO: CONTRATO CERRADO"); 
		    		    	 return fueraPeriodo(response);
		    		    } 
		    	  if(datosConvenio.getEstatusConvenio()==2) {
		    		  response.setCodigo(200);
			            response.setError(false);
			            response.setMensaje("Exito");
				      response.setDatos(ConvertirGenerico.convertInstanceOfObject(datosConvenio));  
		    	  }else {
		    			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),SIN_INFORMACION +filtros.getFolio(), CONSULTA, authentication);
			          return devuelveVacio(response);
		    	  } 
	}catch(Exception e) {
		log.info("Fallo al e ejecutar la query {} ", e.getCause());
		throw new IOException("5", e.getCause()) ;
	}
		      return response;   
	
	}


	public Response<?> buscarConvenioAnterior(DatosRequest request, Authentication authentication) throws IOException {
		Response<?> response = new Response<>();
		List<DatosConvenioResponse> convenioResponse;
		List<BenefResponse> benefResponse;
		String datosJson = String.valueOf(request.getDatos().get("datos"));
		FiltrosConvenioPFRequest filtros = gson.fromJson(datosJson, FiltrosConvenioPFRequest .class);
		UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		if(filtros.getNumeroConvenio()==null && filtros.getNumeroContratante()==null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, INFORMACION_INCOMPLETA);	
		}
		filtros.setTipoPrevision(2);
		DatosConvenioResponse datosConvenio = new DatosConvenioResponse();
	try {
		 providerRestTemplate.consumirServicio(renovarBean.validarBeneficiarios(request, filtros.getNumeroConvenio(), filtros.getNumeroContratante(), usuarioDto.getIdUsuario()).getDatos(), urlActualizar,
  				authentication);

		 Response<?> responseDatosConvenio = providerRestTemplate.consumirServicio(renovarBean.buscarConvenio(request, filtros, fecFormat).getDatos(), urlConsulta,
				authentication);
	      if(responseDatosConvenio.getDatos().toString().equals("[]")){
	    		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),SIN_INFORMACION +filtros.getFolio(), CONSULTA, authentication);
		          return devuelveVacio(response);
	      }
	    	  
	      convenioResponse = Arrays.asList(modelMapper.map(responseDatosConvenio.getDatos(), DatosConvenioResponse[].class));
   		   benefResponse = Arrays.asList(modelMapper.map(providerRestTemplate.consumirServicio(renovarBean.buscarBeneficiarios(filtros.getFolio(),filtros.getNumeroConvenio()).getDatos(), urlConsulta, authentication).getDatos(), BenefResponse[].class));
   		   datosConvenio = convenioResponse.get(0);
   		   datosConvenio.setBeneficiarios(benefResponse);
   		if(filtros.getFolio()==null) {
        	filtros.setNumeroConvenio(datosConvenio.getIdConvenio());
        }
   		if(validarFallecido(filtros, authentication)) {
			
			if(!finadoVigencias(datosConvenio.getFecActual(), datosConvenio.getFecVigencia())){
				providerRestTemplate.consumirServicio(renovarBean.cambiarEstatusPlan(filtros.getFolio(), filtros.getNumeroConvenio(), usuarioDto.getIdUsuario()).getDatos(), urlActualizar, authentication);
				logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"OK CAMBIO DE ESTATUS A CERRADO", MODIFICACION, authentication);
			}
			
			logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"39 TITULAR DEL CONVENIO FALLECIO NO PUEDE RENOVAR EL CONVENIO", CONSULTA, authentication);
			return responseFallecido(response);
		}
	    	        logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"CAMBIO DE ESTATUS BENEFICIARIOS PLAN ANTERIOR " +filtros.getNumeroConvenio(), CONSULTA, authentication);
			    	  if(!validarPeriodoRenovacion(filtros ,authentication)) {
			    		  logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"36 EL CONVENIO NO SE ENCUENTRA EN PERIODO DE RENOVACION", CONSULTA, authentication);
			    		  return fueraPeriodo(response);
			    	  }
			    	  Integer vig = obtieneVigencia(datosConvenio.getFecVigencia());
			    		 if(getDia()>20 && getDia()>vig || anioMesActual()>anioMesVigencia) {
			    	         logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"OK CAMBIO DE ESTATUS A INHABILITADO", MODIFICACION, authentication);
			    			providerRestTemplate.consumirServicio(renovarBean.cambiarEstatusPlan(filtros.getFolio(), filtros.getNumeroConvenio(), usuarioDto.getIdUsuario()).getDatos(), urlActualizar, authentication);
			    			 logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"36 EL CONVENIO SE ENCUENTRA INACTIVO", CONSULTA, authentication);
			    			    log.info("ESTATUS INACTIVO: CONTRATO CERRADO");
			    			    return fueraPeriodo(response);
			    		}
			    		  if(datosConvenio.getEstatusConvenio()==2) {
				    		  response.setCodigo(200);
					            response.setError(false);
					            response.setMensaje("Exito");
						      response.setDatos(ConvertirGenerico.convertInstanceOfObject(datosConvenio));  
				    	  }else {
				    			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),SIN_INFORMACION +filtros.getFolio(), CONSULTA, authentication);
				    		  return devuelveVacio(response);
				    	  }
   }catch(Exception e) {
	   log.info("Fallo al e ejecutar la query {} ", e.getCause());
	   throw new IOException("5", e.getCause()) ;
   }
  
		return response;
	}
	
	@Override
	public Response<?> renovarConvenio(DatosRequest request, Authentication authentication) throws IOException {
		    Response<?> response = new Response<>();
			String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		    RenovarPlanPFRequest renovarRequest = gson.fromJson(datosJson, RenovarPlanPFRequest .class);	
			UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);	
			if ( renovarRequest.getIdConvenioPf() == null ||  renovarRequest.getIndRenovacion() ==null ) {
				throw new BadRequestException(HttpStatus.BAD_REQUEST, INFORMACION_INCOMPLETA);
			}
		try {
			renovarBean = new RenovarBean(renovarRequest);
			renovarBean.setUsuarioAlta(usuarioDto.getIdUsuario());
			Date dateF = new SimpleDateFormat("dd-MM-yyyy").parse(renovarRequest.getVigencia());
	        DateFormat anioMes = new SimpleDateFormat("yyyy-MM-dd", new Locale("es", "MX"));
	        DateFormat anioFormat = new SimpleDateFormat("yyyy", new Locale("es", "MX"));
	        String anio= anioFormat.format(dateF);
	        String fecha=anioMes.format(dateF);
	        log.info("-> "+fecha);
	        renovarBean.setVigencia(fecha);
		Integer contador = contadorRenovaciones(renovarRequest.getIdConvenioPf(), authentication) +1;	
				 String folioAdenda=buildFolio(contador,renovarRequest.getFolio());
				 renovarBean.setFolioAdenda(folioAdenda);
				 if(validaRenovaciones(renovarRequest.getIdConvenioPf(), anio, authentication)>=1) {
					 response.setCodigo(200);
					 response.setError(true);
					 response.setMensaje("5");
					 response.setDatos(null);
					 logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"NO SE PUEDE RENOVAR: YA CUENTA CON RENOVACION EN TRAMITE", ALTA, authentication);
					 return response;
				 }
				response = providerRestTemplate.consumirServicio(renovarBean.renovarPlan().getDatos(), urlCrear,
						authentication);
				logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"Estatus OK", ALTA, authentication);
				
				if(response.getCodigo()==200) {
					pagosService.insertar(renovarBean, response, authentication);
				}
				
				if(response.getCodigo()==200 && renovarRequest.getIndRenovacion()==0) {
					providerRestTemplate.consumirServicio(renovarBean.actualizarEstatusConvenio(renovarRequest.getIdConvenioPf()).getDatos(), urlActualizar,
							authentication);
				}else if(response.getCodigo()==200 && renovarRequest.getIndRenovacion()==1) {
					providerRestTemplate.consumirServicio(renovarBean.actualizarEstatusRenovacionConvenio(renovarRequest.getIdConvenioPf(), fecha).getDatos(), urlActualizar,
							authentication);
				}
				logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"CAMBIO DE ESTATUS REGISTRO CONVENIO OK", BAJA, authentication);
				response.setMensaje("192");	
				return response;						
		}catch (Exception e) {
			String consulta = renovarBean.renovarPlan().getDatos().get("query").toString();
			String encoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error("Error al ejecutar la query " +encoded);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"ERRO AL RENOVAR EL CONVENIO: Fallo al ejecutar la query", ALTA, authentication);
			throw new IOException("5", e.getCause()) ;
		}
	}

	@Override
	public Response<?> descargarAdendaRenovacionAnual(DatosRequest request, Authentication authentication) throws IOException {
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		ReporteDto reporteDto= gson.fromJson(datosJson, ReporteDto.class);
		Map<String, Object> envioDatos = new RenovarBean().generarAdendaAnual(reporteDto);
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"DESCARGA CORRECTA ANEXO B ADENDA DE RENOVACION ANUAL", IMPRIMIR, authentication);
		return providerRestTemplate.consumirServicioReportes(envioDatos, urlReportes ,
				authentication);		
	}

	@Override
	public Response<?> descargarConvenioPlanAnterior(DatosRequest request, Authentication authentication) throws IOException {
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		ReporteDto reporteDto= gson.fromJson(datosJson, ReporteDto.class);
		Response<?> response = providerRestTemplate.consumirServicio(renovarBean.obtieneCostoRenovacion(reporteDto.getIdConvenio()).getDatos(), urlConsulta, authentication);
		MensajeResponseUtil.mensajeConsultaResponse(response, EXITO);
		reporteDto.setCostoRenovacion(recuperaDato(response.getDatos().toString()));
		Map<String, Object> envioDatos = new RenovarBean().generarConvenioAnterior(reporteDto);
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"DESCARGA CORRECTA PLANTILLA CONVENIO RENOVACION PLAN ANTERIOR", IMPRIMIR, authentication);
		return providerRestTemplate.consumirServicioReportes(envioDatos, urlReportes ,
				authentication);
	}


	@Override
	public Response<?> descargarHojaAfiliacion(DatosRequest request, Authentication authentication) throws IOException {
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		ReporteDto reporteDto= gson.fromJson(datosJson, ReporteDto.class);
		Map<String, Object> envioDatos = new RenovarBean().generarHojaAfiliacion(reporteDto);
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"DESCARGA CORRECTA PLANTILLA HOJA DE AFILIACION", IMPRIMIR, authentication);
		return providerRestTemplate.consumirServicioReportes(envioDatos, urlReportes ,
				authentication);
	}

	@Override
	public Response<?> verificarDocumentacion(DatosRequest request, Authentication authentication) throws IOException {
		Response<?> response;
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		VerificarDocumentacionRequest verificarDoc = gson.fromJson(datosJson, VerificarDocumentacionRequest.class);	
		String path="";
		try {
			UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
			renovarBean.setUsuarioAlta(usuarioDto.getIdUsuario());
			if(verificarDoc.getIdValidacionDoc()==null) {
				path = urlCrearMultiple;
			}else {
				path = urlInsertarMultiple;
			}
			response = providerRestTemplate.consumirServicio(renovarBean.actualizarDocumentacion(verificarDoc).getDatos(), path, authentication);
				logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"Se actualizo correctamente la documentacion requerida", MODIFICACION, authentication);
				return response;						
		}catch (Exception e) {
			String consulta = renovarBean.actualizarDocumentacion(verificarDoc).getDatos().get("query").toString();
			String encoded = new String(DatatypeConverter.parseBase64Binary(consulta));
			log.error("Error al ejecutar la query " +encoded);
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"ERROR AL ACTUALIZAR LA DOCUMENTACION REQUERIDA: Fallo al ejecutar la query", ALTA, authentication);
			throw new IOException("5", e.getCause());
		}
	}
	
	private boolean validarFallecido(FiltrosConvenioPFRequest filtros, Authentication authentication) throws IOException {	
		Response<?> response= providerRestTemplate.consumirServicio(renovarBean.validarFallecido(filtros).getDatos(), urlConsulta,
				authentication);
		MensajeResponseUtil.mensajeConsultaResponse(response, EXITO);
	Object rst=response.getDatos();
	return !rst.toString().equals("[]");
	}
	
	private int validaRenovaciones(Integer idConvenioPf, String anio, Authentication authentication) throws IOException {
		Response<?> response = providerRestTemplate.consumirServicio(renovarBean.validaRenovacion(idConvenioPf, anio).getDatos(), urlConsulta,
				authentication);
		MensajeResponseUtil.mensajeConsultaResponse(response, EXITO);
		return recuperaDato(response.getDatos().toString());
	}


	private int contadorRenovaciones(Integer idConvenioPf, Authentication authentication) throws IOException {
		Response<?> response = providerRestTemplate.consumirServicio(renovarBean.contador(idConvenioPf).getDatos(), urlConsulta,authentication);
		MensajeResponseUtil.mensajeConsultaResponse(response, EXITO);
		return recuperaDato(response.getDatos().toString());
	}
	
	private int recuperaDato(String respuesta) {
		Integer contador = 0;
		Pattern pattern = Pattern.compile("c=(\\d+)");
		Matcher matcher = pattern.matcher(respuesta);
		if (matcher.find()) {
		    contador = Integer.parseInt(matcher.group(1));
		}
		log.info("validacion ->" +contador);
		return contador;
	}


	private String buildFolio(Integer contador, String folio) {
	    String formatearConvenioCeros = String.format("%02d", contador);
		return folio+"-"+formatearConvenioCeros;
	}

	
	private int getDia() {
	SimpleDateFormat sdf = new SimpleDateFormat("dd");
		String date = sdf.format(new Date());
		return Integer.parseInt(date);
	}
	
	private Integer anioMesActual() {
	    SimpleDateFormat sdfMes = new SimpleDateFormat("yyyyMM");
		String date = sdfMes.format(new Date());
		return Integer.parseInt(date);
	}
	
	private boolean validarPeriodoRenovacion(FiltrosConvenioPFRequest filtros, Authentication authentication) throws IOException {
		Response<?> response= providerRestTemplate.consumirServicio(renovarBean.validarPeriodo(filtros).getDatos(), urlConsulta,
				authentication);
	MensajeResponseUtil.mensajeConsultaResponse(response, EXITO);
	Object rst=response.getDatos();
	String fec = rst.toString();
	anioMesVigencia = recuperaDato(fec);
	return !rst.toString().equals("[]");
	}

	private Response<?> devuelveVacio(Response<?> response) {
		response.setCodigo(200);
        response.setError(false);
        response.setMensaje("45");
		return response;
	}
	
	private Response<?> fueraPeriodo(Response<?> response) {
		 response.setCodigo(200);
        response.setError(false);
        response.setMensaje("36");
		return response;
	}
	
	private Response<?> responseFallecido(Response<?> response) {
		 response.setCodigo(200);
        response.setError(false);
			response.setDatos(null);
			 response.setMensaje("39");
			 return response;
	}
	
	private Integer obtieneVigencia(String fecVigencia) throws ParseException {
		Date sdf = new SimpleDateFormat("dd/MM/yyyy").parse(fecVigencia);
		  DateFormat fechaFormateada = new SimpleDateFormat("dd");
		  String fechaVig = fechaFormateada.format(sdf);
		return  Integer.parseInt(fechaVig);
	}
	
	private boolean finadoVigencias(Date fecActual, String fecVigencia) throws ParseException {
		SimpleDateFormat formatter;
		formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date vigFin = formatter.parse(fecVigencia); 
		log.info("vigencia "+ vigFin);
		log.info("actual "+ fecActual);
		 return !vigFin.before(fecActual);
	}

}
