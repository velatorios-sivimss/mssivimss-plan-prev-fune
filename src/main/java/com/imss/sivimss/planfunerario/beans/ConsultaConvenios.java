package com.imss.sivimss.planfunerario.beans;

import com.imss.sivimss.planfunerario.model.request.ConsultaGeneralRequest;
import com.imss.sivimss.planfunerario.model.request.DatosReporteRequest;
import com.imss.sivimss.planfunerario.util.AppConstantes;
import com.imss.sivimss.planfunerario.util.DatosRequest;
import com.imss.sivimss.planfunerario.util.SelectQueryUtil;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Component
public class ConsultaConvenios {
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
        // todo - regresar los convenios de acuerdo al estatus que se seleccione
        SelectQueryUtil queryConveniosPersona = new SelectQueryUtil();
        SelectQueryUtil queryBeneficiarios = new SelectQueryUtil();
        queryBeneficiarios.select("count(*)")
                .from("SVT_CONTRATANTE_BENEFICIARIOS beneficiarios")
                .where("beneficiarios.ID_CONTRATANTE_PAQUETE_CONVENIO_PF = contratanteConvenio.ID_CONTRATANTE_PAQUETE_CONVENIO_PF");

        // todo - revisar la consulta para facturas
        // consultar aquellas facturas que sean pagadas o ver que estatus se va a manejar
        // estan descritos en el documento cu100 para las facturas
        // revisar que id_flujo corresponde a los convenios
        SelectQueryUtil queryFacturas = new SelectQueryUtil();
        queryFacturas.select()
                .from("SVT_FACTURA factura")
                .where("factura.CVE_FOLIO = convenio.DES_FOLIO");

        queryConveniosPersona.select("convenio.ID_CONVENIO_PF as idConvenio",
                        "convenio.DES_FOLIO as folioConvenio",
                        recuperarNombrePersona("personaContratante", "nombreContratante"),
                        "convenio.FEC_INICIO as fechaContratacion", // La fecha de inicio sera la fecha de contratacion o sera la fecha de alta
                        "if(convenio.IND_RENOVACION = false, convenio.FEC_INICIO, renovacionConvenio.FEC_INICIO) as fechaVigenciaInicio", // cuando un convenio no tenga renovacion la fecha inicio sera la fecha de inicio, de lo contrario habra que recuperar la fecha de renovacion?
                        "if(convenio.IND_RENOVACION = false, convenio.FEC_VIGENCIA, renovacionConvenio.FEC_VIGENCIA) as fechaVigenciaFin",
                        "(" + queryBeneficiarios.build() + ") as cantidadBeneficiarios",
                        "if(convenio.IND_RENOVACION = false, 'No Renovado', 'Renovado') as situacion",
//                        "exists(" + queryFacturas.build() + ") as factura", // ver que es lo que regresa en la consulta
                        "paquete.MON_PRECIO as importeConvenio",
                        "convenio.ID_ESTATUS_CONVENIO as estatusConvenio")
                .from(SVT_CONVENIO + " convenio")
                .leftJoin("SVT_RENOVACION_CONVENIO_PF renovacionConvenio",
                        "renovacionConvenio.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
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
                        "convenio.FEC_INICIO as fechaContratacion",
                        "if(convenio.IND_RENOVACION = false, convenio.FEC_INICIO, renovacionConvenio.FEC_INICIO) as fechaVigenciaInicio",
                        "if(convenio.IND_RENOVACION = false, convenio.FEC_VIGENCIA, renovacionConvenio.FEC_VIGENCIA) as fechaVigenciaFin",
                        "(" + queryBeneficiarios.build() + ") as cantidadBeneficiarios",
                        "if(convenio.IND_RENOVACION = false, 'No Renovado', 'Renovado') as situacion",
//                        "exists(" + queryFacturas.build() + ") as factura", // ver que es lo que regresa en la consulta
                        "paquete.MON_PRECIO as importeConvenio",
                        "convenio.ID_ESTATUS_CONVENIO as estatusConvenio")
                .from(SVT_CONVENIO + " convenio")
                .leftJoin("SVT_RENOVACION_CONVENIO_PF renovacionConvenio",
                        "renovacionConvenio.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
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
        String encoded = queryConveniosPersona.encrypt(unionPersonaEmpresa);
        return recuperarDatos(request, encoded);
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
                "personaBeneficiario.FEC_NAC as " + ALIAS_FECHA_NACIMIENTO,
                recuperarEdad("personaBeneficiario"),
                "parentesco.DES_PARENTESCO as " + ALIAS_PARENTESCO
        };

