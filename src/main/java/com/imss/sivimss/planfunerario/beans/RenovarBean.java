package com.imss.sivimss.planfunerario.beans;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.planfunerario.exception.BadRequestException;
import com.imss.sivimss.planfunerario.model.request.FiltrosConvenioPFRequest;
import com.imss.sivimss.planfunerario.model.request.RenovarPlanPFRequest;
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
	private Integer usuarioModifica;
	
	public RenovarBean(RenovarPlanPFRequest renovarRequest) {
		this.datosBancarios = renovarRequest.getDatosBancarios();
		this.idConvenioPf = renovarRequest.getIdConvenioPf();
	}


	public DatosRequest buscarNuevo(DatosRequest request, FiltrosConvenioPFRequest filtros) {
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("SCP.DES_FOLIO AS folio",
				"SCP.ID_CONVENIO_PF AS convenioPF",
				 "SP.CVE_RFC AS rfc",
				 "SC.CVE_MATRICULA AS matricula",
				 "SP.NOM_PERSONA AS nombre",
				 "SP.NOM_PRIMER_APELLIDO AS primerApellido",
				 "SP.NOM_SEGUNDO_APELLIDO AS segundoApellido",
				 "SCP.ID_TIPO_PREVISION AS tipoPrevision",
				 "SCPC.ID_PAQUETE",
				 "CASE "
				+ "WHEN PAQ.ID_PAQUETE = 7 THEN 'BASICO' "
				+ "WHEN PAQ.ID_PAQUETE = 8 THEN 'ECONOMICO' "
				+ "WHEN PAQ.ID_PAQUETE = 9 THEN 'BASICO CON CREMACION'"
				+ "ELSE 'DESCONOCIDO'"
				+ "END AS tipoPaquete",
				 "SCP.ID_ESTATUS_CONVENIO AS estatusConvenio",
				 "DATE_FORMAT(SCP.FEC_INICIO, '%d/%m/%Y') AS fechaInicio",
				 "DATE_FORMAT(SCP.FEC_VIGENCIA, '%d/%m/%Y') AS fechaVigencia",
				 "SD.DES_CALLE AS calle",
				 "SD.NUM_EXTERIOR AS numExt",
				 "SD.NUM_INTERIOR AS numInt",
				 "CP.CVE_CODIGO_POSTAL AS cp",
				 "CP.DES_ESTADO AS estado",
				 "CP.DES_MNPIO AS municipio",
				 "SP.DES_TELEFONO AS telefono",
				 "SP.DES_CORREO AS correo",
				 " PAQ.MON_COSTO_REFERENCIA AS costoRenovacion",
				 "(SELECT "
				 +"GROUP_CONCAT(CONCAT(PC.NOM_PERSONA, ' ', "
				 +"PC.NOM_PRIMER_APELLIDO, ' ', "
				 + "PC.NOM_SEGUNDO_APELLIDO)) "
				  +"FROM svt_contratante_beneficiarios SCB "
				 +"JOIN svt_contratante_paquete_convenio_pf BENEF ON SCB.ID_CONTRATANTE_PAQUETE_CONVENIO_PF=BENEF.ID_CONTRATANTE_PAQUETE_CONVENIO_PF "
				 + "JOIN svc_persona PC ON SCB.ID_PERSONA = PC.ID_PERSONA "
				 + "WHERE BENEF.ID_CONVENIO_PF=SCP.ID_CONVENIO_PF  ) AS beneficiario ")
		.from("SVT_CONVENIO_PF SCP")
		.join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF SCPC", "SCP.ID_CONVENIO_PF = SCPC.ID_CONVENIO_PF")
		.join("SVT_PAQUETE PAQ", "SCPC.ID_PAQUETE = PAQ.ID_PAQUETE")
		.join("SVC_CONTRATANTE SC", "SCPC.ID_CONTRATANTE = SC.ID_CONTRATANTE")
		.join("SVT_DOMICILIO SD", "SC.ID_DOMICILIO = SD.ID_DOMICILIO ")
		.join("SVC_CP CP", "SD.ID_CP = CP.ID_CODIGO_POSTAL")
		.join("SVC_PERSONA SP", "SC.ID_PERSONA = SP.ID_PERSONA");
		queryUtil.where("SCP.ID_TIPO_PREVISION = 1");
		if(filtros.getFolio()!=null && filtros.getRfc()==null && filtros.getNumIne()==null) {
			queryUtil.where("SCP.DES_FOLIO = :desFolio")
			.setParameter("desFolio", filtros.getFolio());
		}
		else if(filtros.getFolio()==null && filtros.getRfc()!=null && filtros.getNumIne()==null) {
			queryUtil.where("SP.CVE_RFC = :cveRfc")
			.setParameter("cveRfc", filtros.getRfc());
		}else if(filtros.getFolio()==null && filtros.getRfc()==null && filtros.getNumIne()!=null) {
			queryUtil.where("SP.NUM_INE = :numIne")
			.setParameter("numIne", filtros.getNumIne());
		}else if(filtros.getFolio()!=null && filtros.getRfc()!=null && filtros.getNumIne()==null) {
			queryUtil.where("SCP.DES_FOLIO  = :desFolio").and("SP.CVE_RFC = :cveRfc")
			.setParameter("desFolio", filtros.getFolio())
			.setParameter("cveRfc", filtros.getRfc());
		}else if(filtros.getFolio()!=null && filtros.getRfc()==null && filtros.getNumIne()!=null) {
			queryUtil.where("SCP.DES_FOLIO  = :desFolio").and("SP.NUM_INE = :numIne")
			.setParameter("desFolio", filtros.getFolio())
			.setParameter("numIne", filtros.getNumIne());
		}else if(filtros.getFolio()==null && filtros.getRfc()!=null && filtros.getNumIne()!=null) {
			queryUtil.where("SP.CVE_RFC = :cveRfc").and("SP.NUM_INE = :numIne")
			.setParameter("cveRfc", filtros.getRfc())
			.setParameter("numIne", filtros.getNumIne());
		}else if(filtros.getFolio()!=null && filtros.getRfc()!=null && filtros.getNumIne()!=null) {
			queryUtil.where("SCP.DES_FOLIO = :desFolio").and("SP.CVE_RFC = :cveRfc").and("SP.NUM_INE = :numIne")
			.setParameter("desFolio", filtros.getFolio())
			.setParameter("cveRfc", filtros.getRfc())
			.setParameter("numIne", filtros.getNumIne());
		}
			
		String query = obtieneQuery(queryUtil);
		log.info("QueryHelper -> " +query);
		Map<String, Object> parametros = new HashMap<>();
	    parametros.put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes()));
	    request.setDatos(parametros);
	    return request;
	}

	public DatosRequest buscarAnterior(DatosRequest request, FiltrosConvenioPFRequest filtros ) {
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("SCP.DES_FOLIO AS folio",
				 "SP.CVE_RFC AS rfc",
				 "SC.CVE_MATRICULA AS matricula",
				 "SP.NOM_PERSONA AS nombre",
				 "SP.NOM_PRIMER_APELLIDO AS primerApellido",
				 "SP.NOM_SEGUNDO_APELLIDO AS segundoApellido",
				 "SCP.ID_TIPO_PREVISION AS tipoPrevision",
				 "SCPC.ID_PAQUETE",
				 "CASE "
				+ "WHEN PAQ.ID_PAQUETE = 7 THEN 'BASICO' "
				+ "WHEN PAQ.ID_PAQUETE = 8 THEN 'ECONOMICO' "
				+ "WHEN PAQ.ID_PAQUETE =9 THEN 'BASICO CON CREMACION'"
				+ "ELSE 'DESCONOCIDO'"
				+ "END AS tipoPaquete",
				 "SCP.ID_ESTATUS_CONVENIO AS estatusConvenio",
				 "DATE_FORMAT(SCP.FEC_INICIO, '%d/%m/%Y') AS fechaInicio",
				 "DATE_FORMAT(SCP.FEC_VIGENCIA, '%d/%m/%Y') AS fechaVigencia",
				 "SD.DES_CALLE AS calle",
				 "SD.NUM_EXTERIOR AS numExt",
				 "SD.NUM_INTERIOR AS numInt",
				 "CP.CVE_CODIGO_POSTAL AS cp",
				 "CP.DES_ESTADO AS estado",
				 "CP.DES_MNPIO AS municipio",
				 "SP.DES_TELEFONO AS telefono",
				 "SP.DES_CORREO AS correo",
		 " PAQ.MON_COSTO_REFERENCIA AS costoRenovacion",
		 "(SELECT "
		 +"GROUP_CONCAT(CONCAT(PC.NOM_PERSONA, ' ', "
		 +"PC.NOM_PRIMER_APELLIDO, ' ', "
		 + "PC.NOM_SEGUNDO_APELLIDO)) "
		  +"FROM svt_contratante_beneficiarios SCB "
		 +"JOIN svt_contratante_paquete_convenio_pf BENEF ON SCB.ID_CONTRATANTE_PAQUETE_CONVENIO_PF=BENEF.ID_CONTRATANTE_PAQUETE_CONVENIO_PF "
		 + "JOIN svc_persona PC ON SCB.ID_PERSONA = PC.ID_PERSONA "
		 + "WHERE BENEF.ID_CONVENIO_PF= " +filtros.getNumeroConvenio()+") AS beneficiario ")
		.from("SVT_CONVENIO_PF SCP")
		.join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF SCPC", "SCP.ID_CONVENIO_PF = SCPC.ID_CONVENIO_PF")
		.join("SVT_PAQUETE PAQ", "SCPC.ID_PAQUETE = PAQ.ID_PAQUETE")
		.join("SVC_CONTRATANTE SC", "SCPC.ID_CONTRATANTE = SC.ID_CONTRATANTE")
		.join("SVT_DOMICILIO SD", "SC.ID_DOMICILIO = SD.ID_DOMICILIO ")
		.join("SVC_CP CP", "SD.ID_CP = CP.ID_CODIGO_POSTAL")
		.join("SVC_PERSONA SP", "SC.ID_PERSONA = SP.ID_PERSONA");
		queryUtil.where("SCP.ID_TIPO_PREVISION = 2");
		if(filtros.getNumeroConvenio()!=null && filtros.getNumeroContratante()==null) {
			queryUtil.where("SCP.ID_CONVENIO_PF = :idConvenio")
			.setParameter("idConvenio", filtros.getNumeroConvenio());
		}
		else if(filtros.getNumeroContratante()!=null && filtros.getNumeroConvenio()==null) {
			queryUtil.where("SCPC.ID_CONTRATANTE = :idNumeroContratante")
			.setParameter("idNumeroContratante", filtros.getNumeroContratante());
		}else if(filtros.getNumeroContratante()!=null && filtros.getNumeroConvenio()!=null) {
			queryUtil.where("SCP.ID_CONVENIO_PF = :idConvenio").and("SCPC.ID_CONTRATANTE = :idNumeroContratante")
			.setParameter("idConvenio", filtros.getNumeroConvenio())
			.setParameter("idNumeroContratante", filtros.getNumeroContratante());
		}
		String query = obtieneQuery(queryUtil);
		log.info("QueryHelper -> " +query);
		Map<String, Object> parametros = new HashMap<>();
	    parametros.put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes()));
	    request.setDatos(parametros);
	    return request;
	}

	private static String obtieneQuery(SelectQueryUtil queryUtil) {
        return queryUtil.build();
    }

	public DatosRequest validarFallecido(String rfc) {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
			String query = "SELECT SP.CVE_RFC, "
					+ "SP.NOM_PERSONA "
					+ "FROM svc_finado SF "
					+ "JOIN svc_persona SP ON SP.ID_PERSONA = SF.ID_PERSONA "
					+ "WHERE SP.CVE_RFC =  '"+rfc +"' ";
			String encoded=DatatypeConverter.printBase64Binary(query.getBytes());
			log.info("validar "+query);
			parametro.put(AppConstantes.QUERY, encoded);
			request.setDatos(parametro);
			return request;
	}

	public DatosRequest validarPeriodo(FiltrosConvenioPFRequest filtros) {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("PF.FEC_VIGENCIA")
		.from("SVT_CONVENIO_PF PF");
		queryUtil.where("IF(TIMESTAMPDIFF(DAY, CURDATE(), PF.FEC_VIGENCIA)>=20, PF.FEC_VIGENCIA, 0)");
		if(filtros.getFolio()!=null) {
			queryUtil.where("PF.DES_FOLIO = '"+filtros.getFolio() +"' ");
		}else {
			queryUtil.where("PF.ID_CONVENIO_PF = "+filtros.getNumIne() +" ");
		}
		String query = obtieneQuery(queryUtil);
			String encoded=DatatypeConverter.printBase64Binary(query.getBytes());
			log.info("validar -> "+query);
			parametro.put(AppConstantes.QUERY, encoded);
			request.setDatos(parametro);
			return request;
	}

	public DatosRequest validaPeriodoCtoAnterior(Integer idContratante, Integer idConvenio) {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("PF.FEC_VIGENCIA")
		.from("SVT_CONVENIO_PF PF")
		.join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF SCPC", "PF.ID_CONVENIO_PF = SCPC.ID_CONVENIO_PF")
		.join("SVC_CONTRATANTE SC", "SCPC.ID_CONTRATANTE = SC.ID_CONTRATANTE");
		queryUtil.where("IF(TIMESTAMPDIFF(DAY, CURDATE(), PF.FEC_VIGENCIA)>=20, PF.FEC_VIGENCIA, 0)");
		if(idConvenio!=null) {
			queryUtil.where("PF.ID_CONVENIO_PF = "+idConvenio +"");
		}else {
			queryUtil.where("SC.ID_CONTRATANTE = "+idContratante +"");
		}
		String query = obtieneQuery(queryUtil);
			String encoded=DatatypeConverter.printBase64Binary(query.getBytes());
			log.info("validar -> "+query);
			parametro.put(AppConstantes.QUERY, encoded);
			request.setDatos(parametro);
			return request;
	}

	public DatosRequest validarFallecido(Integer idContratante, Integer idConvenio) {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("SF.ID_PERSONA")
		.from("SVC_FINADO SF")
		.join("SVC_CONTRATANTE SC", "SF.ID_PERSONA = SC.ID_PERSONA")
		.join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF SCPC", "SC.ID_CONTRATANTE = SCPC.ID_CONTRATANTE");
		if(idContratante!=null) {
			queryUtil.where("SC.ID_CONTRATANTE= " +idContratante +"");
		}else {
			queryUtil.where("SCPC.ID_CONVENIO_PF =" +idConvenio +"");
		}
		String query = obtieneQuery(queryUtil);
			String encoded=DatatypeConverter.printBase64Binary(query.getBytes());
			log.info("validar "+query);
			parametro.put(AppConstantes.QUERY, encoded);
			request.setDatos(parametro);
			return request;
	}


	public DatosRequest renovarPlan() {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("UPDATE SVT_CONVENIO_PF");
		q.agregarParametroValues("DATOS_BANCARIOS", "'" + this.datosBancarios + "'");
		q.agregarParametroValues("FEC_VIGENCIA", "DATE_ADD(FEC_VIGENCIA, INTERVAL 365 DAY)");
		q.agregarParametroValues("ID_USUARIO_MODIFICA", ""+usuarioModifica+"");
		q.agregarParametroValues("FEC_ACTUALIZACION", " CURRENT_TIMESTAMP() ");
		q.addWhere("ID_CONVENIO_PF = " + this.idConvenioPf);
		String query = q.obtenerQueryActualizar();
		log.info("renovar -> "+query);
		parametro.put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes()));
		request.setDatos(parametro);
		return request;
	}


	public DatosRequest validarVigenciaCtoAnterior(Integer idContratante, Integer idConvenio) {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("PF.FEC_VIGENCIA")
		.from("SVT_CONVENIO_PF PF")
		.join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF SCPC", "PF.ID_CONVENIO_PF= SCPC.ID_CONVENIO_PF");
		queryUtil.where("IF(TIMESTAMPDIFF(DAY, CURDATE(), PF.FEC_VIGENCIA)>=0, PF.FEC_VIGENCIA, 0)");
		if(idConvenio!=null) {
			queryUtil.where("PF.ID_CONVENIO_PF = "+idConvenio+"");
		}else {
			queryUtil.where("SCPC.ID_CONTRATANTE = "+idContratante+"");
		}
		String query = obtieneQuery(queryUtil);
			String encoded=DatatypeConverter.printBase64Binary(query.getBytes());
			log.info("validar "+query);
			parametro.put(AppConstantes.QUERY, encoded);
			request.setDatos(parametro);
			return request;
	}


	public DatosRequest cambiarEstatusPlan(String folio, Integer id) {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("UPDATE SVT_CONVENIO_PF");
		q.agregarParametroValues("ID_ESTATUS_CONVENIO", "3");
		q.agregarParametroValues("ID_USUARIO_MODIFICA", ""+id+"");
		q.agregarParametroValues("FEC_ACTUALIZACION", " CURRENT_TIMESTAMP() ");
		q.addWhere("DES_FOLIO = '"+folio+"'");
		String query = q.obtenerQueryActualizar();
		log.info("renovar -> "+query);
		parametro.put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes()));
		request.setDatos(parametro);
		return request;
	}


	public DatosRequest validaVigencia(FiltrosConvenioPFRequest filtros) {
		// TODO Auto-generated method stub
		return null;
	}


	public DatosRequest  cambiarEstatusPlanAnterior(Integer idContratante, Integer idConvenio,
			Integer idUsuario) {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		String query;
		if(idConvenio!=null) {
			final QueryHelper q = new QueryHelper("UPDATE SVT_CONVENIO_PF");
			q.agregarParametroValues("ID_ESTATUS_CONVENIO", "3");
			q.agregarParametroValues("ID_USUARIO_MODIFICA", ""+idUsuario+"");
			q.agregarParametroValues("FEC_ACTUALIZACION", " CURRENT_TIMESTAMP() ");
			q.addWhere("ID_CONVENIO_PF = "+idConvenio+"");
			query = q.obtenerQueryActualizar();
		}else {
			query = "UPDATE SVT_CONVENIO_PF SC "
					+ "JOIN SVT_CONTRATANTE_PAQUETE_CONVENIO_PF SCPC ON SC.ID_CONVENIO_PF = SCPC.ID_CONVENIO_PF "
					+ "SET SC.ID_ESTATUS_CONVENIO = 3,"
					+ "SC.ID_USUARIO_MODIFICA= " +idUsuario+ " ,"
							+ "SC.FEC_ACTUALIZACION= CURRENT_TIMESTAMP() "
					+ " WHERE SCPC.ID_CONTRATANTE ="  +idContratante+ "";
		}
		log.info("renovar -> "+query);
		parametro.put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes()));
		request.setDatos(parametro);
		return request;
	}

}
