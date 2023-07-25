package com.imss.sivimss.planfunerario.beans;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.planfunerario.model.request.FiltrosConvenioExtRequest;
import com.imss.sivimss.planfunerario.util.AppConstantes;
import com.imss.sivimss.planfunerario.util.DatosRequest;
import com.imss.sivimss.planfunerario.util.SelectQueryUtil;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RenovarExt {

	public DatosRequest buscarConvenioExt(DatosRequest request, FiltrosConvenioExtRequest filtros) {
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
				"IF(PF.IND_RENOVACION=0, (DATE_FORMAT(PF.FEC_INICIO, '%d/%m/%Y')), DATE_FORMAT(RPF.FEC_INICIO, '%d/%m/%Y')) AS fecInicio",
				"IF(PF.IND_RENOVACION=0, (DATE_FORMAT(PF.FEC_VIGENCIA, '%d/%m/%Y')), DATE_FORMAT(RPF.FEC_VIGENCIA, '%d/%m/%Y')) AS fecVigencia")
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
	
	private static String obtieneQuery(SelectQueryUtil queryUtil) {
        return queryUtil.build();
    }
	
	private static String encodedQuery(String query) {
        return DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
    }

}
