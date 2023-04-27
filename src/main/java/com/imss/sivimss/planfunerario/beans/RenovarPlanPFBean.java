package com.imss.sivimss.planfunerario.beans;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.planfunerario.util.AppConstantes;
import com.imss.sivimss.planfunerario.util.DatosRequest;
import com.imss.sivimss.planfunerario.util.Response;
import com.imss.sivimss.planfunerario.util.SelectQueryUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RenovarPlanPFBean {

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
		String query = getQuery(queryUtil);
		log.info("estoy en: " +query);
		final String encoded = getBinary(query);
		Map<String, Object> parametros = new HashMap<>();
	    parametros.put(AppConstantes.QUERY, encoded);
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
	
	  private static String getBinary(String query) {
	        return DatatypeConverter.printBase64Binary(query.getBytes());
	    }

	private static String getQuery(SelectQueryUtil queryUtil) {
        return queryUtil.build();
    }

}
