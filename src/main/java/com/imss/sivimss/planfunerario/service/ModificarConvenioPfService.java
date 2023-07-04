package com.imss.sivimss.planfunerario.service;

import com.imss.sivimss.planfunerario.util.DatosRequest;
import com.imss.sivimss.planfunerario.util.Response;
import org.springframework.security.core.Authentication;

import java.io.IOException;

public interface ModificarConvenioPfService {
    Response<?> modificarConvenioPersona(DatosRequest request, Authentication authentication) throws IOException;
    Response<?> modificarConvenioEmpresa(DatosRequest request, Authentication authentication) throws IOException;
}
