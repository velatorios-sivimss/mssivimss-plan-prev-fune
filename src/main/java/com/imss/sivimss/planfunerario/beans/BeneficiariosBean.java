package com.imss.sivimss.planfunerario.beans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.planfunerario.exception.BadRequestException;
import com.imss.sivimss.planfunerario.model.request.PersonaRequest;
import com.imss.sivimss.planfunerario.model.request.FiltrosBeneficiariosRequest;
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
public class BeneficiariosBean {

	private Integer idBeneficiario;
	private Integer idPersona;
	private Integer idConvenioPf;
	private String nombre;
	private String apellidoP;
	private String apellidoM;
	private String fechaNac;
	private Integer idParentesco;
	private String curp;
	private String rfc;
	private String actaNac;
	private String correoE;
	private String tel;
	private Integer usuarioAlta;
	private Integer usuarioBaja;

	public BeneficiariosBean(PersonaRequest beneficiarioRequest) {
		this.idBeneficiario = beneficiarioRequest.getIdBeneficiario();
		this.idPersona = beneficiarioRequest.getIdPersona();
		this.idConvenioPf = beneficiarioRequest.getBeneficiario().getIdConvenioPF();
		this.nombre = beneficiarioRequest.getNombre();
		this.apellidoP = beneficiarioRequest.getApellidoP();
		this.apellidoM = beneficiarioRequest.getApellidoM();
		this.fechaNac = beneficiarioRequest.getFechaNac();
		this.idParentesco = beneficiarioRequest.getBeneficiario().getIdParentesco();
		this.curp = beneficiarioRequest.getCurp();
		this.rfc = beneficiarioRequest.getRfc();
		this.actaNac = beneficiarioRequest.getBeneficiario().getActaNac();
		this.correoE = beneficiarioRequest.getCorreoE();
		this.tel = beneficiarioRequest.getTel();
	}

	
	
	public DatosRequest beneficiarios(DatosRequest request) {
		String palabra = request.getDatos().get("palabra").toString();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("SB.ID_CONVENIO_PF AS idCovenio, SB.ID_BENEFICIARIO AS idBenef, "
				+ "CONCAT(SP.NOM_PERSONA,' ', "
				+ "SP.NOM_PRIMER_APELLIDO, ' ', "
				+ "SP.NOM_SEGUNDO_APELLIDO) AS nombre, "
				+ "SP.ID_PERSONA AS idPersona ")
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
				+ " SB.CVE_ACTA AS acta,"
				+ " SP.ID_PERSONA AS idPersona, "
				+ " SB.CVE_ESTATUS AS estatus")
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



	public DatosRequest insertarPersona() {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVC_PERSONA ");
		q.agregarParametroValues(" NOM_PERSONA", "'" + this.nombre + "'");
		q.agregarParametroValues("NOM_PRIMER_APELLIDO", "'" + this.apellidoP + "'");
		q.agregarParametroValues("NOM_SEGUNDO_APELLIDO", "'" + this.apellidoM + "'");
		q.agregarParametroValues("FEC_NAC", "'" + this.fechaNac + "'");
		q.agregarParametroValues("CVE_CURP", "'"+ this.curp + "'");
		q.agregarParametroValues("CVE_RFC", "'" +this.rfc +"'");
		q.agregarParametroValues("DES_CORREO", "'"+ this.correoE +"'");
		q.agregarParametroValues("DES_TELEFONO", "'" + this.tel + "'");
		q.agregarParametroValues("ID_USUARIO_ALTA", ""+usuarioAlta+"");
		q.agregarParametroValues("FEC_ALTA", " CURRENT_TIMESTAMP() ");
		String query = q.obtenerQueryInsertar() +"$$"  + insertarBeneficiario(this.idConvenioPf, this.idParentesco, this.actaNac);
			  String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
		        parametro.put(AppConstantes.QUERY, encoded);
		        parametro.put("separador","$$");
		        parametro.put("replace","idTabla");
		        request.setDatos(parametro);
		
		return request;
	}



