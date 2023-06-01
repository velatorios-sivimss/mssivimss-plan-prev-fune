package com.imss.sivimss.planfunerario.beans;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.planfunerario.model.request.PersonaRequest;
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
	private Integer idContratanteConvenioPf;
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
		this.idContratanteConvenioPf = beneficiarioRequest.getBeneficiario().getIdContratanteConvenioPf();
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
		Map<String, Object> parametros = new HashMap<>();
		String palabra = request.getDatos().get("palabra").toString();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("SCPC.ID_CONVENIO_PF AS idCovenio, SB.ID_CONTRATANTE_BENEFICIARIOS AS idBenef",
				"CONCAT(SP.NOM_PERSONA,' '",
				"SP.NOM_PRIMER_APELLIDO, ' '",
				"SP.NOM_SEGUNDO_APELLIDO) AS nombre",
				"SP.ID_PERSONA AS idPersona",
				"SCPC.ID_CONTRATANTE_PAQUETE_CONVENIO_PF AS idContratanteConvenioPf")
		.from("SVT_CONTRATANTE_BENEFICIARIOS SB")
		.join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF SCPC", "SB.ID_CONTRATANTE_PAQUETE_CONVENIO_PF = SCPC.ID_CONTRATANTE_PAQUETE_CONVENIO_PF")
		.join("SVC_PERSONA SP", " SB.ID_PERSONA = SP.ID_PERSONA")
		.join("SVT_CONVENIO_PF PF", "SCPC.ID_CONVENIO_PF = PF.ID_CONVENIO_PF");
	//	queryUtil.where("PF.ID_TIPO_PREVISION= 1");
		queryUtil.where("SCPC.ID_CONVENIO_PF = :idConvenio").and("SB.IND_SINIESTROS=0")
		.setParameter("idConvenio", Integer.parseInt(palabra));
		String query = obtieneQuery(queryUtil);
		log.info("-> " +query);
		String encoded = encodedQuery(query);
	    parametros.put(AppConstantes.QUERY, encoded);
	    request.setDatos(parametros);
	    return request;
	}
		
	public DatosRequest detalleBeneficiarios(DatosRequest request, Integer idBeneficiario, Integer idConvenio) {
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("SCPC.ID_CONTRATANTE_PAQUETE_CONVENIO_PF AS idCovenio, SB.ID_CONTRATANTE_BENEFICIARIOS AS idBenef",
				 "SP.NOM_PERSONA AS nombre",
				 "SP.NOM_PRIMER_APELLIDO AS primerApellido",
				 "SP.NOM_SEGUNDO_APELLIDO AS segundoApellido",
				 " TIMESTAMPDIFF(YEAR, SP.FEC_nac, CURRENT_TIMESTAMP()) AS edad",
			    " PAR.DES_PARENTESCO AS parentesco",
				 " SP.CVE_CURP AS curp",
				 " SP.CVE_RFC AS rfc",
				 " SP.DES_CORREO AS correo",
				 " SP.DES_TELEFONO AS tel",
				 " SB.CVE_ACTA AS acta",
				 " SP.ID_PERSONA AS idPersona",
				 " SB.IND_ACTIVO AS estatus")
		.from("SVT_CONTRATANTE_BENEFICIARIOS SB")
		.join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF SCPC", "SB.ID_CONTRATANTE_PAQUETE_CONVENIO_PF = SCPC.ID_CONTRATANTE_PAQUETE_CONVENIO_PF")
		.join("SVC_PERSONA SP", " SB.ID_PERSONA = SP.ID_PERSONA")
		.join("SVC_PARENTESCO PAR", "PAR.ID_PARENTESCO = SB.ID_PARENTESCO ");
		queryUtil.where("SCPC.ID_CONVENIO_PF = :idConvenio").and("SB.ID_CONTRATANTE_BENEFICIARIOS = :idBeneficiario")
		.setParameter("idConvenio", idConvenio)
		.setParameter("idBeneficiario", idBeneficiario);
		String query = obtieneQuery(queryUtil);
		log.info("estoy en: " +query);
		Map<String, Object> parametros = new HashMap<>();
		 String encoded = encodedQuery(query);
	    parametros.put(AppConstantes.QUERY, encoded);
	    request.setDatos(parametros);
	    return request;
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
		q.agregarParametroValues("FEC_ALTA", ""+AppConstantes.CURRENT_TIMESTAMP+"");
		String query = q.obtenerQueryInsertar() +"$$"  + insertarBeneficiario(this.idContratanteConvenioPf, this.idParentesco, this.actaNac);
		log.info(query);
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		        parametro.put(AppConstantes.QUERY, encoded);
		        parametro.put("separador","$$");
		        parametro.put("replace","idTabla");
		        request.setDatos(parametro);
		
		return request;
	}



	private String insertarBeneficiario(Integer idContratanteConvenioPf, Integer parentesco, String actaNac) {
		 DatosRequest request = new DatosRequest();
	        Map<String, Object> parametro = new HashMap<>();
	        final QueryHelper q = new QueryHelper("INSERT INTO SVT_CONTRATANTE_BENEFICIARIOS");
	        q.agregarParametroValues("ID_CONTRATANTE_PAQUETE_CONVENIO_PF", ""+idContratanteConvenioPf+"");
	        q.agregarParametroValues("ID_PERSONA", "idTabla");
	        q.agregarParametroValues("ID_PARENTESCO", ""+parentesco+"");
	        q.agregarParametroValues("CVE_ACTA", "'"+actaNac+"'");
	        q.agregarParametroValues(""+AppConstantes.IND_ACTIVO+"", "1");
	        q.agregarParametroValues("IND_SINIESTROS", "0");
	        q.agregarParametroValues("ID_USUARIO_ALTA", ""+usuarioAlta+"" );
			q.agregarParametroValues("FEC_ALTA", ""+AppConstantes.CURRENT_TIMESTAMP+"");
	        String query = q.obtenerQueryInsertar();
	        String encoded = encodedQuery(query);
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
		q.agregarParametroValues("FEC_ACTUALIZACION", ""+AppConstantes.CURRENT_TIMESTAMP+"");
		q.addWhere("ID_PERSONA = " + this.idPersona);
		String query = q.obtenerQueryActualizar();
		String encoded = encodedQuery(query);
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}



	public DatosRequest editarBeneficiario(Integer idPersona, Integer idUsuario, Integer parentesco, String acta) {
		 DatosRequest request = new DatosRequest();
	        Map<String, Object> parametro = new HashMap<>();
	        final QueryHelper q = new QueryHelper("UPDATE SVT_CONTRATANTE_BENEFICIARIOS");
	        q.agregarParametroValues("ID_PARENTESCO", ""+parentesco+"");
	        q.agregarParametroValues("CVE_ACTA", "'"+acta+"'");
	        q.agregarParametroValues(""+AppConstantes.IND_ACTIVO+"", "1");
	        q.agregarParametroValues("ID_USUARIO_MODIFICA", ""+idUsuario+"" );
			q.agregarParametroValues("FEC_ACTUALIZACION", ""+AppConstantes.CURRENT_TIMESTAMP+"");
			q.addWhere("ID_PERSONA = " + idPersona);
	        String query = q.obtenerQueryActualizar();
	        String encoded = encodedQuery(query);
	        parametro.put(AppConstantes.QUERY, encoded);
	        request.setDatos(parametro);
	        return request;
	}



	public  DatosRequest cambiarEstatus(int idBeneficiario, boolean estatus) {
		 DatosRequest request = new DatosRequest();
	        Map<String, Object> parametro = new HashMap<>();
	        final QueryHelper q = new QueryHelper("UPDATE SVT_CONTRATANTE_BENEFICIARIOS");
	        q.agregarParametroValues(""+AppConstantes.IND_ACTIVO+"", ""+estatus+"");
	        if(!estatus) {
	        	 q.agregarParametroValues("ID_USUARIO_BAJA", ""+usuarioBaja+"" );
	 			q.agregarParametroValues("FEC_BAJA", ""+AppConstantes.CURRENT_TIMESTAMP+"");
	        }else {
	        	  q.agregarParametroValues("ID_USUARIO_MODIFICA", ""+usuarioBaja+"" );
	  			q.agregarParametroValues("FEC_ACTUALIZACION", ""+AppConstantes.CURRENT_TIMESTAMP+"");
	        }
			q.addWhere("ID_CONTRATANTE_BENEFICIARIOS = " + idBeneficiario);
	        String query = q.obtenerQueryActualizar();
	        log.info(query);
	        String encoded = encodedQuery(query);
	        parametro.put(AppConstantes.QUERY, encoded);
	        request.setDatos(parametro);
	        return request;
	}
	
	public DatosRequest beneficiariosPlanAnterior(DatosRequest request) {
		String palabra = request.getDatos().get("palabra").toString();
		String query = "SELECT SCPC.ID_CONVENIO_PF AS idCovenio, SB.ID_CONTRATANTE_BENEFICIARIOS AS idBenef, "
				+ "CONCAT(SP.NOM_PERSONA,' ', "
				+ "SP.NOM_PRIMER_APELLIDO, ' ', "
				+ "SP.NOM_SEGUNDO_APELLIDO) AS nombre, "
				+ "SP.ID_PERSONA AS idPersona "
				+ "FROM SVT_CONTRATANTE_BENEFICIARIOS SB "
				+ "JOIN SVT_CONTRATANTE_PAQUETE_CONVENIO_PF SCPC ON SB.ID_CONTRATANTE_PAQUETE_CONVENIO_PF = SCPC.ID_CONTRATANTE_PAQUETE_CONVENIO_PF "
				+ "JOIN SVT_CONVENIO_PF PF ON SCPC.ID_CONVENIO_PF = PF.ID_CONVENIO_PF "
				+ "JOIN SVC_PERSONA SP ON SB.ID_PERSONA = SP.ID_PERSONA "
				+ "WHERE PF.ID_TIPO_PREVISION=2 AND SB.ID_PARENTESCO !=4 "
				+ "AND PF.ID_CONVENIO_PF= '"+palabra+"' "
						+ "UNION "
						+ "SELECT SCPC.ID_CONVENIO_PF AS idCovenio, SB.ID_CONTRATANTE_BENEFICIARIOS AS idBenef, "
						+ "CONCAT(SP.NOM_PERSONA,' ', "
						+ "SP.NOM_PRIMER_APELLIDO, ' ', "
						+ "SP.NOM_SEGUNDO_APELLIDO) AS nombre, "
						+ "SP.ID_PERSONA AS idPersona "
						+ "FROM SVT_CONTRATANTE_BENEFICIARIOS SB "
						+ "JOIN SVT_CONTRATANTE_PAQUETE_CONVENIO_PF SCPC ON SB.ID_CONTRATANTE_PAQUETE_CONVENIO_PF = SCPC.ID_CONTRATANTE_PAQUETE_CONVENIO_PF "
						+ "JOIN SVT_CONVENIO_PF PF ON SCPC.ID_CONVENIO_PF = PF.ID_CONVENIO_PF "
						+ "JOIN SVC_PERSONA SP ON SB.ID_PERSONA = SP.ID_PERSONA "
						+ "WHERE PF.ID_CONVENIO_PF= '"+palabra+"' AND PF.ID_TIPO_PREVISION=2 "
								+ "AND (IF(SB.ID_PARENTESCO=4 AND TIMESTAMPDIFF(YEAR, SP.FEC_NAC, CURDATE())<18, SB.ID_PARENTESCO, NULL)) "
								+ "OR (SB.ID_PARENTESCO=4 AND SB.CVE_ACTA IS NOT NULL "
								+ "AND TIMESTAMPDIFF(YEAR, SP.FEC_NAC, CURDATE()) BETWEEN 18 AND 25) ";
		log.info("estoy en: " +query);
		Map<String, Object> parametros = new HashMap<>();
	   String encoded = encodedQuery(query);
	   parametros.put(AppConstantes.QUERY, encoded);
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