        // nuevo plan
        crearSelect(queryBeneficiariosNuevoPlan, columnas);
        queryBeneficiariosNuevoPlan
                .from("SVT_CONTRATANTE_BENEFICIARIOS beneficiario")
                .join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF contratantePaquete",
                        "contratantePaquete.ID_CONTRATANTE_PAQUETE_CONVENIO_PF = beneficiario.ID_CONTRATANTE_PAQUETE_CONVENIO_PF")
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
                .join("SVT_BENEFICIARIOS_DOCUMENTACION_PLAN_ANTERIOR documentacion",
                        "beneficiario.ID_CONTRATANTE_BENEFICIARIOS = documentacion.ID_CONTRATANTE_BENEFICIARIOS")
                .join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF contratantePaquete",
                        "contratantePaquete.ID_CONTRATANTE_PAQUETE_CONVENIO_PF = beneficiario.ID_CONTRATANTE_PAQUETE_CONVENIO_PF")
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
                        "contratantePaquete.ID_CONTRATANTE_PAQUETE_CONVENIO_PF = beneficiario.ID_CONTRATANTE_PAQUETE_CONVENIO_PF")
                .join("SVT_CONVENIO_PF convenio",
                        "convenio.ID_CONVENIO_PF = contratantePaquete.ID_CONVENIO_PF")
                .join("SVC_PERSONA personaBeneficiario",
                        "personaBeneficiario.ID_PERSONA = beneficiario.ID_PERSONA")
                .join("SVC_PARENTESCO parentesco",
                        "parentesco.ID_PARENTESCO = beneficiario.ID_PARENTESCO");
        agregarCondicionBeneficiarios(filtros, queryBeneficiarioPlanAnteriorHijos, true, true);

        final String unionBeneficiarios = queryBeneficiariosPlanAnterior.unionAll(queryBeneficiarioPlanAnteriorHijos);

        final String query = queryBeneficiariosNuevoPlan.build() + " UNION ALL " + unionBeneficiarios;
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
        // los siniestros tendrian que estar pagados o facturados para poder mostrarlos aca
        // si las facturas son globales, tendria que aparecer repetida en varios registros?
        SelectQueryUtil querySiniestros = new SelectQueryUtil();
        // por persona
        final String[] columnas = {
                "velatorio.DES_VELATORIO as nombreVelatorio",
                "ods.FEC_ALTA as fechaSiniestro",
                "ods.CVE_FOLIO as folioSiniestro",
                recuperarNombrePersona("personaFinado", "nombreFinado"),
                "parentesco.DES_PARENTESCO as descripcionParentesco",
                "velatorioOrigen.DES_VELATORIO as velatorioOrigen",
                "presupuesto.CAN_PRESUPUESTO as importe"
        };

        querySiniestros.select(columnas)
                .from("SVC_ORDEN_SERVICIO ods")
                .join("SVC_VELATORIO velatorio",
                        "velatorio.ID_VELATORIO = ods.ID_VELATORIO")
                .join("SVC_FINADO finado",
                        "finado.ID_ORDEN_SERVICIO = ods.ID_ORDEN_SERVICIO")
                .join("SVT_CONVENIO_PF convenio",
                        "convenio.ID_CONVENIO_PF = finado.ID_CONTRATO_PREVISION")
                .join("SVC_VELATORIO velatorioOrigen",
                        "velatorioOrigen.ID_VELATORIO = convenio.ID_VELATORIO")
                .join("SVC_PERSONA personaFinado",
                        "personaFinado.ID_PERSONA = finado.ID_PERSONA")
                .join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF contratanteConvenio",
                        "contratanteConvenio.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
