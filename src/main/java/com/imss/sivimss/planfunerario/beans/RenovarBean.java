package com.imss.sivimss.planfunerario.beans;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.planfunerario.model.request.FiltrosConvenioPFRequest;
import com.imss.sivimss.planfunerario.util.AppConstantes;
import com.imss.sivimss.planfunerario.util.DatosRequest;
import com.imss.sivimss.planfunerario.util.SelectQueryUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RenovarBean {

	public DatosRequest buscarNuevo(DatosRequest request, FiltrosConvenioPFRequest filtros) {
		// TODO Auto-generated method stub
		return null;
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
				 "SCP.FEC_INICIO AS fechaInicio",
				 "SCP.FEC_VIGENCIA AS fechaVigencia",
				 "SD.DES_CALLE AS calle",
				 "SD.NUM_EXTERIOR AS numExt",
				 "SD.NUM_INTERIOR AS numInt",
				 "CP.CVE_CODIGO_POSTAL AS cp",
				 "CP.DES_ESTADO AS estado",
				 "CP.DES_MNPIO AS municipio",
				 "SP.DES_TELEFONO AS telefono",
				 "SP.DES_CORREO AS correo")
		.from("SVT_CONVENIO_PF SCP")
		.join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF SCPC", "SCP.ID_CONVENIO_PF = SCPC.ID_CONVENIO_PF")
		.join("SVT_PAQUETE PAQ", "SCPC.ID_PAQUETE = PAQ.ID_PAQUETE")
		.join("SVC_CONTRATANTE SC", "SCPC.ID_CONTRATANTE = SC.ID_CONTRATANTE")
		.join("SVT_DOMICILIO SD", "SC.ID_DOMICILIO = SD.ID_DOMICILIO ")
		.join("SVC_CP CP", "SD.ID_CP = CP.ID_CODIGO_POSTAL")
		.join("SVC_PERSONA SP", "SC.ID_PERSONA = SP.ID_PERSONA");
		queryUtil.where("SCP.ID_TIPO_PREVISION = 0");
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

}
