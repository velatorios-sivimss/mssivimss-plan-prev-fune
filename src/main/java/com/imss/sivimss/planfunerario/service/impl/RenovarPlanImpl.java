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
		    		logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"45 No se encontro informacion relacionada a tu busqueda " +filtros.getFolio(), CONSULTA, authentication);
		    		  response.setCodigo(200);
			          response.setError(false);
			          response.setMensaje("45");
			          return response;
		      }
		    	  convenioResponse = Arrays.asList(modelMapper.map(responseDatosConvenio.getDatos(), DatosConvenioResponse[].class));
			        benefResponse = Arrays.asList(modelMapper.map(providerRestTemplate.consumirServicio(renovarBean.buscarBeneficiarios(filtros.getFolio(), filtros.getNumeroConvenio()).getDatos(), urlConsulta, authentication).getDatos(), BenefResponse[].class));
			        datosConvenio = convenioResponse.get(0);
			        datosConvenio.setBeneficiarios(benefResponse);
			        if(filtros.getFolio()==null) {
			        	filtros.setFolio(datosConvenio.getFolio());
			        }
		    	  if(!validarPeriodoRenovacion(filtros, authentication)) {
		    		  logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"36 EL CONVENIO NO SE ENCUENTRA EN PERIODO DE RENOVACION", CONSULTA, authentication);
		    		  response.setCodigo(200);
			          response.setError(false);
		    		  response.setMensaje("36");
		    			return response;
		    	  }
		    	  Integer vig = obtieneVigencia(datosConvenio.getFecVigencia());
		    
		    	  if(getDia()>20 && getDia()>vig || anioMesActual()>anioMesVigencia) {		  
		    			  logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"OK CAMBIO DE ESTATUS A INHABILITADO", MODIFICACION, authentication);
		    		    	providerRestTemplate.consumirServicio(renovarBean.cambiarEstatusPlan(filtros.getFolio(), filtros.getNumeroConvenio() , usuarioDto.getIdUsuario()).getDatos(), urlActualizar,authentication);
		    		    	logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"36 CONVENIO INACTIVO ", CONSULTA, authentication);
		    		    	 response.setCodigo(200);
					          response.setError(false);
					          response.setMensaje("36");
				  			return response;
		    		    } 
		    		if(validarFallecido(filtros, authentication)) {
		    			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"OK CAMBIO DE ESTATUS A CERRADO", MODIFICACION, authentication);
		    			providerRestTemplate.consumirServicio(renovarBean.cambiarEstatusPlan(filtros.getFolio(), filtros.getNumeroConvenio(), usuarioDto.getIdUsuario()).getDatos(), urlActualizar, authentication);
		    			logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"39 TITULAR DEL CONVENIO FALLECIO NO PUEDE RENOVAR EL CONVENIO", CONSULTA, authentication);
		    			 response.setCodigo(200);
				          response.setError(false);
			  			response.setDatos(null);
			  			 response.setMensaje("39");
			  			return response;
		    		  }
		    		response.setCodigo(200);
		            response.setError(false);
		            response.setMensaje("Exito");
			      response.setDatos(ConvertirGenerico.convertInstanceOfObject(datosConvenio));
	}catch(Exception e) {
		log.info("estoy aqui" +e.getCause());
		e.getCause();
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
	    		logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"45 No se encontro informacion relacionada a tu busqueda " +filtros.getNumeroConvenio(), CONSULTA, authentication);
	    		response.setMensaje("45 " +filtros.getNumeroConvenio());
	    		  response.setCodigo(200);
		          response.setError(false);
		          response.setMensaje("45");
		          return response;
	      }
	    	  
	      convenioResponse = Arrays.asList(modelMapper.map(responseDatosConvenio.getDatos(), DatosConvenioResponse[].class));
   		   benefResponse = Arrays.asList(modelMapper.map(providerRestTemplate.consumirServicio(renovarBean.buscarBeneficiarios(filtros.getFolio(),filtros.getNumeroConvenio()).getDatos(), urlConsulta, authentication).getDatos(), BenefResponse[].class));
   		   datosConvenio = convenioResponse.get(0);
   		   datosConvenio.setBeneficiarios(benefResponse);
   		  if(filtros.getNumeroConvenio()==null) {
	        	filtros.setNumeroConvenio(datosConvenio.getIdConvenio());
	        }
	    	        logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"CAMBIO DE ESTATUS BENEFICIARIOS PLAN ANTERIOR " +filtros.getNumeroConvenio(), CONSULTA, authentication);
			    	  if(!validarPeriodoRenovacion(filtros ,authentication)) {
			    		  logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"36 EL CONVENIO NO SE ENCUENTRA EN PERIODO DE RENOVACION", CONSULTA, authentication);
			    		  response.setCodigo(200);
				          response.setError(false);
			    		  response.setMensaje("36");
			    			return response;
			    	  }
			    	  Integer vig = obtieneVigencia(datosConvenio.getFecVigencia());
			    		 if(getDia()>20 && getDia()>vig || anioMesActual()>anioMesVigencia) {
			    	         logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"OK CAMBIO DE ESTATUS A INHABILITADO", MODIFICACION, authentication);
			    			providerRestTemplate.consumirServicio(renovarBean.cambiarEstatusPlan(filtros.getFolio(), filtros.getNumeroConvenio(), usuarioDto.getIdUsuario()).getDatos(), urlActualizar, authentication);
			    			 logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"36 EL CONVENIO SE ENCUENTRA INACTIVO", CONSULTA, authentication);
			    			    response.setCodigo(200);
						        response.setError(false);
						        response.setMensaje("36");
					    	     return response;
			    		}
			    		  if(validarFallecido(filtros, authentication)) {
			    			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"OK CAMBIO DE ESTATUS A CERRADO", MODIFICACION, authentication);
			    			providerRestTemplate.consumirServicio(renovarBean.cambiarEstatusPlan(filtros.getFolio(), filtros.getNumeroConvenio(), usuarioDto.getIdUsuario()).getDatos(), urlActualizar, authentication);
			    			logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"39 TITULAR DEL CONVENIO FALLECIO NO PUEDE RENOVAR EL CONVENIO", CONSULTA, authentication);
			    			response.setCodigo(200);
					        response.setError(false);
			    			response.setMensaje("39");
			    			return response;
			    		}
			    		   response.setCodigo(200);
			    		   response.setError(false);
			    		   response.setMensaje("Exito");
			    		 response.setDatos(ConvertirGenerico.convertInstanceOfObject(datosConvenio));
   }catch(Exception e) {
	   e.getCause();
   }
  
		return response;
	}
	
	@Override
	public Response<?> renovarConvenio(DatosRequest request, Authentication authentication) throws IOException {
		    Response<?> response;
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
	        String fecha=anioMes.format(dateF);
	        log.info("-> "+fecha);
	        renovarBean.setVigencia(fecha);
		Integer contador = contadorRenovaciones(renovarRequest.getIdConvenioPf(), authentication) +1;	
				 String folioAdenda=buildFolio(contador,renovarRequest.getFolio());
				 renovarBean.setFolioAdenda(folioAdenda);
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
		Response<?> response = providerRestTemplate.consumirServicio(renovarBean.contador(idConvenioPf).getDatos(), urlConsulta,
				authentication);
		
		String resultado=response.getDatos().toString();
		Integer contador = 0;
	
		Pattern pattern = Pattern.compile("c=(\\d+)");
		Matcher matcher = pattern.matcher(resultado);
		if (matcher.find()) {
		    contador = Integer.parseInt(matcher.group(1));
		}
		return contador;
	}



	private String buildFolio(Integer contador, String folio) {
	    String formatearConvenioCeros = String.format("%02d", contador);
		return folio+"-"+formatearConvenioCeros;
	}



