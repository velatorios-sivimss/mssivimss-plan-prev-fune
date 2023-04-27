package com.imss.sivimss.planfunerario.beans;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;
import com.imss.sivimss.planfunerario.model.request.FiltrosBeneficiariosRequest;
import com.imss.sivimss.planfunerario.util.AppConstantes;
import com.imss.sivimss.planfunerario.util.DatosRequest;
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
public class RenovarPlanPFBean {

	private Integer idBeneficiario;
	private Integer idConvenioPF;

	public DatosRequest beneficiarios(DatosRequest request) {
		String palabra = request.getDatos().get("palabra").toString();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("SB.ID_CONVENIO_PF AS idCovenio, SB.ID_BENEFICIARIO AS idBenef, "
				+ "CONCAT(SP.NOM_PERSONA,' ', "
				+ "SP.NOM_PRIMER_APELLIDO, ' ', "
				+ "SP.NOM_SEGUNDO_APELLIDO) AS nombre")
		.from("SVC_BENEFICIARIO SB")
		.join("SVC_PERSONA SP", " SB.ID_PERSONA = SP.ID_PERSONA");
		queryUtil.where("SB.ID_CONVENIO_PF = :idConvenio")
		.setParameter("idConvenio", Integer.parseInt(palabra));
		String query = obtieneQuery(queryUtil);
		log.info("estoy en: " +query);
		Map<String, Object> parametros = new HashMap<>();
	    parametros.put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes()));
	    request.setDatos(parametros);
	    return request;
	}
	
/*	public DatosRequest beneficiarios(DatosRequest request) {
		String palabra = request.getDatos().get("palabra").toString();
	String query = " SELECT SB.ID_CONVENIO_PF AS idCovenio, SB.ID_BENEFICIARIO AS idBenef, "
				+ "CONCAT(SP.NOM_PERSONA,' ', "
				+ "SP.NOM_PRIMER_APELLIDO, ' ', "
				+ "SP.NOM_SEGUNDO_APELLIDO) AS nombre "
				+ " FROM SVC_BENEFICIARIO SB "
				+ "JOIN SVC_PERSONA SP ON SB.ID_PERSONA = SP.ID_PERSONA "
				+ " WHERE SB.ID_CONVENIO_PF = "+ Integer.parseInt(palabra) +"";
	log.info(query);
	request.getDatos().remove("palabra");
	request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes()));
	return request;
	} */

	

	public DatosRequest detalleBeneficiarios(DatosRequest request, Integer idBeneficiario, Integer idConvenio) {
		log.info("estoy aqui" +idConvenioPF);
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("SB.ID_CONVENIO_PF AS idCovenio, SB.ID_BENEFICIARIO AS idBenef, "
				+ "SP.NOM_PERSONA AS nombre , "
				+ "SP.NOM_PRIMER_APELLIDO AS primerApellido, "
				+ "SP.NOM_SEGUNDO_APELLIDO AS segundoApellido, "
				+ " TIMESTAMPDIFF(YEAR, SP.FEC_nac, CURRENT_TIMESTAMP()) AS edad,"
				+ " PAR.DES_PARENTESCO AS parentesco, "
				+ " SP.CVE_CURP AS curp, "
				+ " SP.CVE_RFC AS rfc, "
				+ " SP.DES_CORREO AS correo, "
				+ " SP.DES_TELEFONO AS tel, "
				+ " SB.CVE_ACTA AS acta ")
		.from("SVC_BENEFICIARIO SB")
		.join("SVC_PERSONA SP", " SB.ID_PERSONA = SP.ID_PERSONA")
		.join("SVC_PARENTESCO PAR", "PAR.ID_PARENTESCO = SB.ID_PARENTESCO ");
		queryUtil.where("SB.ID_CONVENIO_PF = :idConvenio").and("SB.ID_BENEFICIARIO = :idBeneficiario")
		.setParameter("idConvenio", idConvenio)
		.setParameter("idBeneficiario", idBeneficiario);
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