//                .join("SVC_CONTRATANTE contratante",
//                        "contratante.ID_CONTRATANTE = ods.ID_CONTRATANTE",
//                        "contratante.ID_CONTRATANTE = contratanteConvenio.ID_CONTRATANTE")
//                .join("SVC_PERSONA personaContratante",
//                        "personaContratante.ID_PERSONA = contratante.ID_PERSONA")
                .join("SVT_CONTRATANTE_BENEFICIARIOS beneficiario",
                        "beneficiario.ID_CONTRATANTE_PAQUETE_CONVENIO_PF = contratanteConvenio.ID_CONTRATANTE_PAQUETE_CONVENIO_PF",
                        "beneficiario.IND_ACTIVO = true",
                        "beneficiario.ID_PERSONA = finado.ID_PERSONA")
                .join("SVC_PARENTESCO parentesco",
                        "parentesco.ID_PARENTESCO = beneficiario.ID_PARENTESCO")
                .join("SVC_CARACTERISTICAS_PRESUPUESTO presupuesto",
                        "presupuesto.ID_ORDEN_SERVICIO = ods.ID_ORDEN_SERVICIO")
                .where("convenio.DES_FOLIO = :folioConvenio");

        // todo - hacer un union para juntar los registros de la empresa y los de las personas
//                .join("SVT_EMPRESA_CONVENIO_PF empresaContratante");


        crearWhereConFiltros(querySiniestros, filtros, true);
        // query para los convenios de las empresas
        SelectQueryUtil querySiniestrosEmpresa = new SelectQueryUtil();
        // agregar las columnas que estamos buscando arriba
        querySiniestrosEmpresa.select(
                        "velatorio.DES_VELATORIO as nombreVelatorio",
                        "ods.FEC_ALTA as fechaSiniestro",
                        "ods.CVE_FOLIO as folioSiniestro",
                        recuperarNombrePersona("personaFinado", "nombreFinado"),
                        "parentesco.DES_PARENTESCO as descripcionParentesco",
                        "velatorioOrigen.DES_VELATORIO as velatorioOrigen",
                        "presupuesto.CAN_PRESUPUESTO as importe"
                )
                .from("SVC_ORDEN_SERVICIO ods")
                .join("SVC_VELATORIO velatorio",
                        "velatorio.ID_VELATORIO = ods.ID_VELATORIO")
                .join("SVC_FINADO finado",
                        "finado.ID_ORDEN_SERVICIO = ods.ID_ORDEN_SERVICIO")
                .join("SVT_CONVENIO_PF convenio",
                        "convenio.ID_CONVENIO_PF = finado.ID_CONTRATO_PREVISION")
                .join("SVC_VELATORIO velatorioOrigen",
                        "velatorioOrigen.ID_VELATORIO = convenio.ID_VELATORIO")
                .join("SVC_PERSONA personaFinado",
                        "personaFinado.ID_PERSONA = finado.ID_PERSONA")
                .join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF contratanteConvenio",
                        "contratanteConvenio.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
                // cambiar el contratante
                // ver si van a ser los datos del afiliado
                .join("SVT_EMPRESA_CONVENIO_PF empresaContratante",
                        "empresaContratante.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
//                .join("SVC_CONTRATANTE contratante",
//                        "contratante.ID_CONTRATANTE = ods.ID_CONTRATANTE",
//                        "contratante.ID_CONTRATANTE = contratanteConvenio.ID_CONTRATANTE")
//                .join("SVC_PERSONA personaContratante",
//                        "personaContratante.ID_PERSONA = contratante.ID_PERSONA")
                .join("SVT_CONTRATANTE_BENEFICIARIOS beneficiario",
                        "beneficiario.IND_ACTIVO = true",
                        "beneficiario.ID_PERSONA = finado.ID_PERSONA",
                        "beneficiario.ID_CONTRATANTE_PAQUETE_CONVENIO_PF = contratanteConvenio.ID_CONTRATANTE_PAQUETE_CONVENIO_PF")
                .join("SVC_PARENTESCO parentesco",
                        "parentesco.ID_PARENTESCO = beneficiario.ID_PARENTESCO")
                .join("SVC_CARACTERISTICAS_PRESUPUESTO presupuesto",
                        "presupuesto.ID_ORDEN_SERVICIO = ods.ID_ORDEN_SERVICIO");
        crearWhereConFiltros(querySiniestrosEmpresa, filtros, false);

        // todo hay que filtrar los datos porque son opcionales
//        if (filtros.getEstatusConvenio() != null) {
//            querySiniestros.where("convenio.ID_ESTATUS_CONVENIO = :estatusConvenio")
//                    .setParameter("estatusConvenio", 1);
//        }
//        if (filtros.getFolioConvenio() != null) {
//            querySiniestros.where("convenio.DES_FOLIO = :folioConvenio")
//                    .setParameter("folioConvenio", "123321");
//        }
//        if (filtros.getRfc() != null) {
//            final String condicion = "personaContratante.CVE_RFC = " +
//                    filtros.getRfc() +
//                    " or empresa.DES_RFC = " +
//                    filtros.getRfc();
//            querySiniestros.where("(" + condicion + ")");
//        }
//        if (filtros.get)

        final String query = querySiniestros.unionAll(querySiniestrosEmpresa);
        String encoded = querySiniestros.encrypt(query);
//        request.getDatos().put(AppConstantes.QUERY, encoded);
//        request.getDatos().remove(AppConstantes.DATOS);
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
        // la consulta por nombre aplica para buscar solo en la empresa o para buscar un afiliado?

        // los afiliados son aquellas personas que tengan un convenio de tipo empresa
        // los beneficiarios bajo este aspecto, no entran en la jugada
        // el rfc sera el de la empresa?

        // el afiliado es el contratante_convenio_paquete_pf que esta relacionado con n convenio de itpo empresa

        SelectQueryUtil queryAfiliados = new SelectQueryUtil();

        queryAfiliados.select(
                        "velatorio.DES_VELATORIO as nombreVelatorio",
                        recuperarNombrePersona("personaAfiliada", "nombreAfiliado"),
                        "empresaContratante.DES_RFC as rfcTitular",
                        "personaAfiliada.FEC_NAC as " + ALIAS_FECHA_NACIMIENTO,
                        recuperarEdad("personaAfiliada"),
                        "personaAfiliada.NUM_SEXO as genero",
                        "personaAfiliada.DES_CORREO as correo"
                )
                .from("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF contratantePaquete")
                .join("SVT_CONVENIO_PF convenio",
                        "convenio.ID_CONVENIO_PF = contratantePaquete.ID_CONVENIO_PF",
                        "convenio.IND_TIPO_CONTRATACION = false")
                .join("SVC_VELATORIO velatorio",
                        "velatorio.ID_VELATORIO = convenio.ID_VELATORIO")
                .join("SVT_EMPRESA_CONVENIO_PF empresaContratante",
                        "empresaContratante.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
                .join("SVC_CONTRATANTE_CONVENIO_PF_EMPRESA convenioEmpresa",
                        "convenioEmpresa.ID_EMPRESA_CONVENIO_PF = empresaContratante.ID_EMPRESA_CONVENIO_PF")
                .join("SVC_CONTRATANTE contratante",
                        "contratante.ID_CONTRATANTE = contratantePaquete.ID_CONTRATANTE")
                .join("SVC_PERSONA personaAfiliada",
                        "personaAfiliada.ID_PERSONA = contratante.ID_PERSONA")
                .where("convenio.DES_FOLIO = :folioConvenio")
                .setParameter("folioConvenio", filtros.getFolioConvenio());
        if (filtros.getRfc() != null) {
            queryAfiliados.where("personaAfiliada.CVE_RFC = :rfc")
                    .setParameter("rfc", filtros.getRfc());
        }

        final String query = queryAfiliados.build();
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
                        "convenio.FEC_INICIO as fechaInicio",
                        "if(convenio.IND_RENOVACION = false, convenio.FEC_INICIO, renovacionConvenio.FEC_INICIO) as fechaFin", // cuando un convenio no tenga renovacion la fecha inicio sera la fecha de inicio, de lo contrario habra que recuperar la fecha de renovacion?
                        "if(convenio.IND_RENOVACION = false, convenio.FEC_VIGENCIA, renovacionConvenio.FEC_VIGENCIA) as fechaRenovacion" // cuando un convenio no tenga renovacion la fecha inicio sera la fecha de inicio, de lo contrario habra que recuperar la fecha de renovacion?
//                        "as fechaRenovacion"
                )
                .from("SVT_CONVENIO_PF convenio")
                .leftJoin("SVT_RENOVACION_CONVENIO_PF renovacionConvenio",
                        "renovacionConvenio.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
                // joins para la consulta por filtros
                .join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF contratanteConvenio",
                        "contratanteConvenio.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
                .join("SVC_CONTRATANTE contratante",
                        "contratante.ID_CONTRATANTE = contratanteConvenio.ID_CONTRATANTE")
                .join("SVC_PERSONA personaContratante",
                        "personaContratante.id_persona = contratante.id_persona")
                .where("convenio.IND_TIPO_CONTRATACION = true"); // persona -> true
        crearWhereConFiltros(queryVigencias, filtros, true);

        SelectQueryUtil queryVigenciasEmpresa = new SelectQueryUtil();
        queryVigenciasEmpresa.select(
                        "convenio.DES_FOLIO as folioConvenio",
                        "convenio.FEC_INICIO as fechaInicio",
                        "if(convenio.IND_RENOVACION = false, convenio.FEC_INICIO, renovacionConvenio.FEC_INICIO) as fechaFin",
                        "if(convenio.IND_RENOVACION = false, convenio.FEC_VIGENCIA, renovacionConvenio.FEC_VIGENCIA) as fechaRenovacion"
                )
                .from("SVT_CONVENIO_PF convenio")
                .leftJoin("SVT_RENOVACION_CONVENIO_PF renovacionConvenio",
                        "renovacionConvenio.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
                .join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF contratanteConvenio",
                        "contratanteConvenio.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
                .join("SVT_EMPRESA_CONVENIO_PF empresaContratante",
                        "empresaContratante.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
                .where("convenio.IND_TIPO_CONTRATACION = false"); // empresa -> false
        crearWhereConFiltros(queryVigenciasEmpresa, filtros, false);

        final String query = queryVigencias.unionAll(queryVigenciasEmpresa);
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
        // consultar los pagos
        // ver como relaciono los filtros para que se pueda implementar en esta parte
        SelectQueryUtil queryFacturas = new SelectQueryUtil();

        queryFacturas.select(
                        "factura.CVE_FOLIO as numeroFactura",
                        "factura.UUID as UUID", // cambiar por el nombre que va a tener en la base de datos
                        "factura.FEC_ALTA as fecha", // cambiar por la fecha que se estaria registrando
                        "personaContratante.DES_RFC as rfc",
                        recuperarNombrePersona("personaContratante", "cliente"),
                        "factura.CAN_TOTAL as total",
                        "factura.ID_ESTATUS_FACTURA as estatusFactura"
                )

                .from("SVT_FACTURA factura")
                .join("SVT_BITACORA_PAGO pago",
                        "pago.ID_PAGO_BITACORA = factura.ID_PAGO_BITACORA")
                .join(SVT_CONVENIO,
                        SVT_CONVENIO + ".CVE_FOLIO = factura.CVE_FOLIO")
                .join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF contratanteConvenio",
                        "contratanteConvenio.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")

                .join("SVC_CONTRATANTE contratante",
                        "contratante.ID_CONTRATANTE = contratanteConvenio.ID_CONTRATANTE")
                .join("SVC_PERSONA personaContratante",
                        "personaContratante.id_persona = contratante.id_persona")

                .where("convenio.IND_TIPO_CONTRATACION = true"); // empresa -> false

        crearWhereConFiltros(queryFacturas, filtros, true);

        SelectQueryUtil queryFacturasEmpresa = new SelectQueryUtil();
        queryFacturasEmpresa.select(
                        "factura.CVE_FOLIO as numeroFactura",
                        "factura.UUID as UUID", // que significa este campo - va a estar en la tabla factura
                        "factura.FEC_ALTA as fecha", // ver que fecha es la que se va a regresar
                        "empresaContratante.DES_RFC as rfc",
                        "empresaContratante.DES_NOM as cliente",
                        "factura.CAN_TOTAL as total",
                        "factura.ID_ESTATUS_FACTURA as estatusFactura"
                )
                .from("SVT_FACTURA factura")
                .join("SVT_BITACORA_PAGO pago",
                        "pago.ID_PAGO_BITACORA = factura.ID_PAGO_BITACORA")
                .join(SVT_CONVENIO,
                        SVT_CONVENIO + ".CVE_FOLIO = factura.CVE_FOLIO")

                // todo - esto se puede mover a crearWhere...
                .join("SVT_EMPRESA_CONVENIO_PF empresaContratante",
                        "empresaContratante.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
                .where("convenio.IND_TIPO_CONTRATACION = false"); // empresa -> false
        crearWhereConFiltros(queryFacturasEmpresa, filtros, true);

        final String query = queryFacturas.unionAll(queryFacturasEmpresa);
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
        if (planAnterior) {
            if (esHijo) {
                queryUtil.where("(IF(beneficiario.ID_PARENTESCO = " + PARENTESCO_HIJO + " AND TIMESTAMPDIFF(YEAR, personaBeneficiario.FEC_NAC, CURDATE()) < 18, beneficiario.ID_PARENTESCO, NULL))")
                        .or("beneficiario.ID_PARENTESCO = :idParentesco")
                        .and("documentacion.IND_COMPROBANTE_ESTUDIOS = 1")
                        .and("TIMESTAMPDIFF(YEAR, personaBeneficiario.FEC_NAC, CURDATE()) BETWEEN 18 AND 25")
                        .setParameter("idParentesco", PARENTESCO_HIJO);
            } else {
                queryUtil.where("beneficiario.ID_PARENTESCO != 4");

            }
            queryUtil.where("convenio.ID_TIPO_PREVISION = 2");
        }

        queryUtil.where("convenio.DES_FOLIO = :folioConvenio",
                        "beneficiario.IND_ACTIVO = true",
                        "beneficiario.IND_SINIESTROS = 0")
                .setParameter("folioConvenio", filtros.getFolioConvenio());
        if (filtros.getNombreBeneficiario() != null) {
            queryUtil.where("personaBeneficiario.NOM_PERSONA = :nombreBeneficiario")
                    .setParameter("nombreBeneficiario", filtros.getNombreBeneficiario());
        }
    }

    private void crearWhereSiniestros(SelectQueryUtil queryUtil, ConsultaGeneralRequest filtros, boolean isPersona) {
        if (isPersona) {

        }
    }

    /**
     * todo - add documentation
     *
     * @param selectQuery
     * @param filtros
     * @param isPersona
     */
    private void crearWhereConFiltros(SelectQueryUtil selectQuery, ConsultaGeneralRequest filtros, boolean isPersona) {
        if (isPersona) {
            if (filtros.getRfc() != null) {
                // todo - aca se podrian agregar los joins para las consultas y dejarlas mas limpias
                selectQuery.where("personaContratante.CVE_RFC = :rfc")
                        .setParameter("rfc", filtros.getRfc());
            }
            if (filtros.getCurp() != null) {
                selectQuery.where("personaContratante.CVE_CURP = :curp")
                        .setParameter("curp", filtros.getCurp());
            }
            if (filtros.getNombre() != null) {
                // la consulta se hace sobre el nombre completo, ver si se necesitan coincidencias
                selectQuery.where(recuperarNombrePersona("personaContratante") + " = :nombre")
                        .setParameter("nombre", filtros.getNombre());
            }
        } else {
            if (filtros.getRfc() != null) {
                selectQuery.where("empresaContratante.DES_RFC = :rfc")
                        .setParameter("rfc", filtros.getRfc());
            }
            if (filtros.getNombre() != null) {
                selectQuery.where("empresaContratante.DES_NOMBRE = :nombreEmpresa")
                        .setParameter("nombreEmpresa", filtros.getNombre());
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
    }

    /**
     * todo - add documentation
     *
     * @param aliasTabla
     * @return
     */
    private static String recuperarEdad(String aliasTabla) {
        return "TIMESTAMPDIFF(YEAR, " + aliasTabla + ".FEC_NAC, CURDATE()) as " + ALIAS_EDAD;
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
     * todo - add documentation
     *
     * @param filtros
     * @return
     */
    public Map<String, Object> recuperarDatosFormatoTabla(DatosReporteRequest filtros) {
        Map<String, Object> parametros = new HashMap<>();

        parametros.put("folioConvenio", filtros.getFolioConvenio());
        parametros.put("nombre", filtros.getNombre());
        parametros.put("curp", filtros.getCurp());
        parametros.put("rfc", filtros.getRfc());
        parametros.put("estatusConvenio", filtros.getEstatusConvenio());

        parametros.put("rutaNombreReporte", filtros.getRuta());
        parametros.put("tipoReporte", filtros.getTipoReporte());
        return parametros;
    }

    /**
     * todo - add documentation
     *
     * @param queryUtil
     * @param columnas
     */
    private void crearSelect(SelectQueryUtil queryUtil, String... columnas) {
        queryUtil.select(columnas
//                recuperarNombrePersona("personaBeneficiario", ALIAS_NOMBRE_BENEFICIARIO),
//                "personaBeneficiario.FEC_NAC as " + ALIAS_FECHA_NACIMIENTO,
//                recuperarEdad("personaBeneficiario"),
//                "parentesco.DES_PARENTESCO as " + ALIAS_PARENTESCO
        );
    }
}
