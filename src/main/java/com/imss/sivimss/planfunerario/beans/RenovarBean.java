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
		queryUtil.select("SB.ID_CONVENIO_PF AS idCovenio, SB.ID_CONTRATANTE_BENEFICIARIOS AS idBenef, "
				+ "SP.NOM_PERSONA AS nombre , "
				+ "SP.NOM_PRIMER_APELLIDO AS primerApellido, "
				+ "SP.NOM_SEGUNDO_APELLIDO AS segundoApellido, "
				+ " TIMESTAMPDIFF(YEAR, SP.FEC_nac, CURRENT_TIMESTAMP()) AS edad,"
				+ " PAR.DES_PARENTESCO AS parentesco, "
				+ " SP.CVE_CURP AS curp, "
				+ " SP.CVE_RFC AS rfc, "
				+ " SP.DES_CORREO AS correo, "
				+ " SP.DES_TELEFONO AS tel, "
				+ " SB.CVE_ACTA AS acta,"
				+ " SP.ID_PERSONA AS idPersona, "
				+ " SB.IND_ACTIVO AS estatus")
		.from("SVT_CONTRATANTE_BENEFICIARIOS SB")
		.join("SVC_PERSONA SP", " SB.ID_PERSONA = SP.ID_PERSONA")
		.join("SVC_PARENTESCO PAR", "PAR.ID_PARENTESCO = SB.ID_PARENTESCO ");
		if(filtros.getNumeroConvenio()!=null && filtros.getNumeroContratante()==null) {
			queryUtil.where("SB.ID_CONVENIO_PF = :idConvenio")
			.setParameter("idConvenio", filtros.getNumeroConvenio());
		}
		else if(filtros.getNumeroContratante()!=null && filtros.getNumeroConvenio()==null) {
			queryUtil.where("SB.ID_CONVENIO_PF = :idNumeroContratante")
			.setParameter("idNumeroContratante", filtros.getNumeroContratante());
		}else if(filtros.getNumeroContratante()!=null && filtros.getNumeroConvenio()!=null) {
			queryUtil.where("SB.ID_CONVENIO_PF = :idConvenio").and("SB.ID_CONTRATANTE_BENEFICIARIOS = :idNumeroContratante")
			.setParameter("idConvenio", filtros.getNumeroConvenio())
			.setParameter("idNumeroContratante", filtros.getFolio());
		}
		String query = obtieneQuery(queryUtil);
		log.info("estoy en: " +query);
		Map<String, Object> parametros = new HashMap<>();
	    parametros.put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes()));
	    request.setDatos(parametros);
	    return request;
	}

	private static String obtieneQuery(SelectQueryUtil queryUtil) {
        return queryUtil.build();
    }

}