	private String insertarBeneficiario(Integer idConvenioPf, Integer parentesco, String actaNac) {
		 DatosRequest request = new DatosRequest();
	        Map<String, Object> parametro = new HashMap<>();
	        final QueryHelper q = new QueryHelper("INSERT INTO SVC_BENEFICIARIO");
	        q.agregarParametroValues("ID_CONVENIO_PF", ""+idConvenioPf+"");
	        q.agregarParametroValues("ID_PERSONA", "idTabla");
	        q.agregarParametroValues("ID_PARENTESCO", ""+parentesco+"");
	        q.agregarParametroValues("CVE_ACTA", "'"+actaNac+"'");
	        q.agregarParametroValues("CVE_Estatus", "1");
	        q.agregarParametroValues("ID_USUARIO_ALTA", ""+usuarioAlta+"" );
			q.agregarParametroValues("FEC_ALTA", " CURRENT_TIMESTAMP() ");
	        String query = q.obtenerQueryInsertar();
	        String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
	        parametro.put(AppConstantes.QUERY, encoded);
	        request.setDatos(parametro);
	        return query;
	}



	public DatosRequest editarPersona() {
		DatosRequest request= new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("UPDATE SVC_PERSONA ");
		q.agregarParametroValues(" NOM_PERSONA", "'" + this.nombre + "'");
		q.agregarParametroValues("NOM_PRIMER_APELLIDO", "'" + this.apellidoP + "'");
		q.agregarParametroValues("NOM_SEGUNDO_APELLIDO", "'" + this.apellidoM + "'");
		q.agregarParametroValues("FEC_NAC", "'" + this.fechaNac + "'");
		q.agregarParametroValues("CVE_CURP", "'"+ this.curp + "'");
		q.agregarParametroValues("CVE_RFC", "'" +this.rfc +"'");
		q.agregarParametroValues("DES_CORREO", "'"+ this.correoE +"'");
		q.agregarParametroValues("DES_TELEFONO", "'" + this.tel + "'");
		q.agregarParametroValues("ID_USUARIO_MODIFICA", ""+usuarioAlta+"");
		q.agregarParametroValues("FEC_ACTUALIZACION", " CURRENT_TIMESTAMP() ");
		q.addWhere("ID_PERSONA = " + this.idPersona);
		String query = q.obtenerQueryActualizar();
		parametro.put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes()));
		request.setDatos(parametro);
		return request;
	}



	public DatosRequest editarBeneficiario(Integer idPersona, Integer idUsuario, Integer parentesco, String acta) {
		 DatosRequest request = new DatosRequest();
	        Map<String, Object> parametro = new HashMap<>();
	        final QueryHelper q = new QueryHelper("UPDATE SVC_BENEFICIARIO");
	        q.agregarParametroValues("ID_PARENTESCO", ""+parentesco+"");
	        q.agregarParametroValues("CVE_ACTA", "'"+acta+"'");
	        q.agregarParametroValues("CVE_Estatus", "1");
	        q.agregarParametroValues("ID_USUARIO_MODIFICA", ""+idUsuario+"" );
			q.agregarParametroValues("FEC_ACTUALIZACION", " CURRENT_TIMESTAMP() ");
			q.addWhere("ID_PERSONA = " + idPersona);
	        String query = q.obtenerQueryActualizar();
	        String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
	        parametro.put(AppConstantes.QUERY, encoded);
	        request.setDatos(parametro);
	        return request;
	}



	public  DatosRequest cambiarEstatus(int idBeneficiario) {
		 DatosRequest request = new DatosRequest();
	        Map<String, Object> parametro = new HashMap<>();
	        final QueryHelper q = new QueryHelper("UPDATE SVC_BENEFICIARIO");
	        q.agregarParametroValues("CVE_ESTATUS", "!CVE_ESTATUS");
	        q.agregarParametroValues("ID_USUARIO_BAJA", ""+usuarioBaja+"" );
			q.agregarParametroValues("FEC_BAJA", " CURRENT_TIMESTAMP() ");
			q.addWhere("ID_BENEFICIARIO = " + idBeneficiario);
	        String query = q.obtenerQueryActualizar();
	        String encoded = DatatypeConverter.printBase64Binary(query.getBytes());
	        parametro.put(AppConstantes.QUERY, encoded);
	        request.setDatos(parametro);
	        return request;
	}
}
