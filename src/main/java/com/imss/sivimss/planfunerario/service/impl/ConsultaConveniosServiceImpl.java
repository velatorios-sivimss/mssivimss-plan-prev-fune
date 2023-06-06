package com.imss.sivimss.planfunerario.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.imss.sivimss.planfunerario.beans.ConsultaConvenios;
import com.imss.sivimss.planfunerario.model.request.ConsultaGeneralRequest;
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
    @Value("${endpoints.rutas.dominio-reportes-generales}")
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
            consultas.put("facturas", consultaConvenios.consultarFacturas(request, filtros));

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
        }
        return null;
    }

    @Override
    public Response<?> consultarBeneficiarios(DatosRequest request, Authentication authentication) {
        return null;
    }

    @Override
    public Response<?> consultarAfiliado(DatosRequest request, Authentication authentication) {
        return null;
    }

    @Override
    public Response<?> consultarSiniestros(DatosRequest request, Authentication authentication) {
        return null;
    }

    @Override
    public Response<?> consultarVigencias(DatosRequest request, Authentication authentication) {
        return null;
    }

    @Override
    public Response<?> consultarFacturas(DatosRequest request, Authentication authentication) {
        return null;
    }

    @Override
    public Response<?> generarReporteTabla(DatosRequest request, Authentication authentication) {
        return null;
    }

    /**
     * Manda las consultas a la siguiente capa de los servicios para ejecutar el query.
     * todo - procesar consultas deberia regresar una lista con cada una de las respuestas
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
//                        entry -> recuperarRespuestas(entry.getValue(), authentication)
                ));

        // hacer las peticiones por cada consulta
        // y recuperar cada respuesta e irla colocando en un map o un arreglo las peticiones
//        final Response<?> response = restTemplate.consumirServicio(
//                new DatosRequest(),
//                urlDominioConsulta,
//                authentication
//
//        );
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