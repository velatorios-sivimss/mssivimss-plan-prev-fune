package com.imss.sivimss.planfunerario.beans;

import com.imss.sivimss.planfunerario.model.request.ConsultaGeneralRequest;
import com.imss.sivimss.planfunerario.model.request.DatosReporteRequest;
import com.imss.sivimss.planfunerario.service.impl.ConsultaConveniosServiceImpl;
import com.imss.sivimss.planfunerario.util.AppConstantes;
import com.imss.sivimss.planfunerario.util.DatosRequest;
import com.imss.sivimss.planfunerario.util.SelectQueryUtil;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ConsultaConvenios {
    @Value("${formato_fecha}")
    private String formatoFecha;
    // todo - revisar si los estatus son correctos
    private final static int ESTATUS_VIGENTE = 1;
    private final static int ESTATUS_RENOVACION = 2;
    private final static int ESTATUS_CERRADO = 3;
    private final static int CONVENIO_PERSONA = 1;
    private final static int CONVENIO_EMPRESA = 2;
    // todo - agregar las demas tablas
    private final static String SVT_CONVENIO = "SVT_CONVENIO_PF";
    private final static String ALIAS_FECHA_NACIMIENTO = "fechaNacimiento";
    private final static String ALIAS_EDAD = "edad";
    private final static String ALIAS_PARENTESCO = "descripcionParentesco";
    private final static Integer PARENTESCO_HIJO = 4;
    private final static Integer TIPO_CONTRATACION_EMPRESA = 0;
    private final static Integer TIPO_CONTRATACION_PERSONA = 1;
    private final static String ALIAS_NOMBRE_BENEFICIARIO = "nombreBeneficiario";

    /**
     * Recupera la lista de convenios del sistema.
     *
     * @return
     */
    public DatosRequest consultarConvenios(DatosRequest request, ConsultaGeneralRequest filtros) throws UnsupportedEncodingException {
        SelectQueryUtil queryConveniosPersona = new SelectQueryUtil();
        SelectQueryUtil queryBeneficiarios = new SelectQueryUtil();
        queryBeneficiarios.select("count(*)")
                .from("SVT_CONTRATANTE_BENEFICIARIOS beneficiarios")
                .where("beneficiarios.ID_CONTRA_PAQ_CONVENIO_PF = contratanteConvenio.ID_CONTRA_PAQ_CONVENIO_PF")
                .and("beneficiarios.IND_ACTIVO=1").and("beneficiarios.IND_SINIESTROS=0");

        SelectQueryUtil queryFacturas = new SelectQueryUtil();
        queryFacturas.select()
                .from("SVC_FACTURA factura")
                .join("SVT_PAGO_BITACORA pago",
                        "pago.ID_PAGO_BITACORA = factura.ID_PAGO",
                        "pago.CVE_ESTATUS_PAGO = 5")
                .where("pago.CVE_FOLIO = convenio.DES_FOLIO");

        queryConveniosPersona.select("convenio.ID_CONVENIO_PF as idConvenio",
                        "convenio.DES_FOLIO as folioConvenio",
                        recuperarNombrePersona("personaContratante", "nombreContratante"),
                        formatearFecha("convenio.FEC_INICIO") + " as fechaContratacion", // La fecha de inicio sera la fecha de contratacion o sera la fecha de alta
                        formatearFecha("if(convenio.IND_RENOVACION = false, convenio.FEC_INICIO, renovacionConvenio.FEC_INICIO)")
                                + " as fechaVigenciaInicio", // cuando un convenio no tenga renovacion la fecha inicio sera la fecha de inicio, de lo contrario habra que recuperar la fecha de renovacion?
                        formatearFecha("if(convenio.IND_RENOVACION = false, convenio.FEC_VIGENCIA, renovacionConvenio.FEC_VIGENCIA)")
                                + " as fechaVigenciaFin",
                        "(" + queryBeneficiarios.build() + ") as cantidadBeneficiarios",
                        "if(convenio.IND_RENOVACION = false, 'No Renovado', 'Renovado') as situacion",
                        "if(convenio.IND_RENOVACION = false, null, renovacionConvenio.ID_ESTATUS) as estatusRenovacion",
                        "convenio.ID_TIPO_PREVISION as tipoPlan",
                        "exists(" + queryFacturas.build() + ") as factura", // ver que es lo que regresa en la consulta
                        formatearImporte("paquete.MON_PRECIO") + " as importeConvenio",
                        "estatus.DES_ESTATUS as estatusConvenio")
                .from(SVT_CONVENIO + " convenio")
                .join("SVC_ESTATUS_CONVENIO_PF estatus",
                        "estatus.ID_ESTATUS_CONVENIO_PF = convenio.ID_ESTATUS_CONVENIO")
                .leftJoin("SVT_RENOVACION_CONVENIO_PF renovacionConvenio",
                        "renovacionConvenio.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF",
                        "renovacionConvenio.ID_ESTATUS = 2")
                .join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF contratanteConvenio",
                        "contratanteConvenio.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
                .join("SVT_PAQUETE paquete",
                        "paquete.ID_PAQUETE = contratanteConvenio.ID_PAQUETE")
                .join("SVC_CONTRATANTE contratante",
                        "contratante.ID_CONTRATANTE = contratanteConvenio.ID_CONTRATANTE")
                .join("SVC_PERSONA personaContratante",
                        "personaContratante.id_persona = contratante.id_persona")
                .where("convenio.IND_TIPO_CONTRATACION = :tipoContratacion")
                .setParameter("tipoContratacion", TIPO_CONTRATACION_PERSONA); // persona -> true

        crearWhereConFiltros(queryConveniosPersona, filtros, true);
        queryConveniosPersona.groupBy("convenio.ID_CONVENIO_PF");

        // queryEmpresas
        SelectQueryUtil queryConveniosEmpresa = new SelectQueryUtil();

        queryConveniosEmpresa.select(
                        "convenio.ID_CONVENIO_PF as idConvenio",
                        "convenio.DES_FOLIO as folioConvenio",
                        "empresaContratante.DES_NOMBRE as nombreContratante",
                        formatearFecha("convenio.FEC_INICIO") + " as fechaContratacion",
                        formatearFecha("if(convenio.IND_RENOVACION = false, convenio.FEC_INICIO, renovacionConvenio.FEC_INICIO)")
                                + " as fechaVigenciaInicio",
                        formatearFecha("if(convenio.IND_RENOVACION = false, convenio.FEC_VIGENCIA, renovacionConvenio.FEC_VIGENCIA)")
                                + " as fechaVigenciaFin",
                        "(" + queryBeneficiarios.build() + ") as cantidadBeneficiarios",
                        "if(convenio.IND_RENOVACION = false, 'No Renovado', 'Renovado') as situacion",
                        "if(convenio.IND_RENOVACION = false, null, renovacionConvenio.ID_ESTATUS) as estatusRenovacion",
                        "convenio.ID_TIPO_PREVISION as tipoPlan",
                        "exists(" + queryFacturas.build() + ") as factura", // ver que es lo que regresa en la consulta
                        formatearImporte("paquete.MON_PRECIO") + " as importeConvenio",
                        "estatus.DES_ESTATUS as estatusConvenio")
                .from(SVT_CONVENIO + " convenio")
                .join("SVC_ESTATUS_CONVENIO_PF estatus",
                        "estatus.ID_ESTATUS_CONVENIO_PF = convenio.ID_ESTATUS_CONVENIO")
                .leftJoin("SVT_RENOVACION_CONVENIO_PF renovacionConvenio",
                        "renovacionConvenio.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF",
                        "renovacionConvenio.ID_ESTATUS = 2")
                .join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF contratanteConvenio",
                        "contratanteConvenio.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
                .join("SVT_PAQUETE paquete",
                        "paquete.ID_PAQUETE = contratanteConvenio.ID_PAQUETE")
                .join("SVT_EMPRESA_CONVENIO_PF empresaContratante",
                        "empresaContratante.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
                .where("convenio.IND_TIPO_CONTRATACION = :tipoContratacion")
                .setParameter("tipoContratacion", TIPO_CONTRATACION_EMPRESA); // empresa -> false
        crearWhereConFiltros(queryConveniosEmpresa, filtros, false);

        queryConveniosEmpresa.groupBy("convenio.ID_CONVENIO_PF");

        String unionPersonaEmpresa = queryConveniosPersona.unionAll(queryConveniosEmpresa);
        log.info("---> "+unionPersonaEmpresa);
        String encoded = queryConveniosPersona.encrypt(unionPersonaEmpresa);
        return recuperarDatos(request, encoded);
    }

    /**
     * Formatea el importe.
     *
     * @param campo
     * @return
     */
    private String formatearImporte(String campo) {
        return "CONCAT('$', FORMAT(" + campo + ", 2))";
    }

    /**
     * Regresa la cadena con la fecha con formato.
     *
     * @param campo
     * @return
     */
    private String formatearFecha(String campo) {
        return "DATE_FORMAT(" + campo + ", '" + formatoFecha + "')";
    }


    /**
     * Consulta los beneficiarios relacionados a un <b>Convenio PF</b>
     *
     * @param request Request necesario con los par&aacute;metros para ejecutar la consulta.
     * @param filtros Se usan para filtrar las consultas, ya sea por empresa o por persona.
     * @return Los par&aacute;metros para realizar la consulta de <b>Beneficiarios</b>.
     */
    public DatosRequest consultarBeneficiarios(DatosRequest request, ConsultaGeneralRequest filtros) throws UnsupportedEncodingException {
        SelectQueryUtil queryBeneficiariosNuevoPlan = new SelectQueryUtil();
        SelectQueryUtil queryBeneficiariosPlanAnterior = new SelectQueryUtil();

        final String[] columnas = {
                recuperarNombrePersona("personaBeneficiario", ALIAS_NOMBRE_BENEFICIARIO),
                formatearFecha("personaBeneficiario.FEC_NAC") + " as " + ALIAS_FECHA_NACIMIENTO,
                recuperarEdad("personaBeneficiario"),
                "parentesco.DES_PARENTESCO as " + ALIAS_PARENTESCO
        };

        // nuevo plan
        crearSelect(queryBeneficiariosNuevoPlan, columnas);
        queryBeneficiariosNuevoPlan
                .from("SVT_CONTRATANTE_BENEFICIARIOS beneficiario")
                .join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF contratantePaquete",
                        "contratantePaquete.ID_CONTRA_PAQ_CONVENIO_PF = beneficiario.ID_CONTRA_PAQ_CONVENIO_PF")
                .join("SVT_CONVENIO_PF convenio",
                        "convenio.ID_CONVENIO_PF = contratantePaquete.ID_CONVENIO_PF")
                .join("SVC_PERSONA personaBeneficiario",
                        "personaBeneficiario.ID_PERSONA = beneficiario.ID_PERSONA")
                .join("SVC_PARENTESCO parentesco",
                        "parentesco.ID_PARENTESCO = beneficiario.ID_PARENTESCO");
        agregarCondicionBeneficiarios(filtros, queryBeneficiariosNuevoPlan, false, false);

        // query para el plan anterior
        crearSelect(queryBeneficiariosPlanAnterior, columnas);
        queryBeneficiariosPlanAnterior
                .from("SVT_CONTRATANTE_BENEFICIARIOS beneficiario")
               // .join("SVT_BENEFICIARIOS_DOCUMENTACION_PLAN_ANTERIOR documentacion",
                 //       "beneficiario.ID_CONTRATANTE_BENEFICIARIOS = documentacion.ID_CONTRATANTE_BENEFICIARIOS")
                .join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF contratantePaquete",
                        "contratantePaquete.ID_CONTRA_PAQ_CONVENIO_PF = beneficiario.ID_CONTRA_PAQ_CONVENIO_PF")
                .join("SVT_CONVENIO_PF convenio",
                        "convenio.ID_CONVENIO_PF = contratantePaquete.ID_CONVENIO_PF")
                .join("SVC_PERSONA personaBeneficiario",
                        "personaBeneficiario.ID_PERSONA = beneficiario.ID_PERSONA")
                .join("SVC_PARENTESCO parentesco",
                        "parentesco.ID_PARENTESCO = beneficiario.ID_PARENTESCO");
        agregarCondicionBeneficiarios(filtros, queryBeneficiariosPlanAnterior, false, true);

        // query plan anterior
        SelectQueryUtil queryBeneficiarioPlanAnteriorHijos = new SelectQueryUtil();
        crearSelect(queryBeneficiarioPlanAnteriorHijos, columnas);
        queryBeneficiarioPlanAnteriorHijos
                .from("SVT_CONTRATANTE_BENEFICIARIOS beneficiario")
                .join("SVT_BENEFICIARIOS_DOCUMENTACION_PLAN_ANTERIOR documentacion",
                        "beneficiario.ID_CONTRATANTE_BENEFICIARIOS = documentacion.ID_CONTRATANTE_BENEFICIARIOS")
                .join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF contratantePaquete",
                        "contratantePaquete.ID_CONTRA_PAQ_CONVENIO_PF = beneficiario.ID_CONTRA_PAQ_CONVENIO_PF")
                .join("SVT_CONVENIO_PF convenio",
                        "convenio.ID_CONVENIO_PF = contratantePaquete.ID_CONVENIO_PF")
                .join("SVC_PERSONA personaBeneficiario",
                        "personaBeneficiario.ID_PERSONA = beneficiario.ID_PERSONA")
                .join("SVC_PARENTESCO parentesco",
                        "parentesco.ID_PARENTESCO = beneficiario.ID_PARENTESCO");
        agregarCondicionBeneficiarios(filtros, queryBeneficiarioPlanAnteriorHijos, true, true);

        final String unionBeneficiarios = queryBeneficiariosPlanAnterior.unionAll(queryBeneficiarioPlanAnteriorHijos);

        final String query = queryBeneficiariosNuevoPlan.build() + " UNION ALL " + unionBeneficiarios;
        log.info("beneficiarios --> "+query);
        String encoded = queryBeneficiariosNuevoPlan.encrypt(query);
        return recuperarDatos(request, encoded);
    }


    /**
     * Consulta los siniestros relacionados a un convenio, puede ser de tipo empresa o por persona.
     *
     * @param request Request necesario con los par&aacute;metros para ejecutar la consulta.
     * @param filtros Se usan para filtrar las consultas, ya sea por empresa o por persona.
     * @return Los par&aacute;metros para realizar la consulta de <b>Siniestros</b>.
     */
    public DatosRequest consultarSiniestros(DatosRequest request, ConsultaGeneralRequest filtros) {
        SelectQueryUtil querySiniestros = new SelectQueryUtil();
        final String[] columnas = {
                "velatorio.DES_VELATORIO as nombreVelatorio",
                formatearFecha("ods.FEC_ALTA") + " as fechaSiniestro",
                "ods.CVE_FOLIO as folioSiniestro",
                recuperarNombrePersona("personaFinado", "nombreFinado"),
                "parentesco.DES_PARENTESCO as descripcionParentesco",
                "velatorioOrigen.DES_VELATORIO as velatorioOrigen",
                formatearImporte("presupuesto.CAN_PRESUPUESTO") + " as importe"
        };

        // todo - falta agregar la condicion del pago
        querySiniestros.select(columnas)
                .from("SVC_ORDEN_SERVICIO ods")
                .join("SVC_VELATORIO velatorio",
                        "velatorio.ID_VELATORIO = ods.ID_VELATORIO")
                .join("SVC_FINADO finado",
                        "finado.ID_ORDEN_SERVICIO = ods.ID_ORDEN_SERVICIO",
                        "finado.ID_TIPO_ORDEN = 2") // sacar a una constante
                .join("SVT_CONVENIO_PF convenio",
                        "convenio.ID_CONVENIO_PF = finado.ID_CONTRATO_PREVISION")
                .join("SVC_VELATORIO velatorioOrigen",
                        "velatorioOrigen.ID_VELATORIO = convenio.ID_VELATORIO")
                .join("SVC_PERSONA personaFinado",
                        "personaFinado.ID_PERSONA = finado.ID_PERSONA")
                .join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF contratanteConvenio",
                        "contratanteConvenio.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
                .join("SVT_CONTRATANTE_BENEFICIARIOS beneficiario",
                        "beneficiario.ID_CONTRA_PAQ_CONVENIO_PF = contratanteConvenio.ID_CONTRA_PAQ_CONVENIO_PF",
//                        "beneficiario.IND_ACTIVO = true",
                        "beneficiario.ID_PERSONA = finado.ID_PERSONA")
                .join("SVC_PARENTESCO parentesco",
                        "parentesco.ID_PARENTESCO = beneficiario.ID_PARENTESCO")
                .join("SVC_CARAC_PRESUPUESTO presupuesto",
                        "presupuesto.ID_ORDEN_SERVICIO = ods.ID_ORDEN_SERVICIO")
                .where("convenio.DES_FOLIO = :folioConvenio",
                        "ods.ID_ESTATUS_ORDEN_SERVICIO in (4, 5)") // sacar a una constante
                .setParameter("folioConvenio", filtros.getFolioConvenio());
        if (filtros.getFolioSiniestro() != null) {
            querySiniestros.where("ods.cve_folio = :folioSiniestro")
                    .setParameter("folioSiniestro", filtros.getFolioSiniestro());
        }

        SelectQueryUtil querySiniestrosContratante = new SelectQueryUtil();
        querySiniestrosContratante.select(
                        "velatorio.DES_VELATORIO as nombreVelatorio",
                        formatearFecha("ods.FEC_ALTA") + " as fechaSiniestro",
                        "ods.CVE_FOLIO as folioSiniestro",
                        recuperarNombrePersona("personaFinado", "nombreFinado"),
                        "parentesco.DES_PARENTESCO as descripcionParentesco",
                        "velatorioOrigen.DES_VELATORIO as velatorioOrigen",
                        formatearImporte("presupuesto.CAN_PRESUPUESTO") + " as importe"
                )
                .from("SVC_ORDEN_SERVICIO ods")
                .join("SVC_VELATORIO velatorio",
                        "velatorio.ID_VELATORIO = ods.ID_VELATORIO")
                .join("SVC_FINADO finado",
                        "finado.ID_ORDEN_SERVICIO = ods.ID_ORDEN_SERVICIO",
                        "finado.ID_TIPO_ORDEN = 2") // sacar a una constante
                .join("SVT_CONVENIO_PF convenio",
                        "convenio.ID_CONVENIO_PF = finado.ID_CONTRATO_PREVISION")
                .join("SVC_VELATORIO velatorioOrigen",
                        "velatorioOrigen.ID_VELATORIO = convenio.ID_VELATORIO")
                .join("SVC_PERSONA personaFinado",
                        "personaFinado.ID_PERSONA = finado.ID_PERSONA")
                .join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF contratanteConvenio",
                        "contratanteConvenio.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
                .join("SVC_CONTRATANTE contratante",
                        "contratante.ID_CONTRATANTE = ods.ID_CONTRATANTE",
                        "contratante.ID_CONTRATANTE = contratanteConvenio.ID_CONTRATANTE")
                .join("SVC_PERSONA personaContratante",
                        "personaContratante.ID_PERSONA = contratante.ID_PERSONA",
                        "personaContratante.id_persona = personaFinado.id_persona")
                .join("SVC_PARENTESCO parentesco",
                        "parentesco.ID_PARENTESCO = ods.ID_PARENTESCO")
                .join("SVC_CARAC_PRESUPUESTO presupuesto",
                        "presupuesto.ID_ORDEN_SERVICIO = ods.ID_ORDEN_SERVICIO")
                .join("SVT_PAGO_BITACORA pago",
                        "pago.CVE_FOLIO = ods.CVE_FOLIO",
                        "pago.CVE_ESTATUS_PAGO in (4, 5)") // sacar a una constante
                .where("convenio.DES_FOLIO = :folioConvenio",
                        "ods.ID_ESTATUS_ORDEN_SERVICIO in (4, 5)")// sacar a una constante
                .setParameter("folioConvenio", filtros.getFolioConvenio());
        if (filtros.getFolioSiniestro() != null) {
            querySiniestrosContratante.where("ods.CVE_FOLIO = :folioSiniestro")
                    .setParameter("folioSiniestro", filtros.getFolioSiniestro());
        }

        final String query = querySiniestros.unionAll(querySiniestrosContratante);
        String encoded = querySiniestros.encrypt(query);
        return recuperarDatos(request, encoded);
    }

    /**
     * Consulta de afiliados relacionados a una Empresa y esta a un Convenio PF.
     *
     * @param request Request necesario con los par&aacute;metros para ejecutar la consulta.
     * @param filtros Se usan para filtrar las consultas, ya sea por empresa o por persona.
     * @return Los par&aacute;metros para realizar la consulta de <b>Afiliados</b>.
     */
    public DatosRequest consultarAfiliados(DatosRequest request, ConsultaGeneralRequest filtros) throws UnsupportedEncodingException {

        SelectQueryUtil queryAfiliados = new SelectQueryUtil();

        queryAfiliados.select(
                        "velatorio.DES_VELATORIO as nombreVelatorio",
                        recuperarNombrePersona("personaAfiliada", "nombreAfiliado"),
                        "empresaContratante.DES_RFC as rfcTitular",
                        formatearFecha("personaAfiliada.FEC_NAC") + "as " + ALIAS_FECHA_NACIMIENTO,
                        recuperarEdad("personaAfiliada"),
                        "personaAfiliada.NUM_SEXO as genero",
                        "personaAfiliada.DES_CORREO as correo"
                )
                .from("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF contratantePaquete")
                .join("SVT_CONVENIO_PF convenio",
                        "convenio.ID_CONVENIO_PF = contratantePaquete.ID_CONVENIO_PF")
                .join("SVC_VELATORIO velatorio",
                        "velatorio.ID_VELATORIO = convenio.ID_VELATORIO")
                .leftJoin("SVT_EMPRESA_CONVENIO_PF empresaContratante",
                        "empresaContratante.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF",
                        "convenio.IND_TIPO_CONTRATACION = false")
                .join("SVC_CONTRATANTE contratante",
                        "contratante.ID_CONTRATANTE = contratantePaquete.ID_CONTRATANTE")
                .join("SVC_PERSONA personaAfiliada",
                        "personaAfiliada.ID_PERSONA = contratante.ID_PERSONA")
                .where(
                		//"convenio.IND_TIPO_CONTRATACION = false",
                        "convenio.DES_FOLIO = :folioConvenio")
                .setParameter("folioConvenio", filtros.getFolioConvenio());
        if (filtros.getRfc() != null) {
            queryAfiliados.where("personaAfiliada.CVE_RFC = :rfc")
                    .setParameter("rfc", filtros.getRfc());
        }

        final String query = queryAfiliados.build();
        log.info("query afiliados --> "+query);
        final String encoded = queryAfiliados.encrypt(query);

        return recuperarDatos(request, encoded);
    }

    /**
     * Consulta las vigencias de los convenios, por empresa o por persona.
     *
     * @param request Request necesario con los par&aacute;metros para ejecutar la consulta.
     * @param filtros Se usan para filtrar las consultas, ya sea por empresa o por persona.
     * @return Los par&aacute;metros para realizar la consulta de <b>Vigencias</b>.
     */
    public DatosRequest consultarVigencias(DatosRequest request, ConsultaGeneralRequest filtros) {
        // buscar a Lore para ver de que tablas vamos a sacar al info necesaria para la consulta

        SelectQueryUtil queryVigencias = new SelectQueryUtil();
        queryVigencias.select(
                        "convenio.DES_FOLIO as folioConvenio",
                        formatearFecha("convenio.FEC_INICIO") + " as fechaInicio",
                        formatearFecha("if(convenio.IND_RENOVACION = false, convenio.FEC_VIGENCIA, renovacionConvenio.FEC_VIGENCIA)")
                                + " as fechaFin", // cuando un convenio no tenga renovacion la fecha inicio sera la fecha de inicio, de lo contrario habra que recuperar la fecha de renovacion?
                        formatearFecha("if(convenio.IND_RENOVACION = false, convenio.FEC_INICIO, renovacionConvenio.FEC_INICIO)")
                                + " as fechaRenovacion" // cuando un convenio no tenga renovacion la fecha inicio sera la fecha de inicio, de lo contrario habra que recuperar la fecha de renovacion?
                )
                .from("SVT_CONVENIO_PF convenio")
                .leftJoin("SVT_RENOVACION_CONVENIO_PF renovacionConvenio",
                        "renovacionConvenio.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
                .where("convenio.DES_FOLIO = :folioConvenio") // persona -> true
                .setParameter("folioConvenio", filtros.getFolioConvenio());

        SelectQueryUtil queryVigenciasEmpresa = new SelectQueryUtil();
        queryVigenciasEmpresa.select(
                        "convenio.DES_FOLIO as folioConvenio",
                        formatearFecha("convenio.FEC_INICIO") + " as fechaInicio",
                        formatearFecha("if(convenio.IND_RENOVACION = false, convenio.FEC_VIGENCIA, renovacionConvenio.FEC_VIGENCIA)")
                                + " as fechaFin",
                        formatearFecha("if(convenio.IND_RENOVACION = false, convenio.FEC_INICIO, renovacionConvenio.FEC_INICIO)")
                                + " as fechaRenovacion"
                )
                .from("SVT_CONVENIO_PF convenio")
                .leftJoin("SVT_RENOVACION_CONVENIO_PF renovacionConvenio",
                        "renovacionConvenio.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
                .join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF contratanteConvenio",
                        "contratanteConvenio.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
                .join("SVT_EMPRESA_CONVENIO_PF empresaContratante",
                        "empresaContratante.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
                .where("convenio.IND_TIPO_CONTRATACION = false",
                        "convenio.des_folio = :folioConvenio") // empresa -> false
                .setParameter("folioConvenio", filtros.getFolioConvenio());

        final String query = queryVigencias.build();
        String encoded = queryVigencias.encrypt(query);

        return recuperarDatos(request, encoded);
    }


    /**
     * Consulta las facturas relacionadas a un convenio PF.
     *
     * @param request Request necesario con los par&aacute;metros para ejecutar la consulta.
     * @param filtros Se usan para filtrar las consultas, ya sea por empresa o por persona.
     * @return Los par&aacute;metros para realizar la consulta de <b>Facturas</b>.
     */
    public DatosRequest consultarFacturas(DatosRequest request, ConsultaGeneralRequest filtros) {
        SelectQueryUtil queryFacturas = new SelectQueryUtil();

        final String CONVENIO_ALIAS = SVT_CONVENIO + " convenio";
        queryFacturas.select(
                        "factura.CVE_FOLIO_FISCAL as numeroFactura",
                       // "factura.NUM_UUID as UUID", // cambiar por el nombre que va a tener en la base de datos
                        formatearFecha("factura.FEC_FACTURACION") + " as fecha", // cambiar por la fecha que se estaria registrando
                        "factura.CVE_RFC_CONTRATANTE as rfc",
                        "factura.DES_RAZON_SOCIAL as cliente",
                        "factura.DES_COMENTARIOS as nota",
                        "factura.IMP_TOTAL_PAGADO as total",
                        "estatus.DES_ESTATUS as estatusFactura"
                )
                .from("SVC_FACTURA factura")
                .join("SVC_ESTATUS_FACTURA estatus",
                        "estatus.ID_ESTATUS_FACTURA = factura.ID_ESTATUS_FACTURA")
                .join("SVT_PAGO_BITACORA pago",
                        "pago.ID_PAGO_BITACORA = factura.ID_PAGO")
                .join(CONVENIO_ALIAS,
                        "convenio.DES_FOLIO = pago.CVE_FOLIO")

                .where("convenio.DES_FOLIO = :folioConvenio")
                .setParameter("folioConvenio", filtros.getFolioConvenio()); // empresa -> false

        if (filtros.getNumeroFactura() != null) {
            queryFacturas.where("factura.CVE_FOLIO_FISCAL = :numeroFactura")
                    .setParameter("numeroFactura", filtros.getNumeroFactura());
        }

        final String query = queryFacturas.build();
        String encoded = queryFacturas.encrypt(query);

        return recuperarDatos(request, encoded);
    }

    /**
     * Recupera el nombre de la tabla persona con alias.
     *
     * @param aliasTabla
     * @param aliasCampo
     * @return
     */
    private static String recuperarNombrePersona(String aliasTabla, String aliasCampo) {
        return recuperarNombrePersona(aliasTabla) +
                "as " +
                aliasCampo;
    }

    /**
     * Recupera el nombre completo de la persona sin alias
     *
     * @param aliasTabla
     * @return
     */
    private static String recuperarNombrePersona(String aliasTabla) {
        return "concat(" +
                aliasTabla + "." + "NOM_PERSONA" + ", " +
                "' ', " +
                aliasTabla + "." + "NOM_PRIMER_APELLIDO" + ", " +
                "' ', " +
                aliasTabla + "." + "NOM_SEGUNDO_APELLIDO" + ") ";
    }

    /**
     * Agrega la condicion para la consulta de beneficiarios.
     *
     * @param filtros
     * @param queryUtil
     */
    private static void agregarCondicionBeneficiarios(ConsultaGeneralRequest filtros, SelectQueryUtil queryUtil, boolean esHijo, boolean planAnterior) {
    	 queryUtil.where("convenio.DES_FOLIO = :folioConvenio",
                 "beneficiario.IND_ACTIVO = true",
                 "beneficiario.IND_SINIESTROS = 0")
         .setParameter("folioConvenio", filtros.getFolioConvenio());
    	if (planAnterior) {
    		  queryUtil.where("convenio.ID_TIPO_PREVISION = 2");
            if (esHijo) {
                queryUtil.where("beneficiario.ID_PARENTESCO IN (8,9) AND (TIMESTAMPDIFF(YEAR, personaBeneficiario.FEC_NAC, CURDATE()) < 18")
             
                	.or("(TIMESTAMPDIFF(YEAR, personaBeneficiario.FEC_NAC, CURDATE()) BETWEEN 18 AND 25")
                		.and("documentacion.IND_COMPROBANTE_ESTUDIOS = 1))");
                      
            } else {
                queryUtil.where("(beneficiario.ID_PARENTESCO != 8 AND beneficiario.ID_PARENTESCO !=9)");

            }
          
        } else {
            queryUtil.where("convenio.ID_TIPO_PREVISION = 1");
        }

        if (filtros.getNombreBeneficiario() != null) {
            String condicionNombre = recuperarNombrePersona("personaBeneficiario") + "like '%" + filtros.getNombreBeneficiario() + "%'";
            queryUtil.where(condicionNombre);
        }
    }

    /**
     * Crea una sentencia <b>{@code WHERE}</b> con los filtros que no sean null.
     *
     * @param selectQuery
     * @param filtros
     * @param isPersona
     */
    private void crearWhereConFiltros(SelectQueryUtil selectQuery, ConsultaGeneralRequest filtros, boolean isPersona) {
        if (isPersona) {
            if (filtros.getRfc() != null) {
                selectQuery.where("personaContratante.CVE_RFC = :rfc")
                        .setParameter("rfc", filtros.getRfc());
            }
            if (filtros.getCurp() != null) {
                selectQuery.where("personaContratante.CVE_CURP = :curp")
                        .setParameter("curp", filtros.getCurp());
            }
            if (filtros.getNombre() != null) {
                final String condicion = recuperarNombrePersona("personaContratante") + " like '%" + filtros.getNombre() + "%'";
                selectQuery.where(condicion);
            }
        } else {
            if (filtros.getRfc() != null) {
                selectQuery.where("empresaContratante.DES_RFC = :rfc")
                        .setParameter("rfc", filtros.getRfc());
            }
            if (filtros.getNombre() != null) {
                final String condicion = "empresaContratante.DES_NOMBRE like '%" + filtros.getNombre() + "%'";
                selectQuery.where(condicion);
            }
        }
        if (filtros.getFolioConvenio() != null) {
            selectQuery.where("convenio.DES_FOLIO = :folioConvenio")
                    .setParameter("folioConvenio", filtros.getFolioConvenio());
        }
        if (filtros.getEstatusConvenio() != null) {
            selectQuery.where("convenio.ID_ESTATUS_CONVENIO = :estatusConvenio")
                    .setParameter("estatusConvenio", filtros.getEstatusConvenio());
        }
        if (filtros.getIdVelatorio() != null) {
            selectQuery.where("convenio.ID_VELATORIO = :idVelatorio")
                    .setParameter("idVelatorio", filtros.getIdVelatorio());
        }
    }

    /**
     * Recuprea la edad de la persona con su fecha de nacimiento.
     *
     * @param aliasTabla
     * @return
     */
    private static String recuperarEdad(String aliasTabla) {
//        return "TIMESTAMPDIFF(YEAR, " + aliasTabla + ".FEC_NAC, CURDATE()) as " + ALIAS_EDAD;
        return recuperarEdadSinAlias(aliasTabla) + "as " + ALIAS_EDAD;
    }

    /**
     * Recuprea la edad de la persona con su fecha de nacimiento.
     *
     * @param aliasTabla
     * @return
     */
    private static String recuperarEdadSinAlias(String aliasTabla) {
        return "TIMESTAMPDIFF(YEAR, " + aliasTabla + ".FEC_NAC, CURDATE())";
    }

    /**
     * Recupera los el objeto DatosRequest, para ejecutar la consulta.
     *
     * @param request
     * @param encoded
     * @return
     */
    private static DatosRequest recuperarDatos(DatosRequest request, String encoded) {
        DatosRequest datos = new DatosRequest();
        System.out.println(request);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(AppConstantes.QUERY, encoded);
        parametros.put("pagina", request.getDatos().get("pagina"));
        parametros.put("tamanio", request.getDatos().get("tamanio"));
        datos.setDatos(parametros);
        return datos;
    }

    /**
     * Recupera los datos para el formato del reporte de la tabla de convenios.
     *
     * @param filtros
     * @return
     */
    public Map<String, Object> recuperarDatosFormatoTabla(DatosReporteRequest filtros) {
        Map<String, Object> parametros = new HashMap<>();

        parametros.put("folioConvenio", filtros.getFolioConvenio() != null ? "'" + filtros.getFolioConvenio() + "'" : null);
        parametros.put("nombre", filtros.getNombre() != null ? "'" + filtros.getNombre() + "'" : null);
        parametros.put("curp", filtros.getCurp() != null ? "'" + filtros.getCurp() + "'" : null);
        parametros.put("rfc", filtros.getRfc() != null ? "'" + filtros.getRfc() + "'" : null);
        parametros.put("estatusConvenio", filtros.getEstatusConvenio());

        parametros.put("rutaNombreReporte", filtros.getRuta());
        parametros.put("tipoReporte", filtros.getTipoReporte());
        return parametros;
    }

    /**
     * Crea la sentencia select.
     *
     * @param queryUtil
     * @param columnas
     */
    private void crearSelect(SelectQueryUtil queryUtil, String... columnas) {
        queryUtil.select(columnas
        );
    }
}
