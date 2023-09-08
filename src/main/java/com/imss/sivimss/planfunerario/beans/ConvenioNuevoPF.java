package com.imss.sivimss.planfunerario.beans;

import com.imss.sivimss.planfunerario.model.request.*;
import com.imss.sivimss.planfunerario.model.response.BusquedaInformacionReporteResponse;
import com.imss.sivimss.planfunerario.service.impl.ContratarPlanPFServiceImpl;
import com.imss.sivimss.planfunerario.util.AppConstantes;
import com.imss.sivimss.planfunerario.util.DatosRequest;
import com.imss.sivimss.planfunerario.util.QueryHelper;
import com.imss.sivimss.planfunerario.util.SelectQueryUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Service
public class ConvenioNuevoPF {
    @Autowired
    private ContratarPlanPFServiceImpl imp;
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
            queryPersona.agregarParametroValues("ID_ESTADO", "'" + persona.getEntidadFederativa() + "'");
            queryPersona.agregarParametroValues("DES_TELEFONO", "'" + persona.getTelefono() + "'");
            queryPersona.agregarParametroValues("DES_CORREO", "'" + persona.getCorreoElectronico() + "'");
            queryPersona.agregarParametroValues("TIP_PERSONA", "'" + persona.getTipoPersona() + "'");
            queryPersona.agregarParametroValues("NUM_INE", "'" + persona.getNumIne() + "'");
            queryPersona.agregarParametroValues("ID_USUARIO_ALTA", "'" + usuario + "'");
            log.info("Query insert Persona: " + queryPersona.obtenerQueryInsertar());
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
        //queryPersona.agregarParametroValues("NUM_SEXO", "'" + personaBeneficiario.getSexo() + "'");
        //queryPersona.agregarParametroValues("DES_OTRO_SEXO", "'" + personaBeneficiario.getOtroSexo() + "'");
        queryPersona.agregarParametroValues("FEC_NAC", "'" + personaBeneficiario.getFechaNacimiento() + "'");
       // queryPersona.agregarParametroValues("ID_PAIS", "'" + personaBeneficiario.getPais() + "'");
       // queryPersona.agregarParametroValues("ID_ESTADO", "'" + personaBeneficiario.getEntidadFederativa() + "'");
        queryPersona.agregarParametroValues("DES_TELEFONO", "'" + personaBeneficiario.getTelefono() + "'");
        queryPersona.agregarParametroValues("DES_CORREO", "'" + personaBeneficiario.getCorreoElectronico() + "'");
        //queryPersona.agregarParametroValues("TIP_PERSONA", "'" + personaBeneficiario.getTipoPersona() + "'");
        //queryPersona.agregarParametroValues("NUM_INE", "'" + personaBeneficiario.getNumIne() + "'");
        queryPersona.agregarParametroValues("ID_USUARIO_ALTA", "'" + usuario + "'");
        log.info("Query insert Persona beneficiaria: " + queryPersona.obtenerQueryInsertar());
        return queryPersona.obtenerQueryInsertar();
    }

    public String generarQueryDomicilio(String calle, String numExt, String numInt, String cp, String colonia, String municipio, String estado, String usuario) {
        final QueryHelper queryDomicilio = new QueryHelper("INSERT INTO SVT_DOMICILIO");
        queryDomicilio.agregarParametroValues("DES_CALLE", "'" + calle + "'");
        queryDomicilio.agregarParametroValues("NUM_EXTERIOR", "'" + numExt + "'");
        queryDomicilio.agregarParametroValues("NUM_INTERIOR", "'" + numInt + "'");
        queryDomicilio.agregarParametroValues("DES_CP", "'" + cp + "'");
        queryDomicilio.agregarParametroValues("DES_COLONIA", "'" + colonia + "'");
        queryDomicilio.agregarParametroValues("DES_MUNICIPIO", "'" + municipio + "'");
        queryDomicilio.agregarParametroValues("DES_ESTADO", "'" + estado + "'");
        queryDomicilio.agregarParametroValues("ID_USUARIO_ALTA", usuario);
        log.info("Query insert Domicilio: " + queryDomicilio.obtenerQueryInsertar());
        return queryDomicilio.obtenerQueryInsertar();
    }

    public String generarQueryContratante(PersonaAltaConvenio persona, String usuario) throws IOException {
            final QueryHelper queryContratante = new QueryHelper("INSERT INTO SVC_CONTRATANTE");
            queryContratante.agregarParametroValues("ID_PERSONA", "idPersona");
            queryContratante.agregarParametroValues("CVE_MATRICULA", "'" + persona.getMatricula() + "'");
            queryContratante.agregarParametroValues("ID_DOMICILIO", "idDomicilio");
            queryContratante.agregarParametroValues("ID_USUARIO_ALTA", usuario);
            log.info("Query insert contratante: " + queryContratante.obtenerQueryInsertar());
            return queryContratante.obtenerQueryInsertar();
    }

    public String generarQueryConvenioPf(String nombreVelatorio, String idPromotor, String idVelatorio, String usuario, String tipoContratacion) {
        final QueryHelper querySvtConvenio = new QueryHelper("INSERT INTO SVT_CONVENIO_PF");
        querySvtConvenio.agregarParametroValues("DES_FOLIO", "(SELECT CONCAT(SUBSTRING('" + nombreVelatorio + "',1,3),'-',LPAD((SELECT COUNT(SP.ID_CONVENIO_PF +1) from SVT_CONVENIO_PF SP),6,'0')))");
        querySvtConvenio.agregarParametroValues("FEC_INICIO", "(select DATE_ADD( CURDATE(), INTERVAL 1 DAY))");
        querySvtConvenio.agregarParametroValues("FEC_VIGENCIA", "(select DATE_ADD(DATE_ADD( CURDATE(), interval 1 year),interval 1 DAY))");
        querySvtConvenio.agregarParametroValues("TIM_HORA", "DATE_FORMAT(NOW(), '%H:%i' )");
        querySvtConvenio.agregarParametroValues("ID_VELATORIO", "'" + idVelatorio + "'");
        querySvtConvenio.agregarParametroValues("IND_SINIESTROS", "'0'");
        querySvtConvenio.agregarParametroValues("IND_TIPO_CONTRATACION", tipoContratacion); // 1.- personas - 0.- empresa
        querySvtConvenio.agregarParametroValues("ID_PROMOTOR", idPromotor);
        querySvtConvenio.agregarParametroValues("ID_ESTATUS_CONVENIO", "1");
        querySvtConvenio.agregarParametroValues("ID_USUARIO_ALTA", usuario);
        log.info("Query insert convenio: " + querySvtConvenio.obtenerQueryInsertar());
        return querySvtConvenio.obtenerQueryInsertar();
    }

    public String generarQueryContratantePaquete(PersonaConvenioRequest persona, String usuario) {
        final QueryHelper queryContratantePaquete = new QueryHelper("INSERT INTO SVT_CONTRATANTE_PAQUETE_CONVENIO_PF");
        String idContratante = "idContratante";
        if (!Objects.isNull(persona.getIdContratante())) {
            idContratante = persona.getIdContratante();
        }
        queryContratantePaquete.agregarParametroValues("ID_CONTRATANTE", idContratante);
        queryContratantePaquete.agregarParametroValues("ID_CONVENIO_PF", "idConvenioPf");
        queryContratantePaquete.agregarParametroValues("ID_ENFERMEDAD_PREXISTENTE", "'" + persona.getPersona().getEnfermedadPreexistente() + "'");
        queryContratantePaquete.agregarParametroValues("DES_OTRA_ENFERMEDAD", "'" + persona.getPersona().getOtraEnfermedad() + "'");
        queryContratantePaquete.agregarParametroValues("ID_PAQUETE", persona.getPersona().getPaquete());
        queryContratantePaquete.agregarParametroValues("ID_USUARIO_ALTA", usuario);
        log.info("Query insert contratante paquete: " + queryContratantePaquete.obtenerQueryInsertar());
        return queryContratantePaquete.obtenerQueryInsertar();
    }



    public String generarQueryContratanteBeneficiarios(String parentesco, String claveActa, String usuario, PersonaAltaConvenio persona,Authentication authentication) throws IOException {
            final QueryHelper queryContratanteBeneficiarios = new QueryHelper("INSERT INTO SVT_CONTRATANTE_BENEFICIARIOS");
            queryContratanteBeneficiarios.agregarParametroValues("ID_CONTRA_PAQ_CONVENIO_PF", "idContratantePaqueteConvenio");
            queryContratanteBeneficiarios.agregarParametroValues("ID_PARENTESCO", parentesco);
            queryContratanteBeneficiarios.agregarParametroValues("ID_PERSONA", "idPersona");
            queryContratanteBeneficiarios.agregarParametroValues("CVE_ACTA", "'" + claveActa + "'");
            queryContratanteBeneficiarios.agregarParametroValues("ID_USUARIO_ALTA", usuario);
            queryContratanteBeneficiarios.agregarParametroValues("IND_ACTIVO", "1");
            queryContratanteBeneficiarios.agregarParametroValues("IND_INE_BENEFICIARIO",  persona.getDocumentacion().getValidaIneBeneficiario() == true ? "1" : "0");
            queryContratanteBeneficiarios.agregarParametroValues("IND_ACTA_NACIMIENTO", persona.getDocumentacion().getValidaActaNacimientoBeneficiario() == true ? "1" : "0");
            log.info("Query insert contratante beneficiarios: " + queryContratanteBeneficiarios.obtenerQueryInsertar());
            return queryContratanteBeneficiarios.obtenerQueryInsertar();
    }

    public String generarQueryValidacionDocumentos(PersonaConvenioRequest persona, String usuario) {
        final QueryHelper queryValidaDocumentos = new QueryHelper("INSERT INTO SVC_VALIDACION_DOCUMENTOS_CONVENIO_PF");
        queryValidaDocumentos.agregarParametroValues("IND_INE_AFILIADO", persona.getPersona().getDocumentacion().getValidaIneContratante().toString());
        queryValidaDocumentos.agregarParametroValues("IND_CURP", persona.getPersona().getDocumentacion().getValidaCurp().toString());
        queryValidaDocumentos.agregarParametroValues("IND_RFC", persona.getPersona().getDocumentacion().getValidaRfc().toString());
        queryValidaDocumentos.agregarParametroValues("IND_ACTA_NACIMIENTO", persona.getPersona().getDocumentacion().getValidaActaNacimientoBeneficiario().toString());
        queryValidaDocumentos.agregarParametroValues("IND_INE_BENEFICIARIO", persona.getPersona().getDocumentacion().getValidaIneBeneficiario().toString());
        queryValidaDocumentos.agregarParametroValues("ID_CONVENIO_PF", "idConvenioPf");
        queryValidaDocumentos.agregarParametroValues("ID_USUARIO_ALTA", usuario);
        log.info("Query insert validacion documentos: " + queryValidaDocumentos.obtenerQueryInsertar());
        return queryValidaDocumentos.obtenerQueryInsertar();
    }

    public String generarQueryEmpresaConvenioPf(PorEmpresaRequest empresa, String usuario) {
        final QueryHelper queryEmpresaConvenio = new QueryHelper("INSERT SVT_EMPRESA_CONVENIO_PF");
        queryEmpresaConvenio.agregarParametroValues("DES_NOMBRE", "'" + empresa.getNombreEmpresa() + "'");
        queryEmpresaConvenio.agregarParametroValues("DES_RAZON_SOCIAL", "'" + empresa.getRazonSocial() + "'");
        queryEmpresaConvenio.agregarParametroValues("DES_RFC", "'" + empresa.getRfc() + "'");
        queryEmpresaConvenio.agregarParametroValues("ID_PAIS", "'" + empresa.getPais() + "'");
        queryEmpresaConvenio.agregarParametroValues("ID_DOMICILIO", "idDomicilio");
        queryEmpresaConvenio.agregarParametroValues("DES_TELEFONO", "'" + empresa.getTelefono() + "'");
        queryEmpresaConvenio.agregarParametroValues("DES_CORREO", "'" + empresa.getCorreoElectronico() + "'");
        queryEmpresaConvenio.agregarParametroValues("ID_CONVENIO_PF", "idConvenioPf");
        queryEmpresaConvenio.agregarParametroValues("ID_USUARIO_ALTA", usuario);
        String consulta = queryEmpresaConvenio.obtenerQueryInsertar();
        log.info("Query insert empresa convenio pf: " + consulta);
        return consulta;
    }


    public DatosRequest consultarPromotores() {
        DatosRequest dr = new DatosRequest();
        Map<String, Object> parametro = new HashMap<>();
        SelectQueryUtil query = new SelectQueryUtil();
        query.select("SP.ID_PROMOTOR AS idPromotor", "SP.NUM_EMPLEDO AS numEmpleado",
                        "CONCAT (SP.NOM_PROMOTOR , ' ' , SP.NOM_PAPELLIDO , ' ' , SP.NOM_SAPELLIDO) AS nombrePromotor")
                .from("SVT_PROMOTOR SP");
        String consulta = query.build();
        String encoded = DatatypeConverter.printBase64Binary(consulta.getBytes());
        parametro.put(AppConstantes.QUERY, encoded);
        dr.setDatos(parametro);
        return dr;
    }


    public DatosRequest obtenerPaquetes(Integer idVelatorio) {
        DatosRequest datosRequest = new DatosRequest();
        Map<String, Object>parametros= new HashMap<>();
        SelectQueryUtil selectQueryUtilPaquete= new SelectQueryUtil();
        SelectQueryUtil selectQueryUtilUnionPaqueteRegion= new SelectQueryUtil();
        SelectQueryUtil selectQueryUtilUnionPaqueteVelatorio= new SelectQueryUtil();
        SelectQueryUtil selectQueryUtilUnionPaqueteServicio= new SelectQueryUtil();
        SelectQueryUtil selectQueryUtilUnionPaqueteArticulo= new SelectQueryUtil();

        selectQueryUtilUnionPaqueteVelatorio.select("SP.ID_PAQUETE","SP.DES_NOM_PAQUETE","SP.DES_PAQUETE")
                .from("SVT_PAQUETE SP")
                .innerJoin("SVT_PAQUETE_VELATORIO SPV", "SP.ID_PAQUETE=SPV.ID_PAQUETE")
                .where("SP.IND_ACTIVO = 1")
                .and("SPV.ID_VELATORIO = "+idVelatorio);

        selectQueryUtilUnionPaqueteRegion.select("SP.ID_PAQUETE","SP.DES_NOM_PAQUETE","SP.DES_PAQUETE")
                .from("SVT_PAQUETE SP")
                .where("SP.IND_ACTIVO =1 ")
                .and("SP.IND_REGION =1");

        selectQueryUtilUnionPaqueteServicio.select("SPS.ID_PAQUETE")
                .from("SVT_PROVEEDOR SP")
                .innerJoin("SVT_CONTRATO SC", "SC.ID_PROVEEDOR = SP.ID_PROVEEDOR")
                .innerJoin("SVT_CONTRATO_SERVICIO SCS", "SCS.ID_CONTRATO = SC.ID_CONTRATO")
                .innerJoin("SVT_SERVICIO SS", "SS.ID_SERVICIO = SCS.ID_SERVICIO")
                .innerJoin("SVT_PAQUETE_SERVICIO SPS", "SPS.ID_SERVICIO = SS.ID_SERVICIO")
                .where("SP.IND_ACTIVO =1 ")
                .and("SPS.IND_ACTIVO = 1")
                .and("SP.ID_TIPO_PROVEEDOR =1")
                .and("SC.FEC_FIN_VIG >= CURRENT_DATE()")
                .and("SP.FEC_VIGENCIA >= CURRENT_DATE()")
                .and("SC.IND_ACTIVO =1");

        selectQueryUtilUnionPaqueteArticulo.select("DISTINCT SPA.ID_PAQUETE")
                .from("SVT_INVENTARIO_ARTICULO STI")
                .innerJoin("SVT_ARTICULO STA", "STA.ID_ARTICULO =STI.ID_ARTICULO")
                .innerJoin("SVC_CATEGORIA_ARTICULO SCA", "SCA.ID_CATEGORIA_ARTICULO = STA.ID_CATEGORIA_ARTICULO")
                .innerJoin("SVT_PAQUETE_ARTICULO SPA", "SPA.ID_CATEGORIA_ARTICULO = SCA.ID_CATEGORIA_ARTICULO")
                .where("STI.IND_ESTATUS = 0")
                .and("STI.ID_TIPO_ASIGNACION_ART IN (1,3)");

        String queryPaqueteRegion=selectQueryUtilUnionPaqueteVelatorio.union(selectQueryUtilUnionPaqueteRegion);
        String queryPaqueteServiciosArticulos=selectQueryUtilUnionPaqueteServicio.union(selectQueryUtilUnionPaqueteArticulo);

        selectQueryUtilPaquete.select("PAQUETES.ID_PAQUETE AS idPaquete","PAQUETES.DES_NOM_PAQUETE AS nomPaquete", "PAQUETES.DES_PAQUETE AS descPaquete")
                .from("("+queryPaqueteRegion+") PAQUETES")
                .where("PAQUETES.ID_PAQUETE IN("+queryPaqueteServiciosArticulos+")");

        String query=selectQueryUtilPaquete.build();
        log.info(query);

        String encoded=DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
        parametros.put(AppConstantes.QUERY, encoded);
        datosRequest.setDatos(parametros);
        return datosRequest;
    }

    public DatosRequest consultarCurpRfc(String curp, String rfc) {
        DatosRequest dr = new DatosRequest();
        Map<String, Object> parametro = new HashMap<>();
        if(rfc.equals("\"\"")){
            log.info("rfc vacio");
            SelectQueryUtil query = new SelectQueryUtil();
            query.select("SP.ID_PERSONA as idPersona","SP.CVE_RFC AS rfc", "SP.CVE_CURP AS curp", "SP.CVE_NSS AS nss", "SP.NOM_PERSONA AS nomPersona",
                            "SC.ID_CONTRATANTE as idDelContratante",
                            "SP.NOM_PRIMER_APELLIDO AS primerApellido", "SP.NOM_SEGUNDO_APELLIDO AS segundoApellido",
                            "SP.NUM_SEXO AS sexo","IFNULL(SP.DES_OTRO_SEXO,'') AS otroSexo" ,"SP.FEC_NAC AS fechaNacimiento", "SP.ID_PAIS AS idPais", "SP.ID_ESTADO AS idEstado",
                            "SP.DES_TELEFONO AS telefono", "SP.DES_CORREO AS correo", "SP.TIP_PERSONA AS tipoPersona",
                            "(SELECT COUNT(CPF.ID_CONTRA_PAQ_CONVENIO_PF) FROM SVT_CONTRATANTE_PAQUETE_CONVENIO_PF CPF WHERE CPF.ID_CONTRATANTE = SC.ID_CONTRATANTE ) AS tieneConvenio",
                            "(SELECT C.DES_FOLIO  FROM SVT_CONTRATANTE_PAQUETE_CONVENIO_PF CPF LEFT JOIN SVT_CONVENIO_PF C ON CPF.ID_CONVENIO_PF = C.ID_CONVENIO_PF WHERE CPF.ID_CONTRATANTE = SC.ID_CONTRATANTE ) AS folioConvenio",
                            "DATE_FORMAT((SELECT C.FEC_ALTA  FROM SVT_CONTRATANTE_PAQUETE_CONVENIO_PF CPF LEFT JOIN SVT_CONVENIO_PF C ON CPF.ID_CONVENIO_PF = C.ID_CONVENIO_PF where CPF.ID_CONTRATANTE = SC.ID_CONTRATANTE ),'%d/%m/%Y') AS fecha")
                    .from("SVC_CONTRATANTE SC")
                    .leftJoin("SVC_PERSONA SP", "SC.ID_PERSONA = SP.ID_PERSONA")
                    .where("SP.CVE_CURP = " + curp);
            String consulta = query.build();
            log.info(consulta);
            String encoded = DatatypeConverter.printBase64Binary(consulta.getBytes());
            parametro.put(AppConstantes.QUERY, encoded);
            dr.setDatos(parametro);
            return dr;
        }
        String consultaElse = busquedaRfcCurp(curp, rfc);
        String encoded = DatatypeConverter.printBase64Binary(consultaElse.getBytes());
        parametro.put(AppConstantes.QUERY, encoded);
        dr.setDatos(parametro);
        return dr;
    }

    public String busquedaRfcCurp(String curp, String rfc) {
        SelectQueryUtil query = new SelectQueryUtil();
        query.select("SP.ID_PERSONA as idPersona", "SP.CVE_RFC AS rfc", "SP.CVE_CURP AS curp", "SP.CVE_NSS AS nss", "SP.NOM_PERSONA AS nomPersona",
                        "SC.ID_CONTRATANTE as idDelContratante",
                        "SP.NOM_PRIMER_APELLIDO AS primerApellido", "SP.NOM_SEGUNDO_APELLIDO AS segundoApellido",
                        "SP.NUM_SEXO AS sexo","IFNULL(SP.DES_OTRO_SEXO,'') AS otroSexo" , "SP.FEC_NAC AS fechaNacimiento", "SP.ID_PAIS AS idPais", "SP.ID_ESTADO AS idEstado",
                        "SP.DES_TELEFONO AS telefono", "SP.DES_CORREO AS correo", "SP.TIP_PERSONA AS tipoPersona",
                        "(SELECT COUNT(CPF.ID_CONTRA_PAQ_CONVENIO_PF) FROM SVT_CONTRATANTE_PAQUETE_CONVENIO_PF CPF WHERE CPF.ID_CONTRATANTE = SC.ID_CONTRATANTE ) AS tieneConvenio",
                        "(SELECT C.DES_FOLIO  FROM SVT_CONTRATANTE_PAQUETE_CONVENIO_PF CPF LEFT JOIN SVT_CONVENIO_PF C ON CPF.ID_CONVENIO_PF = C.ID_CONVENIO_PF WHERE CPF.ID_CONTRATANTE = SC.ID_CONTRATANTE ) AS folioConvenio",
                "DATE_FORMAT((SELECT C.FEC_ALTA  FROM SVT_CONTRATANTE_PAQUETE_CONVENIO_PF CPF LEFT JOIN SVT_CONVENIO_PF C ON CPF.ID_CONVENIO_PF = C.ID_CONVENIO_PF where CPF.ID_CONTRATANTE = SC.ID_CONTRATANTE ),'%d/%m/%Y') AS fecha")
                .from("SVC_CONTRATANTE SC")
                .leftJoin("SVC_PERSONA SP", "SC.ID_PERSONA = SP.ID_PERSONA")
                .where("SP.CVE_RFC = " + rfc)
                .or("SP.CVE_CURP = " + curp);
        String consulta = query.build();
        log.info(consulta);
        return consulta;
    }

    public DatosRequest consultarCP(String cp) {
        DatosRequest dr = new DatosRequest();
        Map<String, Object> parametro = new HashMap<>();
        SelectQueryUtil query = new SelectQueryUtil();
        query.select("SC.CVE_CODIGO_POSTAL AS codigoPostal", "SC.DES_COLONIA AS colonia",
                        "SC.DES_MNPIO AS municipio", "SC.DES_ESTADO AS estado")
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
        querySelect.select("SCP.ID_CONVENIO_PF AS idConvenioPf", "SCP.DES_FOLIO AS folioConvenioPf", "SCP.ID_VELATORIO AS idVelatorio", "SV.DES_VELATORIO AS nombreVelatorio",
                        "SCP.ID_PROMOTOR AS idPromotor", "PROM.NUM_EMPLEDO AS numeroEmpleado", "PROM.NOM_PROMOTOR AS nombrePromotor", "PROM.NOM_PAPELLIDO AS primerApellidoPromotor",
                        "PROM.NOM_SAPELLIDO AS segundoApellidoPromotor", "CPF.ID_CONTRA_PAQ_CONVENIO_PF AS idContratanteConvenioPf", "CPF.ID_CONTRATANTE AS idContratante",
                        "SC.CVE_MATRICULA AS cveMatricula", "SC.ID_PERSONA AS idPersona", "SP.CVE_RFC AS rfc", "SP.CVE_CURP AS curp", "SP.CVE_NSS AS nss", "SP.NOM_PERSONA AS nombrePersona",
                        "SP.NOM_PRIMER_APELLIDO AS primerApellido", "SP.NOM_SEGUNDO_APELLIDO AS segundoApellido", "SP.NUM_SEXO AS numSexo", "SP.ID_PAIS AS idPais", "SP.ID_ESTADO AS idEstado",
                        "SP.DES_TELEFONO AS telefono", "SP.DES_CORREO AS correo", "SP.TIP_PERSONA AS tipoPersona", "SP.NUM_INE AS numIne",
                        "SP.DES_OTRO_SEXO AS otroSexo",
                        "CPF.ID_PAQUETE AS idPaquete", "PAQ.DES_NOM_PAQUETE AS nombrePaquete", "DATE_FORMAT(SP.FEC_NAC,'%Y-%m-%d') AS fechaNacimiento",
                        "SD.DES_CALLE AS calle", "SD.NUM_EXTERIOR AS numExterior","SD.NUM_INTERIOR AS numInterior","SD.DES_CP AS cp","SD.DES_COLONIA AS colonia",
                        "SD.DES_MUNICIPIO AS municipio","SD.DES_ESTADO AS estado", "CPF.ID_ENFERMEDAD_PREXISTENTE AS idEnfermedadPreexistente", "CPF.DES_OTRA_ENFERMEDAD AS otraEnfermedad")
                .from("SVT_CONVENIO_PF SCP")
                .leftJoin("SVC_VELATORIO SV", "SCP.ID_VELATORIO = SV.ID_VELATORIO")
                .leftJoin("SVT_PROMOTOR PROM", "SCP.ID_PROMOTOR = PROM.ID_PROMOTOR")
                .leftJoin("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF CPF", "SCP.ID_CONVENIO_PF = CPF.ID_CONVENIO_PF")
                .leftJoin("SVT_PAQUETE PAQ", "CPF.ID_PAQUETE = PAQ.ID_PAQUETE")
                .leftJoin("SVC_CONTRATANTE SC", "CPF.ID_CONTRATANTE = SC.ID_CONTRATANTE")
                .leftJoin("SVC_PERSONA SP", "SC.ID_PERSONA = SP.ID_PERSONA")
                .leftJoin("SVT_DOMICILIO SD"," SC.ID_DOMICILIO = SD.ID_DOMICILIO")
                .leftJoin("SVT_CONTRATANTE_BENEFICIARIOS SCB", "CPF.ID_CONTRA_PAQ_CONVENIO_PF = SCB.ID_CONTRA_PAQ_CONVENIO_PF")
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
                        "EMP.ID_DOMICILIO AS idDomicilio", "SD.DES_CALLE AS calle", "SD.NUM_EXTERIOR AS numExterior",
                        "IFNULL(SD.NUM_INTERIOR,'') AS numInterior",
                        "SD.DES_CP AS cp", "SD.DES_COLONIA AS desColonia", "SD.DES_MUNICIPIO AS desMunicipio", "SD.DES_ESTADO AS desEstado",
                        "EMP.DES_TELEFONO AS telefono", "EMP.DES_CORREO AS correo","IFNULL(SCP.ID_PROMOTOR,'') AS idPromotor",
                        "IFNULL(CONCAT(PRO.NOM_PROMOTOR, ' ', PRO.NOM_PAPELLIDO, ' ', PRO.NOM_SAPELLIDO),'')  AS nomPromotor",
                "SCP.ID_VELATORIO as idVelatorio","VEL.DES_VELATORIO AS desVelatorio")
                .from("SVT_CONVENIO_PF SCP")
                .leftJoin("SVT_EMPRESA_CONVENIO_PF EMP", "SCP.ID_CONVENIO_PF = EMP .ID_CONVENIO_PF")
                .leftJoin("SVT_DOMICILIO SD", "EMP.ID_DOMICILIO = SD.ID_DOMICILIO")
                .leftJoin("SVT_PROMOTOR PRO","SCP.ID_PROMOTOR = PRO.ID_PROMOTOR")
                .leftJoin("SVC_VELATORIO VEL","SCP.ID_VELATORIO = VEL.ID_VELATORIO")
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
                        "SP2.CVE_RFC AS rfc", "SP2.CVE_CURP AS curp", "SP2.CVE_NSS  AS nss", "SP2.NUM_SEXO AS numSexo", "SP2.DES_OTRO_SEXO AS otroSexo","SP2.NUM_SEXO AS numSexo","SP2.ID_ESTADO AS idEstado",
                        "SP2.ID_ESTADO as idEstado", "E.DES_ESTADO AS desEstado",
                        "SP2.DES_TELEFONO AS telefono", "SP2.DES_CORREO AS correo", "SP2.TIP_PERSONA AS tipoPersona", "SP2.NUM_INE AS numIne", "TIMESTAMPDIFF(YEAR, SP2.FEC_NAC , NOW()) AS edad",
                        "SCB.IND_INE_BENEFICIARIO AS validaIneBeneficiario" , "SCB.IND_ACTA_NACIMIENTO AS validaActaNacimientoBeneficiario", "SCB.ID_PARENTESCO  AS idParentesco","SCB.CVE_ACTA as cveActa",
                        "SV.ID_VELATORIO AS idVelatorio","SV.DES_VELATORIO AS nomVelatorio")
                .from("SVT_CONVENIO_PF SCP")
                .leftJoin("SVC_VELATORIO SV", "SCP.ID_VELATORIO = SV.ID_VELATORIO")
                .leftJoin("SVT_PROMOTOR PROM", "SCP.ID_PROMOTOR = PROM.ID_PROMOTOR")
                .leftJoin("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF CPF", "SCP.ID_CONVENIO_PF = CPF.ID_CONVENIO_PF")
                .leftJoin("SVT_PAQUETE PAQ", "CPF.ID_PAQUETE = PAQ.ID_PAQUETE")
                .leftJoin("SVC_CONTRATANTE SC", "CPF.ID_CONTRATANTE = SC.ID_CONTRATANTE")
                .leftJoin("SVC_PERSONA SP", "SC.ID_PERSONA = SP.ID_PERSONA")
                .leftJoin("SVT_CONTRATANTE_BENEFICIARIOS SCB", "CPF.ID_CONTRA_PAQ_CONVENIO_PF = SCB.ID_CONTRA_PAQ_CONVENIO_PF")
                .leftJoin("SVC_PERSONA SP2", "SCB.ID_PERSONA = SP2.ID_PERSONA")
                .leftJoin("SVC_ESTADO E","SP2.ID_ESTADO = E.ID_ESTADO")
                .where("SCP.DES_FOLIO = " + folioConvenio);
        String consulta = querySelect.build();
        log.info(consulta);
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
        query.agregarParametroValues("ID_USUARIO_MODIFICA", String.valueOf(user.getIdUsuario()));
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
        datosPdf.put("nombreAfiliado", infoReporte.getNombrePersona() + " " + infoReporte.getPrimerApellido() + " " + infoReporte.getSegundoApellido());
        datosPdf.put("numeroINE", infoReporte.getNumIne());
        datosPdf.put("paqueteContratado", infoReporte.getNombrePaquete());
        datosPdf.put("serviciosIncluidos", infoReporte.getDesPaquete());
        datosPdf.put("costoPaquete", infoReporte.getMonPrecio());
        datosPdf.put("nombreTitular", infoReporte.getNombrePersona() + " " + infoReporte.getPrimerApellido() + " " + infoReporte.getSegundoApellido());
        datosPdf.put("rfc", infoReporte.getRfc());
        datosPdf.put("idConvenio", pdfDto.getIdConvenio());
        datosPdf.put("ciudadExpedicion", pdfDto.getCiudadExpedicion());
        datosPdf.put("fechaExpedicion", pdfDto.getFechaExpedicion());
        datosPdf.put("folioConvenio", infoReporte.getFolio());
        return datosPdf;
    }

    public DatosRequest busquedaFolioParaReporte(String idConvenio) {
        DatosRequest dr = new DatosRequest();
        Map<String, Object> parametro = new HashMap<>();
        SelectQueryUtil querySelect = new SelectQueryUtil();
        querySelect.select("SP.CVE_RFC AS rfc", "SP.CVE_CURP AS curp", "SP.CVE_NSS AS nss", "SP.NOM_PERSONA AS nombrePersona",
                        "SP.NOM_PRIMER_APELLIDO AS primerApellido", "SP.NOM_SEGUNDO_APELLIDO AS segundoApellido", "SP.NUM_INE AS numIne", "SCP.DES_FOLIO AS folio",
                        "CPF.ID_PAQUETE AS idPaquete", "PAQ.DES_NOM_PAQUETE AS nombrePaquete", "PAQ.DES_PAQUETE AS desPaquete", "PAQ.MON_PRECIO AS monPrecio")
                .from("SVT_CONVENIO_PF SCP")
                .leftJoin("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF CPF", "SCP.ID_CONVENIO_PF = CPF.ID_CONVENIO_PF")
                .leftJoin("SVT_PAQUETE PAQ", "CPF.ID_PAQUETE = PAQ.ID_PAQUETE")
                .leftJoin("SVC_CONTRATANTE SC", "CPF.ID_CONTRATANTE = SC.ID_CONTRATANTE")
                .leftJoin("SVC_PERSONA SP", "SC.ID_PERSONA = SP.ID_PERSONA")
                .where("SCP.ID_CONVENIO_PF = '" + idConvenio + "'")
                .groupBy("SCP.DES_FOLIO");
        String consulta = querySelect.build();
        String encoded = DatatypeConverter.printBase64Binary(consulta.getBytes());
        parametro.put(AppConstantes.QUERY, encoded);
        dr.setDatos(parametro);
        return dr;
    }



}
