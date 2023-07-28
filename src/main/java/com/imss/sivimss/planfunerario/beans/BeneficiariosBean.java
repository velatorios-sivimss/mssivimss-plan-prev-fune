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
	private Integer indActa;
	private Integer indIne;
	private String correoE;
	private String tel;
	private Integer usuarioAlta;
	private Integer usuarioBaja;
	
 
	private Integer indComprobanteEstudios;
	private Integer indActaMatrimonio;
	private Integer indDeclaracionConcubinato;
	private Integer indCartaPoder;
	private Integer indIneTestigo;
	private Integer indIneTestigoDos;

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
		this.indActa = beneficiarioRequest.getBeneficiario().getIndActa();
		this.indIne = beneficiarioRequest.getBeneficiario().getIndIne();
		/*this.indComprobanteEstudios = beneficiarioRequest.getDocPlanAnterior().getIndComprobanteEstudios();
		this.indActaMatrimonio = beneficiarioRequest.getDocPlanAnterior().getIndActaMatrimonio();
		this.indDeclaracionConcubinato = beneficiarioRequest.getDocPlanAnterior().getIndDeclaracionConcubinato();
		this.indCartaPoder = beneficiarioRequest.getIndCartaPoder();
		this.indIneTestigo = beneficiarioRequest.getIndIneTestigo();
		this.indIneTestigoDos = beneficiarioRequest.getIndIneTestigoDos();*/
 
	}

	//TABLAS
	public static final String SVT_CONTRATANTE_BENEFICIARIOS = "SVT_CONTRATANTE_BENEFICIARIOS SB";
	public static final String SVT_CONTRATANTE_PAQUETE_CONVENIO_PF = "SVT_CONTRATANTE_PAQUETE_CONVENIO_PF SCPC";
	public static final String SVC_PERSONA = "SVC_PERSONA SP";
	public static final String SVT_CONVENIO_PF = "SVT_CONVENIO_PF PF";
	
	//COLUMNAS
	public static final String SP_ID_PERSONA = "SP.ID_PERSONA";
	public static final String SB_ID_CONTRATANTE_BENEFICIARIOS = "SB.ID_CONTRATANTE_BENEFICIARIOS";
	public static final String ID_PARENTESCO = "ID_PARENTESCO";
	public static final String NOM_PERSONA = "NOM_PERSONA";
	public static final String NOM_PRIMER_APELLIDO = "NOM_PRIMER_APELLIDO";
	public static final String NOM_SEGUNDO_APELLIDO = "NOM_SEGUNDO_APELLIDO";
	public static final String FEC_NAC = "FEC_NAC";
	public static final String CVE_CURP = "CVE_CURP";
	public static final String CVE_RFC = "CVE_RFC";
	public static final String DES_CORREO = "DES_CORREO";
	public static final String DES_TELEFONO = "DES_TELEFONO";
	public static final String ID_TABLA = "idTabla";

	public static final String IND_ACTA_NACIMIENTO = "IND_ACTA_NACIMIENTO";
	public static final String IND_INE_BENEFICIARIO = "IND_INE_BENEFICIARIO";
 
	
	//JOIN
	public static final String SB_ID_CONTRATANTE_PAQUETE_CONVENIO_PF_SCPC_ID_CONTRATANTE_PAQUETE_CONVENIO_PF = "SB.ID_CONTRATANTE_PAQUETE_CONVENIO_PF = SCPC.ID_CONTRATANTE_PAQUETE_CONVENIO_PF";
	public static final String 	SCPC_ID_CONVENIO_PF_PF_ID_CONVENIO_PF = "SCPC.ID_CONVENIO_PF = PF.ID_CONVENIO_PF";
	
	
	public DatosRequest beneficiarios(DatosRequest request, String palabra) {
		Map<String, Object> parametros = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("SCPC.ID_CONVENIO_PF AS idConvenio",
				"SCPC.ID_CONTRATANTE_PAQUETE_CONVENIO_PF AS idContratanteConvenioPf")
		.from(SVT_CONTRATANTE_PAQUETE_CONVENIO_PF)
		.join(SVT_CONVENIO_PF, SCPC_ID_CONVENIO_PF_PF_ID_CONVENIO_PF)
		.leftJoin(SVT_CONTRATANTE_BENEFICIARIOS, "SCPC.ID_CONTRATANTE_PAQUETE_CONVENIO_PF = SB.ID_CONTRATANTE_PAQUETE_CONVENIO_PF")
		.leftJoin(SVC_PERSONA, "SB.ID_PERSONA=SP.ID_PERSONA");
      	//queryUtil.where("PF.ID_TIPO_PREVISION= 1");
		queryUtil.where("SCPC.ID_CONVENIO_PF = :idConvenio")
		//.and("(SB.IND_SINIESTROS=0 OR SB.IND_SINIESTROS IS NULL)")
		.setParameter("idConvenio", Integer.parseInt(palabra));
		String query = obtieneQuery(queryUtil);
		log.info("-> " +query);
		String encoded = encodedQuery(query);
	    parametros.put(AppConstantes.QUERY, encoded);
	    request.setDatos(parametros);
	    return request;
	}
	
	public  DatosRequest buscarBeneficiarios(DatosRequest request, String palabra) {
		Map<String, Object> parametros = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select(SB_ID_CONTRATANTE_BENEFICIARIOS + " AS id",
				 "CONCAT(SP.NOM_PERSONA,' ',"
				+ "SP.NOM_PRIMER_APELLIDO, ' ',"
				+ "SP.NOM_SEGUNDO_APELLIDO) AS nombreBeneficiario",
				 " SP.ID_PERSONA AS idPersona")
		.from(SVT_CONTRATANTE_PAQUETE_CONVENIO_PF)
		.join(SVT_CONVENIO_PF, SCPC_ID_CONVENIO_PF_PF_ID_CONVENIO_PF)
		.join(SVT_CONTRATANTE_BENEFICIARIOS, "SCPC.ID_CONTRATANTE_PAQUETE_CONVENIO_PF = SB.ID_CONTRATANTE_PAQUETE_CONVENIO_PF")
		.join(SVC_PERSONA, "SB.ID_PERSONA=SP.ID_PERSONA");
		queryUtil.where("SCPC.ID_CONVENIO_PF = :idConvenio").and("SB.IND_ACTIVO=1").and("(SB.IND_SINIESTROS=0 OR SB.IND_SINIESTROS IS NULL)")
		.setParameter("idConvenio", Integer.parseInt(palabra));
		String query = obtieneQuery(queryUtil);
		log.info("-> " +query);
		String encoded = encodedQuery(query);
	    parametros.put(AppConstantes.QUERY, encoded);
	    request.setDatos(parametros);
		return request;
	}
		
	public DatosRequest detalleBeneficiarios(DatosRequest request, Integer idBeneficiario) {
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("SCPC.ID_CONTRATANTE_PAQUETE_CONVENIO_PF AS idConvenio", 
				SB_ID_CONTRATANTE_BENEFICIARIOS +" AS idBenef",
				 "SP.NOM_PERSONA AS nombre",
				 "SP.NOM_PRIMER_APELLIDO AS primerApellido",
				 "SP.NOM_SEGUNDO_APELLIDO AS segundoApellido",
				 "TIMESTAMPDIFF(YEAR, SP.FEC_NAC, CURRENT_TIMESTAMP()) AS edad",
				 "SB.ID_PARENTESCO AS idParentesco",
			    "PAR.DES_PARENTESCO AS parentesco",
				 "SP.CVE_CURP AS curp",
				 "SP.CVE_RFC AS rfc",
				 "SP.DES_CORREO AS correo",
				 "SP.DES_TELEFONO AS tel",
				 "SB.IND_ACTA_NACIMIENTO AS indActa",
				 "SB.IND_INE_BENEFICIARIO AS indIne",
				 "SP.ID_PERSONA AS idPersona",
				 "SB.IND_ACTIVO AS estatus",
				 "SBD.IND_COMPROBANTE_ESTUDIOS AS comprobEstudios",
				  "SBD.IND_ACTA_MATRIMONIO AS actaMatrimonio",
				  "SBD.IND_DECLARACION_CONCUBINATO AS declaracionConcubinato")
		.from(SVT_CONTRATANTE_BENEFICIARIOS)
		.join(SVT_CONTRATANTE_PAQUETE_CONVENIO_PF, SB_ID_CONTRATANTE_PAQUETE_CONVENIO_PF_SCPC_ID_CONTRATANTE_PAQUETE_CONVENIO_PF)
		.leftJoin("SVT_BENEFICIARIOS_DOCUMENTACION_PLAN_ANTERIOR SBD", "SB.ID_CONTRATANTE_BENEFICIARIOS = SBD.ID_CONTRATANTE_BENEFICIARIOS")
		.join(SVC_PERSONA, " SB.ID_PERSONA = SP.ID_PERSONA")
		.join("SVC_PARENTESCO PAR", "PAR.ID_PARENTESCO = SB.ID_PARENTESCO");
		queryUtil.where("SB.ID_CONTRATANTE_BENEFICIARIOS = :idBeneficiario")
		//.setParameter("idConvenio", idConvenio)
		.setParameter("idBeneficiario", idBeneficiario);
		String query = obtieneQuery(queryUtil);
		log.info("estoy en: " +query);
		Map<String, Object> parametros = new HashMap<>();
		 String encoded = encodedQuery(query);
	    parametros.put(AppConstantes.QUERY, encoded);
	    request.setDatos(parametros);
	    return request;
	}

	public DatosRequest insertarPersonaPlanAnterior() {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVC_PERSONA ");
		q.agregarParametroValues(NOM_PERSONA, setValor(this.nombre));
		q.agregarParametroValues(NOM_PRIMER_APELLIDO, setValor(this.apellidoP));
		q.agregarParametroValues(NOM_SEGUNDO_APELLIDO, setValor(this.apellidoM));
		q.agregarParametroValues(FEC_NAC, setValor(this.fechaNac));
		q.agregarParametroValues(CVE_CURP, "'"+ this.curp + "'");
		q.agregarParametroValues(CVE_RFC, setValor(this.rfc));
		q.agregarParametroValues(DES_CORREO, setValor(this.correoE));
		q.agregarParametroValues(DES_TELEFONO, setValor(this.tel));
		q.agregarParametroValues(AppConstantes.ID_USUARIO_ALTA, ""+usuarioAlta+"");
		q.agregarParametroValues(AppConstantes.FEC_ALTA, ""+AppConstantes.CURRENT_TIMESTAMP+"");
		String query = q.obtenerQueryInsertar();
		log.info(query);
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		        parametro.put(AppConstantes.QUERY, encoded);
		        request.setDatos(parametro);
		return request;
	}



	public  DatosRequest insertarBeneficiarioPlanAnterior(Integer id) {
		 DatosRequest request = new DatosRequest();
	        Map<String, Object> parametro = new HashMap<>();
	        final QueryHelper q = new QueryHelper("INSERT INTO SVT_CONTRATANTE_BENEFICIARIOS");
	        q.agregarParametroValues("ID_CONTRATANTE_PAQUETE_CONVENIO_PF", ""+this.idContratanteConvenioPf+"");
	        q.agregarParametroValues("ID_PERSONA", ""+id+"");
	        q.agregarParametroValues(ID_PARENTESCO, ""+this.idParentesco+"");
	        if(indActa!=null) {
	        	q.agregarParametroValues(IND_ACTA_NACIMIENTO, ""+this.indActa+"");	
	        }
	        if(indIne!=null) {
	        	   q.agregarParametroValues(IND_INE_BENEFICIARIO, ""+this.indIne+"");   	
 	        }
	        q.agregarParametroValues(""+AppConstantes.IND_ACTIVO+"", "1");
	        q.agregarParametroValues("IND_SINIESTROS", "0");
	        q.agregarParametroValues(AppConstantes.ID_USUARIO_ALTA, ""+usuarioAlta+"" );
			q.agregarParametroValues(AppConstantes.FEC_ALTA, ""+AppConstantes.CURRENT_TIMESTAMP+"");
	        String query = q.obtenerQueryInsertar()+"$$" +insertarDocPlanAnterior();
	        log.info("estoy aqui "+query);
	        String encoded = encodedQuery(query);
	        parametro.put("separador","$$");
		    parametro.put("replace",ID_TABLA);
	        parametro.put(AppConstantes.QUERY, encoded);
	        request.setDatos(parametro);
	        return request;
	}


	public String insertarDocPlanAnterior() {
		 DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
        final QueryHelper q = new QueryHelper("INSERT INTO SVT_BENEFICIARIOS_DOCUMENTACION_PLAN_ANTERIOR");
        q.agregarParametroValues("ID_CONTRATANTE_BENEFICIARIOS", ID_TABLA);
        q.agregarParametroValues("IND_COMPROBANTE_ESTUDIOS", ""+this.indComprobanteEstudios+"");
        q.agregarParametroValues("IND_ACTA_MATRIMONIO", ""+this.indActaMatrimonio+"");
        q.agregarParametroValues("IND_DECLARACION_CONCUBINATO", ""+this.indDeclaracionConcubinato+"");	
        q.agregarParametroValues(AppConstantes.ID_USUARIO_ALTA, ""+usuarioAlta+"" );
		q.agregarParametroValues(AppConstantes.FEC_ALTA, ""+AppConstantes.CURRENT_TIMESTAMP+"");
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
		q.agregarParametroValues(NOM_PERSONA, "'" + this.nombre + "'");
		q.agregarParametroValues(NOM_PRIMER_APELLIDO, "'" + this.apellidoP + "'");
		q.agregarParametroValues(NOM_SEGUNDO_APELLIDO, "'" + this.apellidoM + "'");
		q.agregarParametroValues(FEC_NAC, "'" + this.fechaNac + "'");
		q.agregarParametroValues(CVE_CURP, "'"+ this.curp + "'");
		q.agregarParametroValues(CVE_RFC, setValor(this.rfc));
		q.agregarParametroValues(DES_CORREO, setValor(this.correoE));
		q.agregarParametroValues(DES_TELEFONO, setValor(this.tel));
		q.agregarParametroValues(""+AppConstantes.ID_USUARIO_MODIFICA+"", ""+usuarioAlta+"");
		q.agregarParametroValues(""+AppConstantes.FEC_ACTUALIZACION+"", ""+AppConstantes.CURRENT_TIMESTAMP+"");
		q.addWhere("ID_PERSONA = " + this.idPersona);
		String query = q.obtenerQueryActualizar();
		String encoded = encodedQuery(query);
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}



	public DatosRequest editarBeneficiario(Integer idPersona, Integer idUsuario, Integer parentesco, Integer indActa, Integer indIne) {
		 DatosRequest request = new DatosRequest();
	        Map<String, Object> parametro = new HashMap<>();
	        final QueryHelper q = new QueryHelper("UPDATE SVT_CONTRATANTE_BENEFICIARIOS");
	        q.agregarParametroValues(ID_PARENTESCO, ""+parentesco+"");
	       // q.agregarParametroValues("CVE_ACTA", "'"+acta+"'");
	        if(indActa!=null) {
	        	 q.agregarParametroValues(IND_ACTA_NACIMIENTO, ""+indActa+""); 	
	        }
	       if(indIne!=null) {
	    	   q.agregarParametroValues(IND_INE_BENEFICIARIO, ""+indIne+"");   
	       }
	       // q.agregarParametroValues(""+AppConstantes.IND_ACTIVO+"", "1");
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
		      .from(SVT_CONTRATANTE_BENEFICIARIOS)
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
				.from(SVT_CONTRATANTE_BENEFICIARIOS)
				 .join(SVT_CONTRATANTE_PAQUETE_CONVENIO_PF, SB_ID_CONTRATANTE_PAQUETE_CONVENIO_PF_SCPC_ID_CONTRATANTE_PAQUETE_CONVENIO_PF)
			      .join(SVT_CONVENIO_PF, SCPC_ID_CONVENIO_PF_PF_ID_CONVENIO_PF)
			      .leftJoin("SVT_BENEFICIARIOS_DOCUMENTACION_PLAN_ANTERIOR SBD", "SB.ID_CONTRATANTE_BENEFICIARIOS = SBD.ID_CONTRATANTE_BENEFICIARIOS")
			      .join(SVC_PERSONA, "SB.ID_PERSONA = SP.ID_PERSONA");
		           queryDos.where("(IF(SB.ID_PARENTESCO=4 AND TIMESTAMPDIFF(YEAR, SP.FEC_NAC, CURDATE())<18, SB.ID_PARENTESCO, NULL))")
		           .or("(SB.ID_PARENTESCO=4 AND SB.IND_SINIESTROS=0  AND PF.ID_CONVENIO_PF= "+palabra+" AND SBD.IND_COMPROBANTE_ESTUDIOS = 1 "
		           		+ "AND TIMESTAMPDIFF(YEAR, SP.FEC_NAC, CURDATE()) BETWEEN 18 AND 25)");
		           final String query = queryUno.union(queryDos);
		log.info("estoy en --> " +query);
	   String encoded = encodedQuery(query);
	   parametros.put(AppConstantes.QUERY, encoded);
	    request.setDatos(parametros);
	    return request;
	}
	
	public DatosRequest  buscarCatalogosParentescos(DatosRequest request) {
		Map<String, Object> parametros = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("DES_PARENTESCO AS parentesco",
				"ID_PARENTESCO AS id")
		.from("SVC_PARENTESCO");
		String query = obtieneQuery(queryUtil);
	   String encoded = encodedQuery(query);
	   parametros.put(AppConstantes.QUERY, encoded);
	    request.setDatos(parametros);
	    return request;
	}

	public DatosRequest  insertarPersona() {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVC_PERSONA ");
		q.agregarParametroValues(NOM_PERSONA, setValor(this.nombre));
		q.agregarParametroValues(NOM_PRIMER_APELLIDO, setValor(this.apellidoP));
		q.agregarParametroValues(NOM_SEGUNDO_APELLIDO, setValor(this.apellidoM));
		q.agregarParametroValues(FEC_NAC, setValor(this.fechaNac));
		q.agregarParametroValues(CVE_CURP, "'"+ this.curp + "'");
		q.agregarParametroValues(CVE_RFC, setValor(this.rfc));
		q.agregarParametroValues(DES_CORREO, setValor(this.correoE));
		q.agregarParametroValues(DES_TELEFONO, setValor(this.tel));
		q.agregarParametroValues(AppConstantes.ID_USUARIO_ALTA, ""+usuarioAlta+"");
		q.agregarParametroValues(AppConstantes.FEC_ALTA, ""+AppConstantes.CURRENT_TIMESTAMP+"");
		String query = q.obtenerQueryInsertar() +"$$"  + insertarBeneficiario();
		log.info(query);
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		        parametro.put(AppConstantes.QUERY, encoded);
		        parametro.put("separador","$$");
		        parametro.put("replace",ID_TABLA);
		        request.setDatos(parametro);
		return request;
	}

	private String insertarBeneficiario() {
		 DatosRequest request = new DatosRequest();
	        Map<String, Object> parametro = new HashMap<>();
	        final QueryHelper q = new QueryHelper("INSERT INTO SVT_CONTRATANTE_BENEFICIARIOS");
	        q.agregarParametroValues("ID_CONTRATANTE_PAQUETE_CONVENIO_PF", ""+this.idContratanteConvenioPf+"");
	        q.agregarParametroValues("ID_PERSONA", ID_TABLA);
	        q.agregarParametroValues(ID_PARENTESCO, ""+this.idParentesco+"");
	        if(this.indActa!=null) {
	        	q.agregarParametroValues(IND_ACTA_NACIMIENTO, ""+this.indActa+"");	
	        }
	        if(this.indIne!=null) {
	        	   q.agregarParametroValues(IND_INE_BENEFICIARIO, ""+this.indIne+"");   	
	        }
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

	public DatosRequest editarDocPlanAnterior() {
		 DatosRequest request = new DatosRequest();
			Map<String, Object> parametro = new HashMap<>();
	        final QueryHelper q = new QueryHelper("UPDATE SVT_BENEFICIARIOS_DOCUMENTACION_PLAN_ANTERIOR");
	        q.agregarParametroValues("IND_COMPROBANTE_ESTUDIOS", ""+this.indComprobanteEstudios+"");
	        q.agregarParametroValues("IND_ACTA_MATRIMONIO", ""+this.indActaMatrimonio+"");
	        q.agregarParametroValues("IND_DECLARACION_CONCUBINATO", ""+this.indDeclaracionConcubinato+"");	
	        q.agregarParametroValues(AppConstantes.ID_USUARIO_MODIFICA, ""+usuarioAlta+"" );
			q.agregarParametroValues(AppConstantes.FEC_ACTUALIZACION, ""+AppConstantes.CURRENT_TIMESTAMP+"");
		    q.addWhere("ID_CONTRATANTE_BENEFICIARIOS= " +this.idBeneficiario);
	        String query = q.obtenerQueryActualizar();
	        String encoded = encodedQuery(query);
	        parametro.put(AppConstantes.QUERY, encoded);
	        request.setDatos(parametro);
		return request;
	}
	
	public DatosRequest buscarCatalogosDocRequerida(DatosRequest request, Integer idConvenio) {
		Map<String, Object> parametros = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("SVD.ID_VALIDACION_DOCUMENTO AS idValidacionDoc",
				"CONCAT(SP.NOM_PERSONA, ' '",
				 "SP.NOM_PRIMER_APELLIDO, ' '",
				 "SP.NOM_SEGUNDO_APELLIDO) AS contratante",
				 "SV.DES_VELATORIO AS velatorio",
				 "IND_INE_AFILIADO AS ineAfiliado",
				 "IND_CURP AS curpAfiliado",
				 "IND_RFC AS rfcAfiliado",
				 "SVDR.IND_CONVENIO_ANTERIOR AS convenioAnterior",
				 "SVDR.IND_CARTA_PODER AS cartaPoder",
				 "SVDR.IND_INE_TESTIGO AS ineTestigo",
				 "SVDR.IND_INE_TESTIGO_DOS AS ineTestigoDos")
		.from("SVC_VALIDACION_DOCUMENTOS_CONVENIO_PF SVD")
		.leftJoin("SVC_VALIDACION_DOCUMENTOS_RENOVACION_CONVENIO_PF SVDR", "SVD.ID_VALIDACION_DOCUMENTO = SVDR.ID_VALIDACION_DOCUMENTO")

		.join(SVT_CONVENIO_PF, "SVD.ID_CONVENIO_PF=PF.ID_CONVENIO_PF")
        .join(SVT_CONTRATANTE_PAQUETE_CONVENIO_PF, "PF.ID_CONVENIO_PF =SCPC.ID_CONVENIO_PF")
        .join("SVC_CONTRATANTE SC", "SCPC.ID_CONTRATANTE = SC.ID_CONTRATANTE")
        .join(SVC_PERSONA, "SC.ID_PERSONA=SP.ID_PERSONA") 
        .join("SVC_VELATORIO SV", "PF.ID_VELATORIO = SV.ID_VELATORIO")
        .where("SVD.ID_CONVENIO_PF=" +idConvenio);
		String query = obtieneQuery(queryUtil);
	   String encoded = encodedQuery(query);
	   parametros.put(AppConstantes.QUERY, encoded);
	    request.setDatos(parametros);
	    return request;
	}
	
	public DatosRequest buscarCatalogosInfoConvenioActual(DatosRequest request, Integer idConvenio, String fecFormat) {
		Map<String, Object> parametros = new HashMap<>();
		SelectQueryUtil queryUtil = new SelectQueryUtil();
		queryUtil.select("PF.ID_TIPO_PREVISION AS idTipoPrevision",
				"IF(PF.ID_TIPO_PREVISION=1,'Prevision funeraria plan nuevo', 'Prevision funeraria plan anterior') AS tipoPrevision",
				 "SCPC.ID_PAQUETE AS idPaquete",
				 "PAQ.DES_PAQUETE AS tipoPaquete",
				 "PAQ.MON_COSTO_REFERENCIA AS cuotaRecuperacion",
				 "MAX(DATE_FORMAT(RPF.FEC_INICIO, '"+fecFormat+"')) AS fecInicio",
				 "MAX(DATE_FORMAT(RPF.FEC_VIGENCIA, '"+fecFormat+"')) AS fecVigencia",
				 "SMP.DESC_METODO_PAGO AS tipoPago",
				 "PF.ID_ESTATUS_CONVENIO AS estatusConvenio",
				 "DATE_FORMAT(PF.FEC_INICIO, '"+fecFormat+"') AS fechaContratacion",
				 "MAX(DATE_FORMAT(RPF.FEC_ALTA, '"+fecFormat+"')) AS fechaRenovacion")
		.from(SVT_CONVENIO_PF)
		.join(SVT_CONTRATANTE_PAQUETE_CONVENIO_PF, "PF.ID_CONVENIO_PF = SCPC.ID_CONVENIO_PF")
        .join("SVT_PAQUETE PAQ", "SCPC.ID_PAQUETE = PAQ.ID_PAQUETE")
        .join("SVT_RENOVACION_CONVENIO_PF RPF", "PF.ID_CONVENIO_PF = RPF.ID_CONVENIO_PF")
        .leftJoin("SVT_PAGO_BITACORA SPB", "PF.DES_FOLIO = SPB.CVE_FOLIO") 
        .leftJoin("SVT_PAGO_DETALLE SPD", "SPB.ID_PAGO_BITACORA = SPD.ID_PAGO_BITACORA")
        .leftJoin("SVC_METODO_PAGO SMP", "SPD.ID_METODO_PAGO = SMP.ID_METODO_PAGO")
        .where("PF.ID_CONVENIO_PF=" +idConvenio)
        .groupBy("PF.ID_CONVENIO_PF");
		String query = obtieneQuery(queryUtil);
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
	
	private String setValor(String valor) {
        if (valor==null || valor.equals("")) {
            return "NULL";
        }else {
            return "'"+valor+"'";
        }
    }


}
