package com.imss.sivimss.planfunerario.service;

import com.imss.sivimss.planfunerario.util.DatosRequest;
import com.imss.sivimss.planfunerario.util.Response;
import org.springframework.security.core.Authentication;

public interface ConsultaConveniosService {
    Response<?> consultarTodo(DatosRequest request, Authentication authentication);
    Response<?> consultarConvenios(DatosRequest request, Authentication authentication);
    Response<?> consultarBeneficiarios(DatosRequest request, Authentication authentication);
    Response<?> consultarAfiliado(DatosRequest request, Authentication authentication);
    Response<?> consultarSiniestros(DatosRequest request, Authentication authentication);
    Response<?> consultarVigencias(DatosRequest request, Authentication authentication);
    Response<?> consultarFacturas(DatosRequest request, Authentication authentication);
    Response<?> generarReporteTabla(DatosRequest request, Authentication authentication);
}
