package com.imss.sivimss.planfunerario.beans;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.planfunerario.exception.BadRequestException;
import com.imss.sivimss.planfunerario.model.request.FiltrosConvenioExtRequest;
import com.imss.sivimss.planfunerario.model.request.RenovarPlanExtRequest;
import com.imss.sivimss.planfunerario.util.AppConstantes;
import com.imss.sivimss.planfunerario.util.DatosRequest;
import com.imss.sivimss.planfunerario.util.QueryHelper;
import com.imss.sivimss.planfunerario.util.SelectQueryUtil;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RenovarExt {

	public DatosRequest buscarConvenioExt(DatosRequest request, FiltrosConvenioExtRequest filtros, String fecFormat) {
		Map<String, Object> parametros = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("SV.DES_VELATORIO AS velatorio",
				"PF.ID_CONVENIO_PF AS idConvenio",
				"PF.DES_FOLIO AS folio",
				"SP.CVE_RFC AS rfc",
				"SP.NOM_PERSONA AS nombre",
				"SP.NOM_PRIMER_APELLIDO AS primerApellido",
				"SP.NOM_SEGUNDO_APELLIDO AS segundoApellido",
				"PF.ID_TIPO_PREVISION AS tipoPrevision",
				"PAQ.DES_NOM_PAQUETE AS tipopaquete",
				"PAQ.MON_COSTO_REFERENCIA AS cuotaRecuperacion",
				"IF(PF.IND_RENOVACION=0, (DATE_FORMAT(PF.FEC_INICIO, '"+fecFormat+"')), DATE_FORMAT(RPF.FEC_INICIO, '"+fecFormat+"')) AS fecInicio",
				"IF(PF.IND_RENOVACION=0, (DATE_FORMAT(PF.FEC_VIGENCIA, '"+fecFormat+"')), DATE_FORMAT(RPF.FEC_VIGENCIA, '"+fecFormat+"')) AS fecVigencia")
		.from("SVT_CONVENIO_PF PF")
		.join("SVC_VELATORIO SV", "PF.ID_VELATORIO = SV.ID_VELATORIO")
		.leftJoin("SVT_RENOVACION_CONVENIO_PF RPF", "PF.ID_CONVENIO_PF = RPF.ID_CONVENIO_PF AND RPF.IND_ESTATUS = 1")
		.join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF SCPC", "PF.ID_CONVENIO_PF = SCPC.ID_CONVENIO_PF")
		.join("SVT_PAQUETE PAQ", "SCPC.ID_PAQUETE = PAQ.ID_PAQUETE")
		.join("SVC_CONTRATANTE SC", "SCPC.ID_CONTRATANTE = SC.ID_CONTRATANTE")
		.join("SVC_PERSONA SP", "SC.ID_PERSONA = SP.ID_PERSONA");
		queryUtil.where("PF.ID_ESTATUS_CONVENIO = 4");
		if(filtros.getIdDelegacion()!=null) {
			queryUtil.where("SV.ID_DELEGACION= :idDelegacion")
			.setParameter("idDelegacion", filtros.getIdDelegacion());
		}
		if(filtros.getIdVelatorio()!=null) {
			queryUtil.where("PF.ID_VELATORIO= :idVelatorio")
			.setParameter("idVelatorio", filtros.getIdVelatorio());
		}
		if(filtros.getFolio()!=null) {
			queryUtil.where("PF.DES_FOLIO= :folio")
			.setParameter("folio", filtros.getFolio());
		}
		if(filtros.getRfc()!=null) {
			queryUtil.where("SP.CVE_RFC = :rfc")
			.setParameter("rfc", filtros.getRfc());
		}
		String query = obtieneQuery(queryUtil);
		log.info("-> " +query);
		String encoded = encodedQuery(query);
	    parametros.put(AppConstantes.QUERY, encoded);
	    parametros.put("pagina",filtros.getPagina());
        parametros.put("tamanio",filtros.getTamanio());
        request.getDatos().remove(AppConstantes.DATOS);
	    request.setDatos(parametros);
		return request;
	}
	
	public DatosRequest verDetalle(DatosRequest request, String palabra, String fecFormat) {
		Map<String, Object> parametros = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("PF.ID_CONVENIO_PF AS idConvenio",
				"PF.DES_FOLIO AS folio",
				"SP.CVE_RFC AS rfc",
				"SC.CVE_MATRICULA AS matricula",
				"SP.NOM_PERSONA AS nombre",
				"SP.NOM_PRIMER_APELLIDO AS primerApellido",
				"SP.NOM_SEGUNDO_APELLIDO AS segundoApellido",
				"PF.ID_TIPO_PREVISION AS tipoPrevision",
				"PF.ID_ESTATUS_CONVENIO AS idEstatus",
				"PAQ.DES_NOM_PAQUETE AS tipopaquete",
				"PAQ.MON_COSTO_REFERENCIA AS cuotaRecuperacion",
				"IF(PF.IND_RENOVACION=0, (DATE_FORMAT(PF.FEC_INICIO, '"+fecFormat+"')), DATE_FORMAT(RPF.FEC_INICIO, '"+fecFormat+"')) AS fecInicio",
				"IF(PF.IND_RENOVACION=0, (DATE_FORMAT(PF.FEC_VIGENCIA, '"+fecFormat+"')), DATE_FORMAT(RPF.FEC_VIGENCIA, '"+fecFormat+"')) AS fecVigencia",
				"SP.DES_TELEFONO AS tel",
				"SP.DES_CORREO AS correo",
				"PF.IND_RENOVACION AS indRenovacion")
		.from("SVT_CONVENIO_PF PF")
		.leftJoin("SVT_RENOVACION_CONVENIO_PF RPF", "PF.ID_CONVENIO_PF = RPF.ID_CONVENIO_PF AND RPF.IND_ESTATUS = 1")
		.join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF SCPC", "PF.ID_CONVENIO_PF = SCPC.ID_CONVENIO_PF")
		.join("SVT_PAQUETE PAQ", "SCPC.ID_PAQUETE = PAQ.ID_PAQUETE")
		.join("SVC_CONTRATANTE SC", "SCPC.ID_CONTRATANTE = SC.ID_CONTRATANTE")
		.join("SVC_PERSONA SP", "SC.ID_PERSONA = SP.ID_PERSONA");
		queryUtil.where("PF.ID_CONVENIO_PF = :idConvenio").and("PF.ID_ESTATUS_CONVENIO= 4")
		.setParameter("idConvenio", Integer.parseInt(palabra));
		String query = obtieneQuery(queryUtil);
		log.info("-> " +query);
		String encoded = encodedQuery(query);
	    parametros.put(AppConstantes.QUERY, encoded);
	    request.setDatos(parametros);
		return request;
	}
	
	public DatosRequest buscarBeneficiarios(DatosRequest request, String palabra) {
		Map<String, Object> parametros = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("SCB.ID_CONTRATANTE_BENEFICIARIOS AS id",
				"CONCAT(SP.NOM_PERSONA, ' ',"
				+"SP.NOM_PRIMER_APELLIDO, ' ',"
				+"SP.NOM_SEGUNDO_APELLIDO) AS nombreBeneficiario",
				"SP.ID_PERSONA AS idPersona")
		.from("SVT_CONVENIO_PF PF")
		.join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF SCPC", "PF.ID_CONVENIO_PF = SCPC.ID_CONVENIO_PF")
		.join("SVT_CONTRATANTE_BENEFICIARIOS SCB", "SCPC.ID_CONTRATANTE_PAQUETE_CONVENIO_PF = SCB.ID_CONTRATANTE_PAQUETE_CONVENIO_PF")
		.join("SVC_PERSONA SP", "SCB.ID_PERSONA = SP.ID_PERSONA");
		queryUtil.where("PF.ID_CONVENIO_PF = :idConvenio").and("SCB.IND_ACTIVO=1").and("(SCB.IND_SINIESTROS=0 OR SCB.IND_SINIESTROS IS NULL)")
		.setParameter("idConvenio", Integer.parseInt(palabra));
		String query = obtieneQuery(queryUtil);
		log.info("-> " +query);
		String encoded = encodedQuery(query);
	    parametros.put(AppConstantes.QUERY, encoded);
	    request.setDatos(parametros);
		return request;
	}
	
	public DatosRequest actualizarVigenciaConvenio( Integer idConvenio, Integer idUsuario) {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("UPDATE SVT_RENOVACION_CONVENIO_PF");
		q.agregarParametroValues("FEC_VIGENCIA", ""+AppConstantes.CURRENT_TIMESTAMP+"");
		q.agregarParametroValues(""+AppConstantes.ID_USUARIO_MODIFICA+"", ""+idUsuario+"");
		q.agregarParametroValues(""+AppConstantes.FEC_ACTUALIZACION+"", ""+AppConstantes.CURRENT_TIMESTAMP+"");
		q.addWhere("ID_CONVENIO_PF =" + idConvenio +" AND IND_ESTATUS= 1");
		String query = q.obtenerQueryActualizar();
		log.info("actualizar estatus convenio --> "+query);
		String encoded = encodedQuery(query);
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}
	
	public DatosRequest actualizarEstatusConvenio(RenovarPlanExtRequest extRequest, Integer idUsuario) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("UPDATE SVT_CONVENIO_PF");
		q.agregarParametroValues("ID_ESTATUS_CONVENIO", "2");
		if(extRequest.getIndRenovacion()==0) {
			q.agregarParametroValues("FEC_VIGENCIA", ""+AppConstantes.CURRENT_TIMESTAMP+"");	
		}
		q.agregarParametroValues(""+AppConstantes.ID_USUARIO_MODIFICA+"", ""+idUsuario+"");
		q.agregarParametroValues(""+AppConstantes.FEC_ACTUALIZACION+"", ""+AppConstantes.CURRENT_TIMESTAMP+"");
		q.addWhere("ID_CONVENIO_PF =" + extRequest.getIdConvenio());
		String query = q.obtenerQueryActualizar() + " $$ " + insertarJustificacion(extRequest.getIdConvenio(), extRequest.getJustificacion(), idUsuario);
		log.info(query);
		String encoded = encodedQuery(query);
		parametro.put(AppConstantes.QUERY, encoded);
		 parametro.put("separador","$$");
		request.setDatos(parametro);
		return request;
	}
	
	
	private String insertarJustificacion(Integer idConvenio, String justificacion, Integer idUsuario) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVT_RENOVACION_EXT_CONVENIO_PF");
		q.agregarParametroValues("ID_CONVENIO_PF", ""+idConvenio+"");
		q.agregarParametroValues("DES_JUSTIFICACION", "'"+justificacion+"'");
		q.agregarParametroValues(""+AppConstantes.ID_USUARIO_ALTA+"", ""+idUsuario+"");
		q.agregarParametroValues(""+AppConstantes.FEC_ALTA+"", ""+AppConstantes.CURRENT_TIMESTAMP+"");
		q.addWhere("ID_CONVENIO_PF =" + idConvenio);
		String query = q.obtenerQueryInsertar();
		log.info(query);
		String encoded = encodedQuery(query);
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return query;
	}

	private static String obtieneQuery(SelectQueryUtil queryUtil) {
        return queryUtil.build();
    }
	
	private static String encodedQuery(String query) {
        return DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
    }
	
}
