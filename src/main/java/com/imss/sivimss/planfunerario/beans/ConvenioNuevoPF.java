package com.imss.sivimss.planfunerario.beans;

import com.imss.sivimss.planfunerario.model.request.*;
import com.imss.sivimss.planfunerario.model.response.BusquedaInformacionReporteResponse;
import com.imss.sivimss.planfunerario.util.AppConstantes;
import com.imss.sivimss.planfunerario.util.DatosRequest;
import com.imss.sivimss.planfunerario.util.QueryHelper;
import com.imss.sivimss.planfunerario.util.SelectQueryUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.xml.bind.DatatypeConverter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Builder
@Data
@AllArgsConstructor
public class ConvenioNuevoPF {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ConvenioNuevoPF.class);

    public String generarQueryPersona(PersonaAltaConvenio persona, String usuario) {
        final QueryHelper queryPersona = new QueryHelper("INSERT INTO SVC_PERSONA");
        queryPersona.agregarParametroValues("CVE_RFC", "'" + persona.getRfc() + "'");
        queryPersona.agregarParametroValues("CVE_CURP", "'" + persona.getCurp() + "'");
        queryPersona.agregarParametroValues("CVE_NSS", "'" + persona.getNss() + "'");
        queryPersona.agregarParametroValues("NOM_PERSONA", "'" + persona.getNombre() + "'");
        queryPersona.agregarParametroValues("NOM_PRIMER_APELLIDO", "'" + persona.getPrimerApellido() + "'");
        queryPersona.agregarParametroValues("NOM_SEGUNDO_APELLIDO", "'" + persona.getSegundoApellido() + "'");
        queryPersona.agregarParametroValues("NUM_SEXO", "'" + persona.getSexo() + "'");
        queryPersona.agregarParametroValues("DES_OTRO_SEXO", "'" + persona.getOtroSexo() + "'");
        queryPersona.agregarParametroValues("FEC_NAC", "'" + persona.getFechaNacimiento() + "'");
        queryPersona.agregarParametroValues("ID_PAIS", "'" + persona.getPais() + "'");
        queryPersona.agregarParametroValues("ID_ESTADO", "'" + persona.getEstado() + "'");
        queryPersona.agregarParametroValues("DES_TELEFONO", "'" + persona.getTelefono() + "'");
        queryPersona.agregarParametroValues("DES_CORREO", "'" + persona.getCorreoElectronico() + "'");
        queryPersona.agregarParametroValues("TIPO_PERSONA", "'" + persona.getTipoPersona() + "'");
        queryPersona.agregarParametroValues("NUM_INE", "'" + persona.getNumIne() + "'");
        queryPersona.agregarParametroValues("ID_USUARIO_ALTA", "'" + usuario + "'");
        log.info("Query insert Persona: " + queryPersona);
        return queryPersona.obtenerQueryInsertar();
    }

    public String generarQueryPersonaBeneficiaria(PersonaAltaConvenio personaBeneficiario, String usuario) {
        final QueryHelper queryPersona = new QueryHelper("INSERT INTO SVC_PERSONA");
        queryPersona.agregarParametroValues("CVE_RFC", "'" + personaBeneficiario.getRfc() + "'");
        queryPersona.agregarParametroValues("CVE_CURP", "'" + personaBeneficiario.getCurp() + "'");
        queryPersona.agregarParametroValues("CVE_NSS", "'" + personaBeneficiario.getNss() + "'");
        queryPersona.agregarParametroValues("NOM_PERSONA", "'" + personaBeneficiario.getNombre() + "'");
        queryPersona.agregarParametroValues("NOM_PRIMER_APELLIDO", "'" + personaBeneficiario.getPrimerApellido() + "'");
        queryPersona.agregarParametroValues("NOM_SEGUNDO_APELLIDO", "'" + personaBeneficiario.getSegundoApellido() + "'");
        queryPersona.agregarParametroValues("NUM_SEXO", "'" + personaBeneficiario.getSexo() + "'");
        queryPersona.agregarParametroValues("DES_OTRO_SEXO", "'" + personaBeneficiario.getOtroSexo() + "'");
        queryPersona.agregarParametroValues("FEC_NAC", "'" + personaBeneficiario.getFechaNacimiento() + "'");
        queryPersona.agregarParametroValues("ID_PAIS", "'" + personaBeneficiario.getPais() + "'");
        queryPersona.agregarParametroValues("ID_ESTADO", "'" + personaBeneficiario.getEstado() + "'");
        queryPersona.agregarParametroValues("DES_TELEFONO", "'" + personaBeneficiario.getTelefono() + "'");
        queryPersona.agregarParametroValues("DES_CORREO", "'" + personaBeneficiario.getCorreoElectronico() + "'");
        queryPersona.agregarParametroValues("TIPO_PERSONA", "'" + personaBeneficiario.getTipoPersona() + "'");
        queryPersona.agregarParametroValues("NUM_INE", "'" + personaBeneficiario.getNumIne() + "'");
        queryPersona.agregarParametroValues("ID_USUARIO_ALTA", "'" + usuario + "'");
        log.info("Query insert Persona beneficiaria: " + queryPersona);
        return queryPersona.obtenerQueryInsertar();
    }

    public String generarQueryDomicilio(PersonaAltaConvenio persona, String usuario) {
        final QueryHelper queryDomicilio = new QueryHelper("INSERT INTO SVT_DOMICILIO");
        queryDomicilio.agregarParametroValues("DES_CALLE", "'" + persona.getCalle() + "'");
        queryDomicilio.agregarParametroValues("NUM_EXTERIOR", "'" + persona.getNumeroExterior() + "'");
        queryDomicilio.agregarParametroValues("NUM_INTERIOR", "'" + persona.getNumeroInterior() + "'");
        queryDomicilio.agregarParametroValues("DES_CP", "'" + persona.getCp() + "'");
        queryDomicilio.agregarParametroValues("DES_COLONIA", "'" + persona.getColonia() + "'");
        queryDomicilio.agregarParametroValues("DES_MUNICIPIO", "'" + persona.getMunicipio() + "'");
        queryDomicilio.agregarParametroValues("DES_ESTADO", "'" + persona.getEstado() + "'");
        queryDomicilio.agregarParametroValues("ID_USUARIO_ALTA", usuario);
        log.info("Query insert Domicilio: " + queryDomicilio);
        return queryDomicilio.obtenerQueryInsertar();
    }

    public String generarQueryContratante(PersonaAltaConvenio persona, String usuario) {
        final QueryHelper queryContratante = new QueryHelper("INSERT INTO SVC_CONTRATANTE");
        queryContratante.agregarParametroValues("ID_PERSONA", "idPersona");
        queryContratante.agregarParametroValues("CVE_MATRICULA", "'" + persona.getMatricula() + "'");
        queryContratante.agregarParametroValues("ID_DOMICILIO", "idDomicilio");
        queryContratante.agregarParametroValues("ID_USUARIO_ALTA", usuario);
        log.info("Query insert contratante: " + queryContratante);
        return queryContratante.obtenerQueryInsertar();
    }

    public String generarQueryConvenioPf(String nombreVelatorio,String idPromotor, String idVelatorio, String usuario) {
        final QueryHelper querySvtConvenio = new QueryHelper("INSERT INTO SVT_CONVENIO_PF");
        querySvtConvenio.agregarParametroValues("DES_FOLIO", "(SELECT CONCAT(SUBSTRING('" + nombreVelatorio +"',1,3),'-',LPAD((SELECT COUNT(SP.ID_CONVENIO_PF +1) from SVT_CONVENIO_PF SP),6,'0')))");
        querySvtConvenio.agregarParametroValues("FEC_INICIO", "(select DATE_ADD( CURDATE(), INTERVAL 1 DAY))");
        querySvtConvenio.agregarParametroValues("FEC_VIGENCIA", "(select DATE_ADD(DATE_ADD( CURDATE(), interval 1 year),interval 1 DAY))");
        querySvtConvenio.agregarParametroValues("TIM_HORA", "DATE_FORMAT(NOW(), '%H:%i' )");
        querySvtConvenio.agregarParametroValues("ID_VELATORIO", "'" + idVelatorio + "'");
        querySvtConvenio.agregarParametroValues("IND_SINIESTROS", "'0'");
        querySvtConvenio.agregarParametroValues("IND_TIPO_CONTRATACION", "1"); // 1.- personas - 0.- empresa
        querySvtConvenio.agregarParametroValues("ID_PROMOTOR", idPromotor);
        querySvtConvenio.agregarParametroValues("ID_ESTATUS_CONVENIO", "1");
        querySvtConvenio.agregarParametroValues("ID_USUARIO_ALTA", usuario);
        log.info("Query insert convenio: " + querySvtConvenio);
        return querySvtConvenio.obtenerQueryInsertar();
    }

    public String generarQueryContratantePaquete(PersonaConvenioRequest persona , String usuario) {
        final QueryHelper queryContratantePaquete = new QueryHelper("INSERT INTO SVT_CONTRATANTE_PAQUETE_CONVENIO_PF");
        String idContratante = "idContratante";
        if(!Objects.isNull(persona.getIdContratante())){
            idContratante = persona.getIdContratante();
        }
        queryContratantePaquete.agregarParametroValues("ID_CONTRATANTE", idContratante);
        queryContratantePaquete.agregarParametroValues("ID_CONVENIO_PF", "idConvenioPf");
        queryContratantePaquete.agregarParametroValues("ID_ENFERMEDAD_PREXISTENTE", "'" + persona.getPersona().getEnfermedadPreexistente() + "'");
        queryContratantePaquete.agregarParametroValues("DES_OTRA_ENFERMEDAD", "'" + persona.getPersona().getOtraEnfermedad() + "'");
        queryContratantePaquete.agregarParametroValues("ID_PAQUETE", persona.getPersona().getPaquete());
        queryContratantePaquete.agregarParametroValues("ID_USUARIO_ALTA", usuario);
        log.info("Query insert contratante paquete: " + queryContratantePaquete);
        return queryContratantePaquete.obtenerQueryInsertar();
    }

    public String generarQueryContratanteBeneficiarios(String parentesco, String claveActa, String usuario) {
        final QueryHelper queryContratanteBeneficiarios = new QueryHelper("INSERT INTO SVT_CONTRATANTE_BENEFICIARIOS");
        queryContratanteBeneficiarios.agregarParametroValues("ID_CONTRATANTE_PAQUETE_CONVENIO_PF", "idContratantePaqueteConvenio");
        queryContratanteBeneficiarios.agregarParametroValues("ID_PARENTESCO",parentesco);
        queryContratanteBeneficiarios.agregarParametroValues("ID_PERSONA", "idPersona");
        queryContratanteBeneficiarios.agregarParametroValues("ID_CONVENIO_PF", "idConvenioPf");
        queryContratanteBeneficiarios.agregarParametroValues("CVE_ACTA", "'" + claveActa + "'");
        queryContratanteBeneficiarios.agregarParametroValues("ID_USUARIO_ALTA", usuario);
        queryContratanteBeneficiarios.agregarParametroValues("IND_ACTIVO", "1");
        log.info("Query insert contratante beneficiarios: " + queryContratanteBeneficiarios);
        return queryContratanteBeneficiarios.obtenerQueryInsertar();
    }
    public String generarQueryValidacionDocumentos(PersonaConvenioRequest persona,String usuario) {
        final QueryHelper queryValidaDocumentos = new QueryHelper("INSERT INTO SVC_VALIDACION_DOCUMENTOS_CONVENIO_PF");
        queryValidaDocumentos.agregarParametroValues("IND_INE_AFILIADO", persona.getPersona().getDocumentacion().getValidaIneContratante().toString());
        queryValidaDocumentos.agregarParametroValues("IND_CURP", persona.getPersona().getDocumentacion().getValidaCurp().toString());
        queryValidaDocumentos.agregarParametroValues("IND_RFC", persona.getPersona().getDocumentacion().getValidaRfc().toString());
        queryValidaDocumentos.agregarParametroValues("IND_ACTA_NACIMIENTO", persona.getPersona().getDocumentacion().getValidaActaNacimientoBeneficiario().toString());
        queryValidaDocumentos.agregarParametroValues("IND_INE_BENEFICIARIO", persona.getPersona().getDocumentacion().getValidaIneBeneficiario().toString());
        queryValidaDocumentos.agregarParametroValues("ID_CONVENIO_PF", "idConvenioPf");
        queryValidaDocumentos.agregarParametroValues("ID_USUARIO_ALTA", usuario);
        log.info("Query insert validacion documentos: " + queryValidaDocumentos);
        return queryValidaDocumentos.obtenerQueryInsertar();
    }
    public DatosRequest consultarPromotores() {
        DatosRequest dr = new DatosRequest();
        Map<String, Object> parametro = new HashMap<>();
        SelectQueryUtil query = new SelectQueryUtil();
        query.select("SP.ID_PROMOTOR AS idPromotor","SP.NUM_EMPLEDO AS numEmpleado",
                "CONCAT (SP.NOM_PROMOTOR , ' ' , SP.NOM_PAPELLIDO , ' ' , SP.NOM_SAPELLIDO) AS nombrePromotor")
                .from("SVT_PROMOTOR SP");
        String consulta = query.build();
        String encoded = DatatypeConverter.printBase64Binary(consulta.getBytes());
        parametro.put(AppConstantes.QUERY, encoded);
        dr.setDatos(parametro);
        return dr;
    }

    public DatosRequest consultarCurpRfc(String curp, String rfc) {
        DatosRequest dr = new DatosRequest();
        Map<String, Object> parametro = new HashMap<>();
        SelectQueryUtil query = new SelectQueryUtil();
        query.select("SP.CVE_RFC AS rfc","SP.CVE_CURP AS curp","SP.CVE_NSS AS nss","SP.NOM_PERSONA AS nomPersona",
                "SP.NOM_PRIMER_APELLIDO AS primerApellido","SP.NOM_SEGUNDO_APELLIDO AS segundoApellido",
                "SP.NUM_SEXO AS sexo","SP.FEC_NAC AS fechaNacimiento","SP.ID_PAIS AS idPais","SP.ID_ESTADO AS idEstado",
                "SP.DES_TELEFONO AS telefono","SP.DES_CORREO AS correo","SP.TIPO_PERSONA AS tipoPersona")
                .from("SVC_CONTRATANTE SC")
                .leftJoin("SVC_PERSONA SP","SC.ID_PERSONA = SP.ID_PERSONA")
                .where("SP.CVE_RFC = " + rfc)
                .or("SP.CVE_CURP = " + curp);
        String consulta = query.build();
        String encoded = DatatypeConverter.printBase64Binary(consulta.getBytes());
        parametro.put(AppConstantes.QUERY, encoded);
        dr.setDatos(parametro);
        return dr;
    }

    public DatosRequest consultarCP(String cp) {
        DatosRequest dr = new DatosRequest();
        Map<String, Object> parametro = new HashMap<>();
        SelectQueryUtil query = new SelectQueryUtil();
        query.select("SC.CVE_CODIGO_POSTAL AS codigoPostal","SC.DES_COLONIA AS colonia",
                "SC.DES_MNPIO AS municipio","SC.DES_ESTADO AS estado")
                .from("SVC_CP SC")
                .where("SC.CVE_CODIGO_POSTAL = " + cp);
        String consulta = query.build();
        String encoded = DatatypeConverter.printBase64Binary(consulta.getBytes());
        parametro.put(AppConstantes.QUERY, encoded);
        dr.setDatos(parametro);
        return dr;
    }

    public DatosRequest busquedaFolioPersona(String folioConvenio) {
        DatosRequest dr = new DatosRequest();
        Map<String, Object> parametro = new HashMap<>();
        SelectQueryUtil querySelect = new SelectQueryUtil();
        querySelect.select("SCP.ID_CONVENIO_PF AS idConvenioPf", "SCP.DES_FOLIO AS folioConvenioPf", "SCP.ID_VELATORIO AS idVelatorio", "sv.NOM_VELATORIO as nombreVelatorio",
                        "SCP.ID_PROMOTOR AS idPromotor", "PROM.NUM_EMPLEDO AS numeroEmpleado", "PROM.NOM_PROMOTOR AS nombrePromotor", "PROM.NOM_PAPELLIDO AS primerApellido",
                        "PROM.NOM_SAPELLIDO AS segundoApellido", "CPF.ID_CONTRATANTE_PAQUETE_CONVENIO_PF AS idContratanteConvenioPf", "CPF.ID_CONTRATANTE AS idContratante",
                        "SC.CVE_MATRICULA AS cveMatricula", "SC.ID_PERSONA AS idPersona", "SP.CVE_RFC AS rfc", "SP.CVE_CURP AS curp", "SP.CVE_NSS AS nss", "SP.NOM_PERSONA AS nombrePersona",
                        "SP.NOM_PRIMER_APELLIDO AS primerApellido", "SP.NOM_SEGUNDO_APELLIDO AS segundoApellido", "SP.NUM_SEXO AS numSexo", "SP.ID_PAIS AS idPais", "SP.ID_ESTADO AS idEstado",
                        "SP.DES_TELEFONO AS telefono", "SP.DES_CORREO AS correo", "SP.TIPO_PERSONA AS tipoPersona", "SP.NUM_INE AS numIne",
                        "CPF.ID_PAQUETE AS idPaquete", "PAQ.NOM_PAQUETE AS nombrePaquete", "DATE_FORMAT(SP.FEC_NAC,'%Y-%m-%d') AS fechaNacimiento")
                .from("SVT_CONVENIO_PF SCP")
                .leftJoin("SVC_VELATORIO SV", "SCP.ID_VELATORIO = SV.ID_VELATORIO")
                .leftJoin("SVT_PROMOTOR PROM", "SCP.ID_PROMOTOR = PROM.ID_PROMOTOR")
                .leftJoin("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF CPF", "SCP.ID_CONVENIO_PF = CPF.ID_CONVENIO_PF")
                .leftJoin("SVT_PAQUETE PAQ", "CPF.ID_PAQUETE = PAQ.ID_PAQUETE")
                .leftJoin("SVC_CONTRATANTE SC", "CPF.ID_CONTRATANTE = SC.ID_CONTRATANTE")
                .leftJoin("SVC_PERSONA SP", "SC.ID_PERSONA = SP.ID_PERSONA")
                .leftJoin("SVT_CONTRATANTE_BENEFICIARIOS SCB", "CPF.ID_CONTRATANTE_PAQUETE_CONVENIO_PF = SCB.ID_CONTRATANTE_PAQUETE_CONVENIO_PF")
                .where("SCP.DES_FOLIO = " + folioConvenio)
                .groupBy("SCP.DES_FOLIO");
        String consulta = querySelect.build();
        String encoded = DatatypeConverter.printBase64Binary(consulta.getBytes());
        parametro.put(AppConstantes.QUERY, encoded);
        dr.setDatos(parametro);
        return dr;
    }

    public DatosRequest busquedaFolioEmpresa(String folioConvenio) {
        DatosRequest dr = new DatosRequest();
        Map<String, Object> parametro = new HashMap<>();
        SelectQueryUtil querySelect = new SelectQueryUtil();
        querySelect.select("SCP.ID_CONVENIO_PF AS idConvenio", "SCP.DES_FOLIO AS desFolio", "EMP.ID_EMPRESA_CONVENIO_PF AS idEmpresa",
                        "EMP.DES_NOMBRE AS nombreEmpresa", "EMP.DES_RAZON_SOCIAL AS razonSocial", "EMP.DES_RFC AS rfc", "EMP.ID_PAIS AS idPais",
                        "EMP.ID_DOMICILIO AS idDomicilio", "SD.DES_CALLE AS calle", "SD.NUM_EXTERIOR AS numExterior", "SD.NUM_INTERIOR AS numInterior",
                        "SD.DES_CP AS cp", "SD.DES_COLONIA AS desColonia", "SD.DES_MUNICIPIO AS desMunicipio", "SD.DES_ESTADO AS desEstado",
                        "EMP.DES_TELEFONO AS telefono", "EMP.DES_CORREO AS correo")
                .from("SVT_CONVENIO_PF SCP")
                .leftJoin("SVT_EMPRESA_CONVENIO_PF EMP", "SCP.ID_CONVENIO_PF = EMP .ID_CONVENIO_PF")
                .leftJoin("SVT_DOMICILIO SD", "EMP.ID_DOMICILIO = SD.ID_DOMICILIO")
                .where("SCP.DES_FOLIO = " + folioConvenio);
        String consulta = querySelect.build();
        String encoded = DatatypeConverter.printBase64Binary(consulta.getBytes());
        parametro.put(AppConstantes.QUERY, encoded);
        dr.setDatos(parametro);
        return dr;
    }

    public DatosRequest busquedaBeneficiarios(String folioConvenio) {
        DatosRequest dr = new DatosRequest();
        Map<String, Object> parametro = new HashMap<>();
        SelectQueryUtil querySelect = new SelectQueryUtil();
        querySelect.select("SP2.ID_PERSONA AS idPersona", "SP2.NOM_PERSONA AS nombreBeneficiario", "SP2.NOM_PRIMER_APELLIDO AS primerApellido",
                        "SP2.NOM_SEGUNDO_APELLIDO AS segundoApellido", "DATE_FORMAT(SP2.FEC_NAC,'%Y-%m-%d') AS fechaNacimiento",
                        "SP2.CVE_RFC AS rfc", "SP2.CVE_CURP AS curp", "SP2.CVE_NSS  AS nss", "SP2.NUM_SEXO AS numSexo",
                        "SP2.DES_TELEFONO AS telefono", "SP2.DES_CORREO AS correo", "SP2.TIPO_PERSONA AS tipoPersona", "SP2.NUM_INE AS numIne")
                .from("SVT_CONVENIO_PF SCP")
                .leftJoin("SVC_VELATORIO SV", "SCP.ID_VELATORIO = SV.ID_VELATORIO")
                .leftJoin("SVT_PROMOTOR PROM", "SCP.ID_PROMOTOR = PROM.ID_PROMOTOR")
                .leftJoin("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF CPF", "SCP.ID_CONVENIO_PF = CPF.ID_CONVENIO_PF")
                .leftJoin("SVT_PAQUETE PAQ", "CPF.ID_PAQUETE = PAQ.ID_PAQUETE")
                .leftJoin("SVC_CONTRATANTE SC", "CPF.ID_CONTRATANTE = SC.ID_CONTRATANTE")
                .leftJoin("SVC_PERSONA SP", "SC.ID_PERSONA = SP.ID_PERSONA")
                .leftJoin("SVT_CONTRATANTE_BENEFICIARIOS SCB", "CPF.ID_CONTRATANTE_PAQUETE_CONVENIO_PF = SCB.ID_CONTRATANTE_PAQUETE_CONVENIO_PF")
                .leftJoin("SVC_PERSONA SP2", "SCB.ID_PERSONA = SP2.ID_PERSONA")
                .where("SCP.DES_FOLIO = " + folioConvenio);
        String consulta = querySelect.build();
        String encoded = DatatypeConverter.printBase64Binary(consulta.getBytes());
        parametro.put(AppConstantes.QUERY, encoded);
        dr.setDatos(parametro);
        return dr;
    }

    public DatosRequest busquedaRfcEmpresa(String rfc) {
        DatosRequest dr = new DatosRequest();
        Map<String, Object> parametro = new HashMap<>();
        SelectQueryUtil querySelect = new SelectQueryUtil();
        querySelect.select("EC.DES_NOMBRE AS nombreEmpresa", "EC.DES_RAZON_SOCIAL AS razonSocial", "EC.DES_RFC AS rfc",
                        "EC.ID_PAIS AS idPais", "SP.DES_PAIS AS desPais", "EC.ID_DOMICILIO AS idDomicilio", "SD.DES_CALLE AS calle", "SD.NUM_EXTERIOR AS numExterior",
                        "SD.NUM_INTERIOR AS numInterior", "SD.DES_CP AS cp", "SD.DES_COLONIA AS desColonia", "SD.DES_MUNICIPIO AS desMunicipio",
                        "SD.DES_ESTADO AS desEstado", "EC.DES_TELEFONO AS telefono", "EC.DES_CORREO AS correo")
                .from("SVT_EMPRESA_CONVENIO_PF EC")
                .leftJoin("SVC_PAIS SP", "EC.ID_PAIS = SP.ID_PAIS")
                .leftJoin("SVT_DOMICILIO SD", "EC.ID_DOMICILIO = SD.ID_DOMICILIO")
                .where("EC.DES_RFC = " + rfc);
        String consulta = querySelect.build();
        String encoded = DatatypeConverter.printBase64Binary(consulta.getBytes());
        parametro.put(AppConstantes.QUERY, encoded);
        dr.setDatos(parametro);
        return dr;
    }

    public DatosRequest cambiarEstatusConvenio(String idEstatusConvenio, String folioConvenio, UsuarioDto user) {
        DatosRequest dr = new DatosRequest();
        Map<String, Object> parametro = new HashMap<>();
        final QueryHelper query = new QueryHelper("UPDATE SVT_CONVENIO_PF");
        query.agregarParametroValues("ID_ESTATUS_CONVENIO", idEstatusConvenio);
        query.agregarParametroValues("ID_USUARIO_MODIFICA", user.getCveUsuario());
        query.agregarParametroValues("FEC_ACTUALIZACION", "NOW()");
        query.addWhere("DES_FOLIO = " + folioConvenio);
        String encoded = DatatypeConverter.printBase64Binary(query.obtenerQueryActualizar().getBytes());
        parametro.put(AppConstantes.QUERY, encoded);
        dr.setDatos(parametro);
        return dr;
    }

    public Map<String, Object> generarReporte(PdfDto pdfDto, BusquedaInformacionReporteResponse infoReporte) {
        Map<String, Object> datosPdf = new HashMap<>();
        datosPdf.put("rutaNombreReporte", pdfDto.getRutaNombreReporte());
        datosPdf.put("tipoReporte", "pdf");
        datosPdf.put("nombreAfiliado", infoReporte.getNombrePersona() + " " + infoReporte.getPrimerApellido() + " " + infoReporte.getSegundoApellido()); // sacar datos de query
        datosPdf.put("numeroINE", infoReporte.getNumIne());// sacar datos de query
        datosPdf.put("paqueteContratado", infoReporte.getNombrePaquete());// sacar datos de query
        datosPdf.put("serviciosIncluidos", infoReporte.getDesPaquete());// sacar datos de query
        datosPdf.put("costoPaquete", infoReporte.getMonPrecio());// sacar datos de query
        datosPdf.put("nombreTitular", infoReporte.getNombrePersona() + " " + infoReporte.getPrimerApellido() + " " + infoReporte.getSegundoApellido());// sacar datos de query
        datosPdf.put("rfc", infoReporte.getRfc());// sacar datos de query
        datosPdf.put("folioConvenio", pdfDto.getFolioConvenio());// sacar datos de query
        datosPdf.put("ciudadExpedicion", pdfDto.getCiudadExpedicion());// sacar datos de query
        datosPdf.put("fechaExpedicion", pdfDto.getFechaExpedicion());// sacar datos de query
        return datosPdf;
    }

    public DatosRequest busquedaFolioParaReporte(String folioConvenio) {
        DatosRequest dr = new DatosRequest();
        Map<String, Object> parametro = new HashMap<>();
        SelectQueryUtil querySelect = new SelectQueryUtil();
        querySelect.select("SP.CVE_RFC AS rfc", "SP.CVE_CURP AS curp", "SP.CVE_NSS AS nss", "SP.NOM_PERSONA AS nombrePersona",
                        "SP.NOM_PRIMER_APELLIDO AS primerApellido", "SP.NOM_SEGUNDO_APELLIDO AS segundoApellido", "SP.NUM_INE AS numIne",
                        "CPF.ID_PAQUETE AS idPaquete", "PAQ.DES_NOM_PAQUETE AS nombrePaquete", "PAQ.DES_PAQUETE AS desPaquete", "PAQ.MON_PRECIO AS monPrecio")
                .from("SVT_CONVENIO_PF SCP")
                .leftJoin("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF CPF", "SCP.ID_CONVENIO_PF = CPF.ID_CONVENIO_PF")
                .leftJoin("SVT_PAQUETE PAQ", "CPF.ID_PAQUETE = PAQ.ID_PAQUETE")
                .leftJoin("SVC_CONTRATANTE SC", "CPF.ID_CONTRATANTE = SC.ID_CONTRATANTE")
                .leftJoin("SVC_PERSONA SP", "SC.ID_PERSONA = SP.ID_PERSONA")
                .where("SCP.DES_FOLIO = '" + folioConvenio + "'")
                .groupBy("SCP.DES_FOLIO");
        String consulta = querySelect.build();
        String encoded = DatatypeConverter.printBase64Binary(consulta.getBytes());
        parametro.put(AppConstantes.QUERY, encoded);
        dr.setDatos(parametro);
        return dr;
    }
}
