package com.imss.sivimss.planfunerario.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.imss.sivimss.planfunerario.beans.ConsultaConvenios;
import com.imss.sivimss.planfunerario.model.request.ConsultaGeneralRequest;
import com.imss.sivimss.planfunerario.model.request.DatosReporteRequest;
import com.imss.sivimss.planfunerario.service.ConsultaConveniosService;
import com.imss.sivimss.planfunerario.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ConsultaConveniosServiceImpl implements ConsultaConveniosService {
    // todo - cambiar los estatus por los adecuados

    private static final int ESTATUS_ELIMINADO = 0;

    private static final int ESTATUS_ENTREGADO = 2;
    private static final String MSG_ERROR_REGISTRAR = "5";
    private static final String MSG023_GUARDAR_OK = "23";
    //    MSG131	Se ha registrado correctamente el registro de salida del equipo de velación.
    private static final String MSG131_REGISTRO_SALIDA_OK = "131";
    //    MSG133	Se ha registrado correctamente el registro de entrada del equipo de velación.
    private static final String MSG133_REGISTRO_ENTRADA_OK = "133";

    // endpoints
    @Value("${endpoints.rutas.dominio-consulta}")
    private String urlDominioConsulta;
    @Value("${endpoints.rutas.dominio-consulta-paginado}")
    private String urlDominioConsultaPaginado;
    @Value("${endpoints.ms-reportes}")
    private String urlReportes;

    private final ProviderServiceRestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final Gson gson;
    private final ConsultaConvenios consultaConvenios;

    public ConsultaConveniosServiceImpl(ProviderServiceRestTemplate restTemplate,
                                        ObjectMapper mapper, ConsultaConvenios consultaConvenios) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.consultaConvenios = consultaConvenios;
        this.gson = new Gson();
    }

    @Override
    public Response<?> consultarTodo(DatosRequest request, Authentication authentication) {
        try {
            // recuperar los datos del request
            ConsultaGeneralRequest filtros = gson.fromJson(
                    String.valueOf(request.getDatos().get(AppConstantes.DATOS)),
                    ConsultaGeneralRequest.class
            );
            // hacer una lista con los queries
            Map<String, DatosRequest> consultas = new HashMap<>();
            consultas.put("convenios", consultaConvenios.consultarConvenios(request, filtros));
            consultas.put("afiliados", consultaConvenios.consultarAfiliados(request, filtros));
            consultas.put("beneficiarios", consultaConvenios.consultarBeneficiarios(request, filtros));
            consultas.put("siniestros", consultaConvenios.consultarSiniestros(request, filtros));
            consultas.put("vigencias", consultaConvenios.consultarVigencias(request, filtros));
            // armar la consulta para las facturas
            // hay que armar la tabla con lo que vimos que puede ser de utilidad
//            consultas.put("facturas", consultaConvenios.consultarFacturas(request, filtros));

//            procesarConsultas(consultas, authentication);
//            for (Map.Entry<String, DatosRequest> consulta : consultas.entrySet()) {
//            }
            final Response<Map<String, Response<?>>> respuesta = new Response<>();
            respuesta.setCodigo(HttpStatus.OK.value());
            respuesta.setError(false);
            respuesta.setMensaje("OK");
            respuesta.setDatos(procesarConsultas(consultas, authentication));

            return MensajeResponseUtil.mensajeResponse(respuesta, "OK");
        } catch (UnsupportedEncodingException e) {
            log.error("Error al encriptar la consulta");
        } catch (IOException exception) {
            log.error("Error al realizar las consultas para convenios");
        } catch (Exception exception) {
            log.error("Ha ocurrido un error al consultar los registros");
        }
        return null;
    }

    @Override
    public Response<?> consultarConvenios(DatosRequest request, Authentication authentication) {
        try {
            // recuperar los datos del request
            ConsultaGeneralRequest filtros = gson.fromJson(
                    String.valueOf(request.getDatos().get(AppConstantes.DATOS)),
                    ConsultaGeneralRequest.class
            );
            final DatosRequest datosRequest = consultaConvenios.consultarConvenios(request, filtros);

            return enviarPeticion(datosRequest, authentication);
        } catch (UnsupportedEncodingException e) {
            log.error("Error al encriptar la consulta");
        } catch (IOException exception) {
            log.error("Error al realizar las consultas para convenios");
        } catch (Exception exception) {
            log.error("Ha ocurrido un error al consultar los registros");
            // todo mandar el mensajeResponse con el codigo correspondiente
        }
        return null;
    }

    @Override
    public Response<?> consultarBeneficiarios(DatosRequest request, Authentication authentication) {
        try {
            ConsultaGeneralRequest filtros = gson.fromJson(
                    String.valueOf(request.getDatos().get(AppConstantes.DATOS)),
                    ConsultaGeneralRequest.class
            );
            final DatosRequest datosRequest = consultaConvenios.consultarBeneficiarios(request, filtros);

            return enviarPeticion(datosRequest, authentication);
        } catch (UnsupportedEncodingException e) {
            log.error("Error al encriptar la consulta");
        } catch (IOException exception) {
            log.error("Error al realizar las consultas para convenios");
        } catch (Exception exception) {
            log.error("Ha ocurrido un error al consultar los registros");
            // todo - mandar el mensajeResponse con el codigo correspondiente
        }
        return null;
    }

    @Override
    public Response<?> consultarAfiliados(DatosRequest request, Authentication authentication) {
        try {
            ConsultaGeneralRequest filtros = gson.fromJson(
                    String.valueOf(request.getDatos().get(AppConstantes.DATOS)),
                    ConsultaGeneralRequest.class
            );
            final DatosRequest datosRequest = consultaConvenios.consultarAfiliados(request, filtros);

            return enviarPeticion(datosRequest, authentication);
        } catch (UnsupportedEncodingException e) {
            log.error("Error al encriptar la consulta");
        } catch (IOException exception) {
            log.error("Error al realizar las consultas para convenios");
        } catch (Exception exception) {
            log.error("Ha ocurrido un error al consultar los registros");
            // todo - mandar el mensajeResponse con el codigo correspondiente
        }
        return null;
    }

    @Override
    public Response<?> consultarSiniestros(DatosRequest request, Authentication authentication) {
        try {
            ConsultaGeneralRequest filtros = gson.fromJson(
                    String.valueOf(request.getDatos().get(AppConstantes.DATOS)),
                    ConsultaGeneralRequest.class
            );
            final DatosRequest datosRequest = consultaConvenios.consultarSiniestros(request, filtros);

            return enviarPeticion(datosRequest, authentication);
        } catch (UnsupportedEncodingException e) {
            log.error("Error al encriptar la consulta");
        } catch (IOException exception) {
            log.error("Error al realizar las consultas para convenios");
        } catch (Exception exception) {
            log.error("Ha ocurrido un error al consultar los registros");
            // todo - mandar el mensajeResponse con el codigo correspondiente
        }
        return null;
    }

    @Override
    public Response<?> consultarVigencias(DatosRequest request, Authentication authentication) {
        try {
            ConsultaGeneralRequest filtros = gson.fromJson(
                    String.valueOf(request.getDatos().get(AppConstantes.DATOS)),
                    ConsultaGeneralRequest.class
            );
            final DatosRequest datosRequest = consultaConvenios.consultarVigencias(request, filtros);

            return enviarPeticion(datosRequest, authentication);
        } catch (UnsupportedEncodingException e) {
            log.error("Error al encriptar la consulta");
        } catch (IOException exception) {
            log.error("Error al realizar las consultas para convenios");
        } catch (Exception exception) {
            log.error("Ha ocurrido un error al consultar los registros");
            // todo - mandar el mensajeResponse con el codigo correspondiente
        }
        return null;
    }

    @Override
    public Response<?> consultarFacturas(DatosRequest request, Authentication authentication) {
        try {
            ConsultaGeneralRequest filtros = gson.fromJson(
                    String.valueOf(request.getDatos().get(AppConstantes.DATOS)),
                    ConsultaGeneralRequest.class
            );
            final DatosRequest datosRequest = consultaConvenios.consultarFacturas(request, filtros);
            // todo - validar la respuesta y eso

            return enviarPeticion(datosRequest, authentication);
        } catch (UnsupportedEncodingException e) {
            // todo - crear una funciona para generar el response cuando haya una excepcion
            log.error("Error al encriptar la consulta.");
            log.error(e.getMessage());
//            final Response<?> response = getErrorResponse();
            return MensajeResponseUtil.mensajeResponse(getErrorResponse(), "Error al procesar la consulta");
        } catch (IOException exception) {
            log.error("Error al realizar las consultas para convenios");
            return MensajeResponseUtil.mensajeResponse(getErrorResponse(), "Error al realizar la peticion");
        } catch (Exception exception) {
            log.error("Ha ocurrido un error al consultar los registros");
            // todo - mandar el mensajeResponse con el codigo correspondiente
            return MensajeResponseUtil.mensajeResponse(getErrorResponse(), "Error al realizar la peticion");
        }
//        return null;
    }

    private static Response<?> getErrorResponse() {
        final Response<?> response = new Response<>();
        response.setError(true);
        response.setCodigo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return response;
    }

    @Override
    public Response<?> generarReporteTabla(DatosRequest request, Authentication authentication) {
        try {

            DatosReporteRequest filtros = gson.fromJson(
                    String.valueOf(request.getDatos().get(AppConstantes.DATOS)),
                    DatosReporteRequest.class
            );
            Map<String, Object> parametrosReporte = consultaConvenios.recuperarDatosFormatoTabla(filtros);
            return restTemplate.consumirServicioReportes(
                    parametrosReporte,
//                    filtros.getRuta(),
//                    filtros.getTipoReporte(),
                    urlReportes,
                    authentication
            );
        } catch (Exception ex) {
            log.error("Error al crear el reporte", ex);
            Response<?> respuesta = new Response<>();
            respuesta.setCodigo(HttpStatus.INTERNAL_SERVER_ERROR.value());
            respuesta.setMensaje("");
            respuesta.setError(true);
            return MensajeResponseUtil.mensajeResponse(respuesta, "Error al generar el reporte");
        }
    }

    /**
     * Manda las consultas a la siguiente capa de los servicios para ejecutar el query.
     *
     * @param consultas
     * @param authentication
     * @throws IOException
     */
    private Map<String, Response<?>> procesarConsultas(Map<String, DatosRequest> consultas, Authentication authentication) throws IOException {

        Map<String, Response<?>> respuestas = consultas.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            Response<?> response = new Response<>();
                            try {
                                response = enviarPeticion(entry.getValue(), authentication);
                                return response;
                            } catch (IOException e) {
                                // todo - hacer un objeto con la respuesta correcta
                                MensajeResponseUtil.mensajeResponse(new Response(), "Error");
                            }
                            return response;
                        }
                ));

        validarRespuesta((HashMap<String, Response<?>>) respuestas);

        return respuestas;

    }

//    private Function<Map.Entry<String, DatosRequest>, HashMap<String, Response<?>>> recuperarRespuestas(
//            Map<String, Response<?>> mapRespuestas,
//            Authentication authentication) {
//        return (Map.Entry<String, DatosRequest> entry) -> {
//            // regresar un map con los response de cada uno de los request
////                    enviarPeticion(entry.getValue(), authentication);
//
//            try {
//                mapRespuestas.put(entry.getKey(), enviarPeticion(entry.getValue(), authentication));
//            } catch (IOException e) {
//                // todo - arrojar el error de forma adecuada
//                log.error("Error al realizar la consulta");
////                throw new RuntimeException(e);
//                mapRespuestas.put(entry.getKey(), MensajeResponseUtil.mensajeResponse(new Response(), "Error"));
//            }
//            return mapRespuestas;
//        };
//    }

    private Response<?> enviarPeticion(DatosRequest datos, Authentication authentication) throws IOException {

        return restTemplate.consumirServicio(
                datos.getDatos(),
                urlDominioConsultaPaginado,
                authentication

        );
    }

    private void validarRespuesta(HashMap<String, Response<?>> respuestas) {
        System.out.println(respuestas);
    }
}