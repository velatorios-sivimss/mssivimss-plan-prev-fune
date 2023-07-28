package com.imss.sivimss.planfunerario.beans;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.ibm.icu.text.RuleBasedNumberFormat;
import com.imss.sivimss.planfunerario.model.RenovarDocumentacionModel;
import com.imss.sivimss.planfunerario.model.request.FiltrosConvenioPFRequest;
import com.imss.sivimss.planfunerario.model.request.RenovarPlanPFRequest;
import com.imss.sivimss.planfunerario.model.request.ReporteDto;
import com.imss.sivimss.planfunerario.model.request.VerificarDocumentacionRequest;
import com.imss.sivimss.planfunerario.util.AppConstantes;
import com.imss.sivimss.planfunerario.util.DatosRequest;
import com.imss.sivimss.planfunerario.util.QueryHelper;
import com.imss.sivimss.planfunerario.util.SelectQueryUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class RenovarBean {
	
	private String datosBancarios;
	private Integer idConvenioPf;
	private Integer usuarioAlta;
	private String vigencia;
	private String folioAdenda;
	
	public RenovarBean(RenovarPlanPFRequest renovarRequest) {
		this.datosBancarios = renovarRequest.getDatosBancarios();
		this.idConvenioPf = renovarRequest.getIdConvenioPf();
		this.vigencia = renovarRequest.getVigencia();
	}
	
	//Tablas
	public static final String SVT_RENOVACION_CONVENIO_PF = "SVT_RENOVACION_CONVENIO_PF RPF";
	public static final String SVC_PERSONA = "SVC_PERSONA SP";
	public static final String SVT_CONTRATANTE_PAQUETE_CONVENIO_PF = "SVT_CONTRATANTE_PAQUETE_CONVENIO_PF SCPC";
	public static final String SVC_CONTRATANTE = "SVC_CONTRATANTE SC";
	public static final String SVT_CONVENIO_PF = "SVT_CONVENIO_PF SPF";
	public static final String UPDATE_SVT_CONVENIO_PF = "UPDATE SVT_CONVENIO_PF";
	public static final String SCPC_ID_CONTRATANTE_SC_ID_CONTRATANTE ="SCPC.ID_CONTRATANTE = SC.ID_CONTRATANTE";
	                                                                                                                                
	//Parameters
	public static final String DES_FOLIO = "desFolio";
	public static final String CVE_RFC = "cveRfc";
	public static final String NUM_INE = "numIne";
	public static final String ID_CONVENIO = "idConvenio";
	public static final String ID_CONVENIO_PF = "ID_CONVENIO_PF";
	
	public DatosRequest buscarConvenio(DatosRequest request, FiltrosConvenioPFRequest filtros, String fecFormat ) {
		Map<String, Object> parametros = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("SCP.DES_FOLIO AS folio",
				"SCP.ID_CONVENIO_PF AS idConvenio",
				 "SP.CVE_RFC AS rfc",
				 "SC.CVE_MATRICULA AS matricula",
				 "SP.NOM_PERSONA AS nomContratante",
				 "SP.NOM_PRIMER_APELLIDO AS primerApellido",
				 "SP.NOM_SEGUNDO_APELLIDO AS segundoApellido",
				 "SCP.ID_TIPO_PREVISION AS idTipoPrevision",
				 "IF(SCP.ID_TIPO_PREVISION=1, 'Previsión funeraria plan nuevo', 'Previsión funeraria plan anterior') AS tipoPrevision",
				 "SCPC.ID_PAQUETE AS idPaquete",
				 "PAQ.DES_PAQUETE AS tipoPaquete",
				 "SCP.ID_ESTATUS_CONVENIO AS estatusConvenio",
				 "IF(SCP.IND_RENOVACION=0, (DATE_FORMAT(SCP.FEC_INICIO, '"+fecFormat+"')), DATE_FORMAT(RPF.FEC_INICIO, '"+fecFormat+"')) AS fecInicio",
				 "IF(SCP.IND_RENOVACION=0, (DATE_FORMAT(SCP.FEC_VIGENCIA, '"+fecFormat+"')), DATE_FORMAT(RPF.FEC_VIGENCIA, '"+fecFormat+"')) AS fecVigencia",
				 "SD.DES_CALLE AS calle",
				 "SD.NUM_EXTERIOR AS numExterior",
				 "SD.NUM_INTERIOR AS numInterior",
				 "SD.DES_CP AS cp",
				 "IFNULL(CP.DES_ESTADO, SD.DES_ESTADO) AS estado",
				 "IFNULL(CP.DES_MNPIO, SD.DES_MUNICIPIO) AS municipio",
				 "SP.DES_TELEFONO AS tel",
				 "SP.DES_CORREO AS correo",
		 "PAQ.MON_COSTO_REFERENCIA AS costoRenovacion",
		 "SCP.IND_RENOVACION AS indRenovacion")
		.from("SVT_CONVENIO_PF SCP")
		.leftJoin(SVT_RENOVACION_CONVENIO_PF, "SCP.ID_CONVENIO_PF=RPF.ID_CONVENIO_PF AND RPF.IND_ESTATUS=1")
		.join(SVT_CONTRATANTE_PAQUETE_CONVENIO_PF, "SCP.ID_CONVENIO_PF = SCPC.ID_CONVENIO_PF")
		.join("SVT_PAQUETE PAQ", "SCPC.ID_PAQUETE = PAQ.ID_PAQUETE")
		.join(SVC_CONTRATANTE, SCPC_ID_CONTRATANTE_SC_ID_CONTRATANTE)
		.join("SVT_DOMICILIO SD", "SC.ID_DOMICILIO = SD.ID_DOMICILIO ")
		.leftJoin("SVC_CP CP", "SD.DES_CP = CP.CVE_CODIGO_POSTAL")
		.join(SVC_PERSONA, "SC.ID_PERSONA = SP.ID_PERSONA");

		queryUtil.where("SCP.ID_ESTATUS_CONVENIO = 2");
		queryUtil.where("SCP.ID_TIPO_PREVISION = :tipoPrevision")
		.setParameter("tipoPrevision", filtros.getTipoPrevision());
		if(filtros.getNumeroConvenio()!=null) {
			queryUtil.where("SCP.ID_CONVENIO_PF = :idConvenio")
			.setParameter(ID_CONVENIO, filtros.getNumeroConvenio());
		}
		if(filtros.getNumeroContratante()!=null) {
			queryUtil.where("SCPC.ID_CONTRATANTE = :idNumeroContratante")
			.setParameter("idNumeroContratante", filtros.getNumeroContratante());
		}
		if(filtros.getFolio()!=null) {
			queryUtil.where("SCP.DES_FOLIO = :desFolio")
			.setParameter(DES_FOLIO, filtros.getFolio());
		}
		if(filtros.getRfc()!=null) {
			queryUtil.where("SP.CVE_RFC = :cveRfc")
			.setParameter(CVE_RFC, filtros.getRfc());
		}
		if(filtros.getNumIne()!=null) {
			queryUtil.where("SP.NUM_INE = :numIne")
			.setParameter(NUM_INE, filtros.getNumIne());
		}
		queryUtil.groupBy("CP.CVE_CODIGO_POSTAL");
		String query = obtieneQuery(queryUtil);
		log.info("buscar convenio " +query);
		String encoded = encodedQuery(query);
	    parametros.put(AppConstantes.QUERY, encoded);
	    request.setDatos(parametros);
	    return request;
	}
	
/*	public DatosRequest validarFallecidoCtoAnterior(Integer idContratante, Integer idConvenio) {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("SF.ID_PERSONA")
		.from("SVC_FINADO SF")
		.join(SVC_CONTRATANTE, "SF.ID_PERSONA = SC.ID_PERSONA")
		.join(SVT_CONTRATANTE_PAQUETE_CONVENIO_PF, "SC.ID_CONTRATANTE = SCPC.ID_CONTRATANTE");
		if(idContratante!=null) {
			queryUtil.where("SC.ID_CONTRATANTE= " +idContratante +"");
		}else {
			queryUtil.where("SCPC.ID_CONVENIO_PF =" +idConvenio +"");
		}
		String query = obtieneQuery(queryUtil);
		log.info("validar "+query);
		String encoded = encodedQuery(query);
			parametro.put(AppConstantes.QUERY, encoded);
			request.setDatos(parametro);
			return request;
	} */
	
	public DatosRequest validarFallecido(FiltrosConvenioPFRequest filtros) {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("SP.CVE_RFC",
				"SP.NOM_PERSONA")
		.from("SVC_FINADO SF")
		.join(SVC_PERSONA, "SF.ID_PERSONA = SP.ID_PERSONA")
		.join(SVC_CONTRATANTE, "SP.ID_PERSONA = SC.ID_PERSONA")
		.join(SVT_CONTRATANTE_PAQUETE_CONVENIO_PF, "SC.ID_CONTRATANTE = SCPC.ID_CONTRATANTE")
		.join(SVT_CONVENIO_PF, "SCPC.ID_CONVENIO_PF = SPF.ID_CONVENIO_PF");
		if(filtros.getNumeroConvenio()!= null) {
			queryUtil.where("SCPC.ID_CONVENIO_PF =" +filtros.getNumeroConvenio()+"");
		}
		if(filtros.getFolio()!=null) {
			queryUtil.where("SPF.DES_FOLIO= '"+filtros.getFolio()+"'");
		}
			String query = obtieneQuery(queryUtil);
			log.info("validar fallecido ->"+query);
			String encoded = encodedQuery(query);
			parametro.put(AppConstantes.QUERY, encoded);
			request.setDatos(parametro);
			return request;
	}

	public DatosRequest validarPeriodo(FiltrosConvenioPFRequest filtros) {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("IF(SPF.IND_RENOVACION=0, (DATE_FORMAT(SPF.FEC_VIGENCIA, \"%Y%m\")), DATE_FORMAT(RPF.FEC_VIGENCIA, \"%Y%m\")) AS vig")
		.from(SVT_CONVENIO_PF)
		.leftJoin(SVT_RENOVACION_CONVENIO_PF, "SPF.ID_CONVENIO_PF = RPF.ID_CONVENIO_PF AND RPF.IND_ESTATUS=1")
		.join(SVT_CONTRATANTE_PAQUETE_CONVENIO_PF, "SPF.ID_CONVENIO_PF = SCPC.ID_CONVENIO_PF ")
		.join(SVC_CONTRATANTE, SCPC_ID_CONTRATANTE_SC_ID_CONTRATANTE)
		.join(SVC_PERSONA, "SC.ID_PERSONA=SP.ID_PERSONA");
		queryUtil.where("IF(TIMESTAMPDIFF(DAY, IF(SPF.IND_RENOVACION=0, DATE_FORMAT(SPF.FEC_VIGENCIA, \"%Y/%m/%1\"), DATE_FORMAT(RPF.FEC_VIGENCIA, \"%Y/%m/%1\")), CURDATE())>=0, SPF.FEC_VIGENCIA, 0)");
		if(filtros.getFolio()!=null) {
			queryUtil.where("SPF.DES_FOLIO = '"+filtros.getFolio() +"'");
		}
	    if(filtros.getNumeroConvenio()!=null) {
	    	queryUtil.where("SPF.ID_CONVENIO_PF = "+filtros.getNumeroConvenio()+"");
	    }
		String query = obtieneQuery(queryUtil);
		log.info("valida ->"+query);
		String encoded = encodedQuery(query);
			parametro.put(AppConstantes.QUERY, encoded);
			request.setDatos(parametro);
			return request;
	}

	

	public DatosRequest renovarPlan() {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVT_RENOVACION_CONVENIO_PF");
		q.agregarParametroValues(ID_CONVENIO_PF, "" + this.idConvenioPf+ "");
		q.agregarParametroValues("FEC_INICIO", "'" + this.vigencia + "'");
		q.agregarParametroValues("DES_FOLIO_ADENDA", "'" + folioAdenda + "'");
		if(this.datosBancarios!=null) {
			q.agregarParametroValues("DES_DATOS_BANCARIOS", "'" + this.datosBancarios + "'");	
		}
		q.agregarParametroValues("FEC_VIGENCIA", "DATE_ADD('"+ this.vigencia +"', INTERVAL 365 DAY)");
		q.agregarParametroValues("IND_ESTATUS", "1");
		q.agregarParametroValues("ID_USUARIO_ALTA", ""+usuarioAlta+"");
		q.agregarParametroValues("FEC_ALTA", ""+AppConstantes.CURRENT_TIMESTAMP+"");
		String query = q.obtenerQueryInsertar();
		log.info(" -> "+query);
		String encoded = encodedQuery(query);
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}
	
	public DatosRequest actualizarEstatusConvenio(Integer idConvenioPf) {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("UPDATE SVT_CONVENIO_PF ");
		q.agregarParametroValues("IND_RENOVACION", "1");
		q.agregarParametroValues(""+AppConstantes.ID_USUARIO_MODIFICA+"", ""+usuarioAlta+"");
		q.agregarParametroValues(""+AppConstantes.FEC_ACTUALIZACION+"", ""+AppConstantes.CURRENT_TIMESTAMP+"");
		q.addWhere("ID_CONVENIO_PF =" + idConvenioPf);
		String query = q.obtenerQueryActualizar();
		log.info("actualizar estatus convenio --> "+query);
		String encoded = encodedQuery(query);
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}
	
	public DatosRequest actualizarEstatusRenovacionConvenio(Integer idConvenioPf, String vigencia) {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("UPDATE SVT_RENOVACION_CONVENIO_PF");
		q.agregarParametroValues("IND_ESTATUS", "0");
		q.agregarParametroValues(AppConstantes.ID_USUARIO_BAJA, ""+usuarioAlta+"");
		q.agregarParametroValues(AppConstantes.FEC_BAJA, ""+AppConstantes.CURRENT_TIMESTAMP+"");
		q.addWhere("ID_CONVENIO_PF=" + idConvenioPf +" AND FEC_VIGENCIA = '"+ vigencia +"'");
		String query = q.obtenerQueryActualizar();
		log.info("estatus convenio ->"+query);
		String encoded = encodedQuery(query);
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}

	public DatosRequest cambiarEstatusPlan(String folio, Integer idConvenio, Integer id) {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper(UPDATE_SVT_CONVENIO_PF);
		q.agregarParametroValues(""+AppConstantes.ID_ESTATUS_CONVENIO+"", "4");
		q.agregarParametroValues(""+AppConstantes.ID_USUARIO_MODIFICA+"", ""+id+"");
		q.agregarParametroValues(""+AppConstantes.FEC_ACTUALIZACION+"", ""+AppConstantes.CURRENT_TIMESTAMP+"");
		if(folio!=null) {
			q.addWhere("DES_FOLIO = '"+folio+"'");
		}
		if(idConvenio!=null) {
			q.addWhere(ID_CONVENIO_PF+"="+idConvenio+"");
		}
		String query = q.obtenerQueryActualizar();
		log.info("cambiar estatus plan -> "+query);
		String encoded = encodedQuery(query);
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}

/*	public DatosRequest  cambiarEstatusPlanAnterior(Integer idConvenio, Integer idUsuario) {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		String query;
			final QueryHelper q = new QueryHelper(UPDATE_SVT_CONVENIO_PF);
			q.agregarParametroValues(""+AppConstantes.ID_ESTATUS_CONVENIO+"", "3");
			q.agregarParametroValues(""+AppConstantes.ID_USUARIO_MODIFICA+"", ""+idUsuario+"");
			q.agregarParametroValues(""+AppConstantes.FEC_ACTUALIZACION+"", ""+AppConstantes.CURRENT_TIMESTAMP+"");
			q.addWhere(ID_CONVENIO_PF+"="+idConvenio+"");
			query = q.obtenerQueryActualizar();
		log.info("renovar -> "+query);
		String encoded = encodedQuery(query);
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	} 


	public DatosRequest cambiarEstatusACerrado(String folio, Integer idConvenio,Integer idUsuario) {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		String query;
			final QueryHelper q = new QueryHelper(UPDATE_SVT_CONVENIO_PF);
			q.agregarParametroValues(""+AppConstantes.ID_ESTATUS_CONVENIO+"", "4");
			q.agregarParametroValues(""+AppConstantes.ID_USUARIO_MODIFICA+"", ""+idUsuario+"");
			q.agregarParametroValues(""+AppConstantes.FEC_ACTUALIZACION+"", ""+AppConstantes.CURRENT_TIMESTAMP+"");
			if(idConvenio!=null) {
				q.addWhere("ID_CONVENIO_PF = "+idConvenio+"");
			}else {
				q.addWhere("DES_FOLIO = '"+folio+"'");
			}query = q.obtenerQueryActualizar();
		log.info("a estatus cerrado "+query);
		String encoded = encodedQuery(query);
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	} */


	public DatosRequest contador (Integer idConvenioPf) {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametros = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("COUNT(*) AS c")
		.from(SVT_RENOVACION_CONVENIO_PF);
		queryUtil.where("RPF.ID_CONVENIO_PF= :id")
		.setParameter("id", +idConvenioPf);
		String query = obtieneQuery(queryUtil);
		log.info("contador -> " +query);
		String encoded = encodedQuery(query);
	    parametros.put(AppConstantes.QUERY, encoded );
	    request.setDatos(parametros);
	    return request;
	}


	public Map<String, Object> generarAdendaAnual(ReporteDto reporte) {
	Map<String, Object> envioDatos = new HashMap<>();
	envioDatos.put(""+AppConstantes.RUTA+"", reporte.getRutaNombreReporte());
	envioDatos.put(""+AppConstantes.TIPO+"", reporte.getTipoReporte());
	envioDatos.put("folio", reporte.getFolio());
	envioDatos.put("planPF", "Prevision Funeraria Plan Nuevo");
	return envioDatos;
	}


	public Map<String, Object> generarConvenioAnterior(ReporteDto reporteDto) {
		Map<String, Object> envioDatos = new HashMap<>();
		RuleBasedNumberFormat rule = new RuleBasedNumberFormat(new Locale("es-ES"), RuleBasedNumberFormat.SPELLOUT);
		String costoLetra = rule.format(reporteDto.getCostoRenovacion());
		envioDatos.put(""+AppConstantes.RUTA+"", reporteDto.getRutaNombreReporte());
		envioDatos.put(""+AppConstantes.TIPO+"", reporteDto.getTipoReporte());
		envioDatos.put(ID_CONVENIO, reporteDto.getIdConvenio());
		envioDatos.put("costoConvenio", reporteDto.getCostoRenovacion());
		envioDatos.put("version", "1.0.0");
		envioDatos.put("letraCosto", costoLetra+" Pesos 00/100 M/N");
		envioDatos.put("nomFibeso", " ");
		return envioDatos;
	}


	public Map<String, Object> generarHojaAfiliacion(ReporteDto reporteDto) {
		Map<String, Object> envioDatos = new HashMap<>();
		envioDatos.put(""+AppConstantes.RUTA+"", reporteDto.getRutaNombreReporte());
		envioDatos.put(""+AppConstantes.TIPO+"", reporteDto.getTipoReporte());
		envioDatos.put(ID_CONVENIO, reporteDto.getIdConvenio());
		envioDatos.put("tipoConvenio", "Previsión Funeraria Plan Anterior");
		envioDatos.put("nombreFibeso", " ");
		envioDatos.put("observaciones", reporteDto.getObservaciones());
		return envioDatos;
	}
	public DatosRequest actualizarDocumentacion(VerificarDocumentacionRequest verificarDoc) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("UPDATE SVC_VALIDACION_DOCUMENTOS_CONVENIO_PF");
		q.agregarParametroValues("IND_INE_AFILIADO", "" + verificarDoc.getIneAfiliado() + "");
		q.agregarParametroValues("IND_CURP", "" + verificarDoc.getCurp() + "");
		q.agregarParametroValues("IND_RFC", "" + verificarDoc.getRfc()+ "");
		q.agregarParametroValues("IND_ACTA_NACIMIENTO", "" + verificarDoc.getActaNac() + "");
		q.agregarParametroValues("IND_INE_BENEFICIARIO", ""+ verificarDoc.getIneBeneficiario() + "");
		q.agregarParametroValues(""+AppConstantes.ID_USUARIO_MODIFICA+"", ""+usuarioAlta+"");
		q.agregarParametroValues(""+AppConstantes.FEC_ACTUALIZACION+"", ""+AppConstantes.CURRENT_TIMESTAMP+"");
		q.addWhere("ID_VALIDACION_DOCUMENTO = "+verificarDoc.getIdValidacionDoc());
		String query = q.obtenerQueryActualizar() +"$$" +renovarDocumentacion(verificarDoc.getRenovarDoc(), verificarDoc.getIdValidacionDoc());
		log.info("---> "+query);
		String encoded = encodedQuery(query);
		        parametro.put(AppConstantes.QUERY, encoded);
		        parametro.put("separador","$$");
		        request.setDatos(parametro);
		return request;
	}


	private String renovarDocumentacion(RenovarDocumentacionModel renovarDoc, Integer validacionDoc) {
		 DatosRequest request = new DatosRequest();
	        Map<String, Object> parametro = new HashMap<>();
	    	StringBuilder queries= new StringBuilder();
	    	  final QueryHelper qh = new QueryHelper("INSERT INTO SVC_VALIDACION_DOCUMENTOS_RENOVACION_CONVENIO_PF");
		        qh.agregarParametroValues("ID_VALIDACION_DOCUMENTO", ""+validacionDoc+"");
		        qh.agregarParametroValues("IND_CONVENIO_ANTERIOR", ""+renovarDoc.getConvenioAnterior()+"");
		        //qh.agregarParametroValues("IND_COMPROBANTE_ESTUDIOS_BENEFICIARIO", ""+renovarDoc.getComprobanteEstudios()+"");
		        //qh.agregarParametroValues("IND_ACTA_MATRIMONIO", ""+renovarDoc.getActaMatrimonio()+"");
		      //  qh.agregarParametroValues("IND_DECLARACION_CONCUBINATO", ""+renovarDoc.getDeclaracionConcubinato()+"");
		        qh.agregarParametroValues("IND_CARTA_PODER", ""+renovarDoc.getCartaPoder()+"");
		        qh.agregarParametroValues("IND_INE_TESTIGO", ""+renovarDoc.getIneTestigo()+"");
		        qh.agregarParametroValues("IND_INE_TESTIGO_DOS", ""+renovarDoc.getIneTestigoDos()+"");
		        qh.agregarParametroValues(AppConstantes.ID_USUARIO_ALTA, ""+usuarioAlta+"");
		        qh.agregarParametroValues(AppConstantes.FEC_ALTA, ""+AppConstantes.CURRENT_TIMESTAMP+"");
		        String query = qh.obtenerQueryInsertar(); 
	        queries.append(query+ "ON DUPLICATE KEY ");
	        final QueryHelper q = new QueryHelper("UPDATE");
	        q.agregarParametroValues("IND_CONVENIO_ANTERIOR", ""+renovarDoc.getConvenioAnterior()+"");
	        //q.agregarParametroValues("IND_COMPROBANTE_ESTUDIOS_BENEFICIARIO", ""+renovarDoc.getComprobanteEstudios()+"");
	        //q.agregarParametroValues("IND_ACTA_MATRIMONIO", ""+renovarDoc.getActaMatrimonio()+"");
	        //q.agregarParametroValues("IND_DECLARACION_CONCUBINATO", ""+renovarDoc.getDeclaracionConcubinato()+"");
	        q.agregarParametroValues("IND_CARTA_PODER", ""+renovarDoc.getCartaPoder()+"");
	        q.agregarParametroValues("IND_INE_TESTIGO", ""+renovarDoc.getIneTestigo()+"");
	        q.agregarParametroValues("IND_INE_TESTIGO_DOS", ""+renovarDoc.getIneTestigoDos()+"");
	        q.agregarParametroValues(""+AppConstantes.ID_USUARIO_MODIFICA+"", ""+usuarioAlta+"");
	        q.agregarParametroValues(""+AppConstantes.FEC_ACTUALIZACION+"", ""+AppConstantes.CURRENT_TIMESTAMP+"");
	        q.addWhere("");
	        String queryUpdate = q.obtenerQueryActualizar(); 
	        queries.append(queryUpdate);
	        String queryFinal=queries.toString().replace("WHERE", "").replace("SET", "").replaceFirst(";", " ");
	        String encoded = encodedQuery(queryFinal);
	        parametro.put(AppConstantes.QUERY, encoded);
	        request.setDatos(parametro);
	        return queryFinal;
	}
	
	public  DatosRequest cambiarEstatusDoc(Integer idConvenio) {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		String query;
			final QueryHelper q = new QueryHelper("UPDATE SVC_VALIDACION_DOCUMENTOS_CONVENIO_PF");
			q.agregarParametroValues("IND_ACTIVO", "0");
			q.agregarParametroValues(AppConstantes.ID_USUARIO_BAJA, ""+usuarioAlta+"");
			q.agregarParametroValues(AppConstantes.FEC_BAJA, ""+AppConstantes.CURRENT_TIMESTAMP+"");
			q.addWhere("ID_CONVENIO_PF = "+idConvenio+" AND IND_ACTIVO= 1 ");
			query = q.obtenerQueryActualizar();
		log.info("renovar -> "+query);
		String encoded = encodedQuery(query);
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}
	
	public DatosRequest validarBeneficiarios(DatosRequest request, Integer idConvenio, Integer idContra, Integer idUsuario) {
		Map<String, Object> parametro = new HashMap<>();
		String query;
			final QueryHelper q = new QueryHelper("UPDATE SVT_CONTRATANTE_BENEFICIARIOS SB");
			q.agregarParametroValues("IND_ACTIVO", "0");
			q.agregarParametroValues("ID_USUARIO_BAJA", ""+idUsuario+"");
			q.agregarParametroValues("FEC_BAJA", ""+AppConstantes.CURRENT_TIMESTAMP+"");
			q.addWhere("SB.ID_CONTRATANTE_BENEFICIARIOS IN " );
			query = q.obtenerQueryActualizar().replace(";", "(");
			SelectQueryUtil queryUtil = new SelectQueryUtil();
			queryUtil.select("SB.ID_CONTRATANTE_BENEFICIARIOS")
			.from("SVT_CONTRATANTE_BENEFICIARIOS SB")
			.leftJoin("SVT_BENEFICIARIOS_DOCUMENTACION_PLAN_ANTERIOR SBD", "SB.ID_CONTRATANTE_BENEFICIARIOS = SBD.ID_CONTRATANTE_BENEFICIARIOS")
			.join(SVT_CONTRATANTE_PAQUETE_CONVENIO_PF, "SB.ID_CONTRATANTE_PAQUETE_CONVENIO_PF = SCPC.ID_CONTRATANTE_PAQUETE_CONVENIO_PF")
			.join("SVT_CONVENIO_PF PF", "SCPC.ID_CONVENIO_PF= PF.ID_CONVENIO_PF")
			.join("SVC_PERSONA SP ON SB.ID_PERSONA = SP.ID_PERSONA");
			queryUtil.where("(IF(SB.ID_PARENTESCO=8 OR SB.ID_PARENTESCO=9 AND TIMESTAMPDIFF(YEAR, SP.FEC_NAC, CURDATE())>18, SB.ID_PARENTESCO, NULL))")
			.and("PF.ID_TIPO_PREVISION=2");
			if(idConvenio!=null) {
				queryUtil.where("PF.ID_CONVENIO_PF= :idConvenio")
				.setParameter("idConvenio", idConvenio);	
			}
			if(idContra!=null && idConvenio==null) {
				queryUtil.where("SCPC.ID_CONTRATANTE= :idContra")
				.setParameter("idContra", idContra);	
			}
			queryUtil.where("(SBD.IND_COMPROBANTE_ESTUDIOS = 0 OR SBD.IND_COMPROBANTE_ESTUDIOS IS NULL))");
			String queryConsulta = obtieneQuery(queryUtil);
			String consultaFinal=query+queryConsulta;
		log.info("update -> "+consultaFinal);
		String encoded = encodedQuery(consultaFinal);
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}

	public DatosRequest buscarBeneficiarios(String folio, Integer id) {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametros = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("CONCAT(PC.NOM_PERSONA, ' ', "
				+ "PC.NOM_PRIMER_APELLIDO, ' ', "
				+ "PC.NOM_SEGUNDO_APELLIDO ) AS nombreBeneficiario",
				" SCB.ID_CONTRATANTE_BENEFICIARIOS AS id")
		.from("SVT_CONTRATANTE_BENEFICIARIOS SCB")
		.join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF BENEF", "SCB.ID_CONTRATANTE_PAQUETE_CONVENIO_PF=BENEF.ID_CONTRATANTE_PAQUETE_CONVENIO_PF")
		.join("SVT_CONVENIO_PF PF", "BENEF.ID_CONVENIO_PF = PF.ID_CONVENIO_PF")
		.join("SVC_PERSONA PC", "SCB.ID_PERSONA = PC.ID_PERSONA");
		queryUtil.where("SCB.IND_ACTIVO=1 AND SCB.IND_SINIESTROS=0");
		if(folio!=null) {
			queryUtil.where("PF.DES_FOLIO= :folio")
			.setParameter("folio", folio);	
		}
		if(id!=null) {
			queryUtil.where("PF.ID_CONVENIO_PF= :id")
			.setParameter("id", id);	
		}
		queryUtil.limit(3);
		String query = obtieneQuery(queryUtil);
		log.info("beneficiarios -> " +query);
		String encoded = encodedQuery(query);
	    parametros.put(AppConstantes.QUERY, encoded );
	    request.setDatos(parametros);
	    return request;
	}
	
	private static String obtieneQuery(SelectQueryUtil queryUtil) {
        return queryUtil.build();
    }
	
	private static String encodedQuery(String query) {
		return DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
	}
}