/*	private boolean validarFallecidoCtoAnterior(Integer numContratante, Integer numConvenio, Authentication authentication) throws IOException {
		Response<?> response= providerRestTemplate.consumirServicio(renovarBean.validarFallecidoCtoAnterior(numContratante, numConvenio).getDatos(), urlConsulta,
				authentication);
	Object rst=response.getDatos();
	return !rst.toString().equals("[]");
	} */

	private boolean validarFallecido(FiltrosConvenioPFRequest filtros, Authentication authentication) throws IOException {	
		Response<?> response= providerRestTemplate.consumirServicio(renovarBean.validarFallecido(filtros).getDatos(), urlConsulta,
				authentication);
	Object rst=response.getDatos();
	return !rst.toString().equals("[]");
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
	if (verificarDoc.getIdValidacionDoc()==null) {
		throw new BadRequestException(HttpStatus.BAD_REQUEST, INFORMACION_INCOMPLETA);		
	}
		try {
			UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
			renovarBean.setUsuarioAlta(usuarioDto.getIdUsuario());
			response = providerRestTemplate.consumirServicio(renovarBean.actualizarDocumentacion(verificarDoc).getDatos(), urlInsertarMultiple, authentication);
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
	Object rst=response.getDatos();
	String fec = rst.toString();
	obtieneMesVigencia(fec);
	return !rst.toString().equals("[]");
	}
	

	private void obtieneMesVigencia(String fec) {
		Integer vigencia = 0;
		Pattern pattern = Pattern.compile("vig=(\\d+)");
		Matcher matcher = pattern.matcher(fec);
		if (matcher.find()) {
		    vigencia = Integer.parseInt(matcher.group(1));
		}
		log.info("-> " +vigencia);
		anioMesVigencia = vigencia;
	}
	
	private Integer obtieneVigencia(String fecVigencia) throws ParseException {
		Date sdf = new SimpleDateFormat("dd/MM/yyyy").parse(fecVigencia);
		  DateFormat fechaFormateada = new SimpleDateFormat("dd");
		  String fechaVig = fechaFormateada.format(sdf);
		return  Integer.parseInt(fechaVig);
	}

}
