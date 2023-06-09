package com.imss.sivimss.planfunerario.beans;

import com.imss.sivimss.planfunerario.model.request.ConsultaGeneralRequest;
import com.imss.sivimss.planfunerario.util.AppConstantes;
import com.imss.sivimss.planfunerario.util.DatosRequest;
import com.imss.sivimss.planfunerario.util.SelectQueryUtil;
import org.apache.commons.lang3.NotImplementedException;
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

    // todo - imprimir los campos de las tablas involucradas
    //      - Convenios: son con el contratante o con la empresa
    //          - hay que recuperar convenios por empresa y luego por persona, sino no vamos a poder armar la tabla
    //      - Afiliados: Los afiliados con las personas que esten relacionadas con un convenio_paquete
    //      - Beneficiarios
    //      - Siniestros
    //      - Vigencias
    //      - Facturas

    /**
     * Recupera la lista de convenios del sistema.
     *
     * @return
     */
    public DatosRequest consultarConvenios(DatosRequest request, ConsultaGeneralRequest filtros) throws UnsupportedEncodingException {
        // todo - regresar los convenios de acuerdo al estatus que se seleccione
        SelectQueryUtil queryConveniosPersona = new SelectQueryUtil();
        SelectQueryUtil queryBeneficiarios = new SelectQueryUtil();
        // convenios existen por empresa y por
        // todo - validar los filtros para la consulta de beneficiarios
        queryBeneficiarios.select("count(*)")
                .from("SVT_CONTRATANTE_BENEFICIARIOS beneficiarios")
                .where("beneficiarios.ID_CONTRATANTE_PAQUETE_CONVENIO_PF = contratanteConvenio.ID_CONTRATANTE_PAQUETE_CONVENIO_PF");

        SelectQueryUtil queryFacturas = new SelectQueryUtil();
        // consultar aquellas facturas que sean pagadas o ver que estatus se va a manejar
        // estan descritos en el documento cu100 para las facturas
        // revisar que flujo corresponde a los convenios
        queryFacturas.select()
                .from("SVT_FACTURA factura")
                .where("factura.id_proceso = convenio.CVE_FOLIO",
                        "factura.id_flujo = 'convenio'");

        // de donde se va a recuperar el importe del convenio, no existe un campo para la captura de dicha informacion
        // o se va a recupear del paquete
        queryConveniosPersona.select("convenio.ID_CONVENIO_PF as idConvenio",
                        "convenio.DES_FOLIO as folioConvenio",
                        recuperarNombrePersona("personaContratante", "nombreContratante"),
                        "convenio.FEC_INICIO as fechaContratacion", // La fecha de inicio sera la fecha de contratacion o sera la fecha de alta
                        // ver que regreso si no tiene renovacion ni nada
                        // hacer un query aparte para revisar eso
                        "if(convenio.IND_RENOVACION = false, convenio.FEC_INICIO, renovacionConvenio.FEC_INICIO) as fechaVigenciaInicio", // cuando un convenio no tenga renovacion la fecha inicio sera la fecha de inicio, de lo contrario habra que recuperar la fecha de renovacion?
                        "if(convenio.IND_RENOVACION = false, convenio.FEC_VIGENCIA, renovacionConvenio.FEC_VIGENCIA) as fechaVigenciaFin", // sacar la fecha de vigencia de la tabla de Lore
                        "(" + queryBeneficiarios.build() + ") as cantidadBeneficiarios",
//                        "as situacion", // de donde se recupera la situacion
//                        "exists(" + queryFacturas.build() + ") as factura", // ver que es lo que regresa en la consulta
//                        "presupuesto.CAN_PRESUPUESTO as importeConvenio", // revisar con pablo para la parte de los importes, vienen de caracteristicas_presupuesto
                        "convenio.ID_ESTATUS_CONVENIO as estatusConvenio")
                .from(SVT_CONVENIO + " convenio")
//                .from(SVT_CONVENIO + " convenio", "SVT_RENOVACION_CONVENIO_PF renovacionConvenio")
                .leftJoin("SVT_RENOVACION_CONVENIO_PF renovacionConvenio",
                        "renovacionConvenio.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
                .join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF contratanteConvenio",
                        "contratanteConvenio.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
                .join("SVC_CONTRATANTE contratante",
                        "contratante.ID_CONTRATANTE = contratanteConvenio.ID_CONTRATANTE")
                .join("SVC_PERSONA personaContratante",
                        "personaContratante.id_persona = contratante.id_persona")
                // cambiar por el paquete y recuperar ese monto, preguntar
//                .join("SVC_ORDEN_SERVICIO ods",
//                        "ods.ID_CONTRATANTE = contratante.ID_CONTRATANTE")
//                .join("SVC_CARACTERISTICAS_PRESUPUESTO presupuesto",
//                        "presupuesto.ID_ORDEN_SERVICIO = ods.ID_ORDEN_SERVICIO")
                .where("convenio.IND_TIPO_CONTRATACION = true"); // persona -> true
        crearWhereConFiltros(queryConveniosPersona, filtros, true);

        SelectQueryUtil queryConveniosEmpresa = new SelectQueryUtil();

        queryConveniosEmpresa.select(
                        "convenio.ID_CONVENIO_PF as idConvenio",
                        "convenio.DES_FOLIO as folioConvenio",
                        // cambiar el nombre, tiene que ser el de la empresa
//                        recuperarNombrePersona("personaContratante", "nombreContratante"),
                        "empresaContratante.DES_NOMBRE as nombreContratante",
                        "convenio.FEC_INICIO as fechaContratacion",
                        "if(convenio.IND_RENOVACION = false, convenio.FEC_INICIO, renovacionConvenio.FEC_INICIO) as fechaVigenciaInicio", // cuando un convenio no tenga renovacion la fecha inicio sera la fecha de inicio, de lo contrario habra que recuperar la fecha de renovacion?
                        "if(convenio.IND_RENOVACION = false, convenio.FEC_VIGENCIA, renovacionConvenio.FEC_VIGENCIA) as fechaVigenciaFin", // sacar la fecha de vigencia de la tabla de Lore
                        "(" + queryBeneficiarios.build() + ") as cantidadBeneficiarios",
//                        "as situacion", // de donde se recupera la situacion
//                        "exists(" + queryFacturas.build() + ") as factura", // ver que es lo que regresa en la consulta
//                        "presupuesto.CAN_PRESUPUESTO as importeConvenio", // revisar con pablo para la parte de los importes, vienen de caracteristicas_presupuesto
                        "convenio.ID_ESTATUS_CONVENIO as estatusConvenio")
                .from(SVT_CONVENIO + " convenio")
//                .from(SVT_CONVENIO + " convenio", "SVT_RENOVACION_CONVENIO_PF renovacionConvenio")
                .leftJoin("SVT_RENOVACION_CONVENIO_PF renovacionConvenio",
                        "renovacionConvenio.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
                .join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF contratanteConvenio",
                        "contratanteConvenio.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
                .join("SVT_EMPRESA_CONVENIO_PF empresaContratante",
                        "empresaContratante.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
                .where("convenio.IND_TIPO_CONTRATACION = false"); // empresa -> false
        crearWhereConFiltros(queryConveniosEmpresa, filtros, false);

        String unionPersonaEmpresa = queryConveniosPersona.unionAll(queryConveniosEmpresa);
        String encoded = queryConveniosPersona.encrypt(unionPersonaEmpresa);
//        DatosRequest datos = recuperarDatos(request, encoded);
//        datos.getDatos().remove(AppConstantes.DATOS);
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
        SelectQueryUtil queryBeneficiarios = new SelectQueryUtil();
        // todo - recuperar beneficiario que tengan un convenio pagado (ver los estatus)
        //      - la consulta puede ser por los filtros asi que hay que relacionar el:
        //      - rfc
        //      - curp
        //      - folioConvenio
        //      - estatusConvenio

        // Replicar la consulta para empresas
        final String aliasNombreBeneficiario = "nombreBeneficiario";
        // validar que el beneficiario tenga menos de 18
        // ver como se va a hacer la parte de la validacion, porque no entiendo bien,
        // ver la parte de la visualizacion del detalle, porque no esta contemplada

        queryBeneficiarios.select(
                        recuperarNombrePersona("personaBeneficiario", aliasNombreBeneficiario),
                        "personaBeneficiario.FEC_NAC as " + ALIAS_FECHA_NACIMIENTO,
                        recuperarEdad("personaBeneficiario"),
                        "parentesco.DES_PARENTESCO as " + ALIAS_PARENTESCO
                )
                .from("SVT_CONTRATANTE_BENEFICIARIOS beneficiario")
                .join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF contratantePaquete",
                        "contratantePaquete.ID_CONTRATANTE_PAQUETE_CONVENIO_PF = beneficiario.ID_CONTRATANTE_PAQUETE_CONVENIO_PF")
                .join("SVT_CONVENIO_PF convenio",
                        "convenio.ID_CONVENIO_PF = contratantePaquete.ID_CONVENIO_PF")
                .join("SVC_PERSONA personaBeneficiario",
                        "personaBeneficiario.ID_PERSONA = beneficiario.ID_PERSONA")
                .join("SVC_PARENTESCO parentesco",
                        "parentesco.ID_PARENTESCO = beneficiario.ID_PARENTESCO");
        // el filtro de la tabla es el nombre de la persona
        if (filtros.getNombreBeneficiario() != null) {
            queryBeneficiarios.where("personaBeneficiario.nom_persona = :nombreBeneficiario")
                    .setParameter("nombreBeneficiario", filtros.getNombreBeneficiario());
        }
        crearWhereConFiltros(queryBeneficiarios, filtros, true);

        SelectQueryUtil queryBeneficiariosEmpresa = new SelectQueryUtil();
        queryBeneficiariosEmpresa.select(
                        recuperarNombrePersona("personaBeneficiario", aliasNombreBeneficiario),
                        "personaBeneficiario.FEC_NAC as " + ALIAS_FECHA_NACIMIENTO,
                        recuperarEdad("personaBeneficiario"),
                        "parentesco.DES_PARENTESCO as " + ALIAS_PARENTESCO
                )
                .from("SVT_CONTRATANTE_BENEFICIARIOS beneficiario")
                .join("SVT_CONTRATANTE_PAQUETE_CONVENIO_PF contratantePaquete",
                        "contratantePaquete.ID_CONTRATANTE_PAQUETE_CONVENIO_PF = beneficiario.ID_CONTRATANTE_PAQUETE_CONVENIO_PF")
                .join("SVT_CONVENIO_PF convenio",
                        "convenio.ID_CONVENIO_PF = contratantePaquete.ID_CONVENIO_PF")
                .join("SVT_EMPRESA_CONVENIO_PF empresaContratante",
                        "empresaContratante.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF",
                        "empresaContratante.ID_CONVENIO_PF = contratantePaquete.ID_CONVENIO_PF")
                .join("SVC_PERSONA personaBeneficiario",
                        "personaBeneficiario.ID_PERSONA = beneficiario.ID_PERSONA")
                .join("SVC_PARENTESCO parentesco",
                        "parentesco.ID_PARENTESCO = beneficiario.ID_PARENTESCO");
        // agregar el nombre del filtro de la tabla
        if (filtros.getNombreBeneficiario() != null) {
            queryBeneficiariosEmpresa.where("personaBeneficiario.nom_persona = :nombreBeneficiario")
                    .setParameter("nombreBeneficiario", filtros.getNombreBeneficiario());
        }
        crearWhereConFiltros(queryBeneficiariosEmpresa, filtros, false);

        final String unionBeneficiarios = queryBeneficiarios.unionAll(queryBeneficiariosEmpresa);

        // todo - agregar lo del utf-8 a los otros servicios
        String encoded = queryBeneficiarios.encrypt(unionBeneficiarios);
//        request.getDatos().put(AppConstantes.QUERY, encoded);
//        request.getDatos().remove(AppConstantes.DATOS);
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
                        "presupuesto.ID_ORDEN_SERVICIO = ods.ID_ORDEN_SERVICIO");
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
//                "as nombreAfiliado",
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
                        "convenio.IND_TIPO_CONTRATACION = true") // ver si el true es para empresa
                .join("SVC_VELATORIO velatorio",
                        "velatorio.ID_VELATORIO = convenio.ID_VELATORIO")
                .join("SVT_EMPRESA_CONVENIO_PF empresaContratante",
                        "empresaContratante.ID_CONVENIO_PF = convenio.ID_CONVENIO_PF")
                .join("SVC_CONTRATANTE_CONVENIO_PF_EMPRESA convenioEmpresa",
                        "convenioEmpresa.ID_EMPRESA_CONVENIO_PF = empresaContratante.ID_EMPRESA_CONVENIO_PF")
                .join("SVC_CONTRATANTE contratante",
                        "contratante.ID_CONTRATANTE = contratantePaquete.ID_CONTRATANTE")
                .join("SVC_PERSONA personaAfiliada",
                        "personaAfiliada.ID_PERSONA = contratante.ID_PERSONA");
        crearWhereConFiltros(queryAfiliados, filtros, false);

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
        String encoded = queryFacturas.encrypt(queryFacturas.build());

        return recuperarDatos(request, encoded);
    }

    /**
     * Recupera el nombre de la tabla persona.
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

    private static String recuperarNombrePersona(String aliasTabla) {
        return "concat(" +
                aliasTabla + "." + "NOM_PERSONA" + ", " +
                "' ', " +
                aliasTabla + "." + "NOM_PRIMER_APELLIDO" + ", " +
                "' ', " +
                aliasTabla + "." + "NOM_SEGUNDO_APELLIDO" + ") ";
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
}
