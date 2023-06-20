package com.imss.sivimss.planfunerario.service;

import com.imss.sivimss.planfunerario.util.DatosRequest;
import com.imss.sivimss.planfunerario.util.Response;
import org.springframework.security.core.Authentication;

import java.io.IOException;

public interface ContratarPlanPFService {
    Response<?> agregarConvenioNuevoPF(DatosRequest request, Authentication authentication) throws IOException;
    Response<?> agregarConvenioNuevoPFEmpresa(DatosRequest request, Authentication authentication) throws IOException;
    Response<?> consultaPromotores(DatosRequest request, Authentication authentication) throws IOException;
    Response<?> validaCurpRfc(DatosRequest request, Authentication authentication) throws IOException;
    Response<?> consultaCP(DatosRequest request, Authentication authentication) throws IOException;
    Response<?> generarPDF(DatosRequest request, Authentication authentication) throws IOException;
    Response<?> busquedaFolioPersona(DatosRequest request, Authentication authentication) throws IOException;
    Response<?> busquedaFolioEmpresa(DatosRequest request, Authentication authentication) throws IOException;
    Response<?> busquedaRfcEmpresa(DatosRequest request, Authentication authentication) throws IOException;
    Response<?> activarDesactivarConvenio(DatosRequest request, Authentication authentication) throws IOException;

}
