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

	//TABLAS
	public static final String SVT_CONTRATANTE_BENEFICIARIOS = "SVT_CONTRATANTE_BENEFICIARIOS SB";
	public static final String SVT_CONTRATANTE_PAQUETE_CONVENIO_PF = "SVT_CONTRATANTE_PAQUETE_CONVENIO_PF SCPC";
	public static final String SVC_PERSONA = "SVC_PERSONA SP";
	public static final String SVT_CONVENIO_PF = "SVT_CONVENIO_PF PF";
	
	//COLUMNAS
	public static final String SP_ID_PERSONA = "SP.ID_PERSONA";
	
	//JOIN
	public static final String SB_ID_CONTRATANTE_PAQUETE_CONVENIO_PF_SCPC_ID_CONTRATANTE_PAQUETE_CONVENIO_PF = "SB.ID_CONTRATANTE_PAQUETE_CONVENIO_PF = SCPC.ID_CONTRATANTE_PAQUETE_CONVENIO_PF";
	public static final String 	SCPC_ID_CONVENIO_PF_PF_ID_CONVENIO_PF = "SCPC.ID_CONVENIO_PF = PF.ID_CONVENIO_PF";
	
	
	public DatosRequest beneficiarios(DatosRequest request) {
		Map<String, Object> parametros = new HashMap<>();
		String palabra = request.getDatos().get("palabra").toString();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("SCPC.ID_CONVENIO_PF AS idCovenio, SB.ID_CONTRATANTE_BENEFICIARIOS AS idBenef",
				"CONCAT(SP.NOM_PERSONA,' '",
				"SP.NOM_PRIMER_APELLIDO, ' '",
				"SP.NOM_SEGUNDO_APELLIDO) AS nombre",
				SP_ID_PERSONA+" AS idPersona",
				"SCPC.ID_CONTRATANTE_PAQUETE_CONVENIO_PF AS idContratanteConvenioPf")
		.from(SVT_CONTRATANTE_BENEFICIARIOS)
		.join(SVT_CONTRATANTE_PAQUETE_CONVENIO_PF, SB_ID_CONTRATANTE_PAQUETE_CONVENIO_PF_SCPC_ID_CONTRATANTE_PAQUETE_CONVENIO_PF)
		.join(SVC_PERSONA, " SB.ID_PERSONA = SP.ID_PERSONA")
		.join(SVT_CONVENIO_PF, SCPC_ID_CONVENIO_PF_PF_ID_CONVENIO_PF);
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
		.from(SVT_CONTRATANTE_BENEFICIARIOS)
		.join(SVT_CONTRATANTE_PAQUETE_CONVENIO_PF, SB_ID_CONTRATANTE_PAQUETE_CONVENIO_PF_SCPC_ID_CONTRATANTE_PAQUETE_CONVENIO_PF)
		.join(SVC_PERSONA, " SB.ID_PERSONA = SP.ID_PERSONA")
		.join("SVC_PARENTESCO PAR", "PAR.ID_PARENTESCO = SB.ID_PARENTESCO");
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
		q.agregarParametroValues(""+AppConstantes.ID_USUARIO_MODIFICA+"", ""+usuarioAlta+"");
		q.agregarParametroValues(""+AppConstantes.FEC_ACTUALIZACION+"", ""+AppConstantes.CURRENT_TIMESTAMP+"");
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
	        q.agregarParametroValues(""+AppConstantes.ID_USUARIO_MODIFICA+"", ""+idUsuario+"" );
			q.agregarParametroValues(""+AppConstantes.FEC_ACTUALIZACION+"", ""+AppConstantes.CURRENT_TIMESTAMP+"");
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
	        	  q.agregarParametroValues(""+AppConstantes.ID_USUARIO_MODIFICA+"", ""+usuarioBaja+"" );
	  			q.agregarParametroValues(""+AppConstantes.FEC_ACTUALIZACION+"", ""+AppConstantes.CURRENT_TIMESTAMP+"");
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
		Map<String, Object> parametros = new HashMap<>();
		SelectQueryUtil queryUno = new SelectQueryUtil();
		queryUno.select("SCPC.ID_CONVENIO_PF AS idCovenio",
				"SB.ID_CONTRATANTE_BENEFICIARIOS AS idBenef",
				"CONCAT(SP.NOM_PERSONA,' ',"
				+ "SP.NOM_PRIMER_APELLIDO, ' ',"
				+ "SP.NOM_SEGUNDO_APELLIDO) AS nombre",
				SP_ID_PERSONA +" AS idPersona")
		      .from("SVT_CONTRATANTE_BENEFICIARIOS SB")
		      .join(SVT_CONTRATANTE_PAQUETE_CONVENIO_PF, SB_ID_CONTRATANTE_PAQUETE_CONVENIO_PF_SCPC_ID_CONTRATANTE_PAQUETE_CONVENIO_PF)
		      .join(SVT_CONVENIO_PF, SCPC_ID_CONVENIO_PF_PF_ID_CONVENIO_PF)
		      .join(SVC_PERSONA, "SB.ID_PERSONA = SP.ID_PERSONA");
		queryUno.where("PF.ID_TIPO_PREVISION=2").and("SB.IND_SINIESTROS=0").and("SB.ID_PARENTESCO !=4")
		.and("PF.ID_CONVENIO_PF= "+palabra+"");
		SelectQueryUtil queryDos = new SelectQueryUtil();
		queryDos.select("SCPC.ID_CONVENIO_PF AS idCovenio",
				"SB.ID_CONTRATANTE_BENEFICIARIOS AS idBenef",
				"CONCAT(SP.NOM_PERSONA,' ', SP.NOM_PRIMER_APELLIDO, ' ', SP.NOM_SEGUNDO_APELLIDO) AS nombre",
				"SP.ID_PERSONA AS idPersona")
				.from("SVT_CONTRATANTE_BENEFICIARIOS SB")
				 .join(SVT_CONTRATANTE_PAQUETE_CONVENIO_PF, SB_ID_CONTRATANTE_PAQUETE_CONVENIO_PF_SCPC_ID_CONTRATANTE_PAQUETE_CONVENIO_PF)
			      .join(SVT_CONVENIO_PF, SCPC_ID_CONVENIO_PF_PF_ID_CONVENIO_PF)
			      .join("SVC_VALIDACION_DOCUMENTOS_CONVENIO_PF DOCPF", "PF.ID_CONVENIO_PF = DOCPF.ID_CONVENIO_PF")
			      .join("SVC_VALIDACION_DOCUMENTOS_RENOVACION_CONVENIO_PF RDOCPF", "DOCPF.ID_VALIDACION_DOCUMENTO = RDOCPF.ID_VALIDACION_DOCUMENTO")
			      .join(SVC_PERSONA, "SB.ID_PERSONA = SP.ID_PERSONA");
		           queryDos.where("(IF(SB.ID_PARENTESCO=4 AND TIMESTAMPDIFF(YEAR, SP.FEC_NAC, CURDATE())<18, SB.ID_PARENTESCO, NULL))")
		           .or("(SB.ID_PARENTESCO=4 AND SB.IND_SINIESTROS=0  AND PF.ID_CONVENIO_PF= "+palabra+" AND RDOCPF.IND_COMPROBANTE_ESTUDIOS_BENEFICIARIO = 1 "
		           		+ "AND TIMESTAMPDIFF(YEAR, SP.FEC_NAC, CURDATE()) BETWEEN 18 AND 25)");
		           final String query = queryUno.union(queryDos);
		log.info("estoy en --> " +query);
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
