package com.imss.sivimss.planfunerario.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.planfunerario.util.DatosRequest;
import com.imss.sivimss.planfunerario.util.Response;

public interface RenovarExtService {

	Response<?> buscarRenovacionExt(DatosRequest request, Authentication authentication) throws IOException;

	Response<?> verDetalleRenovacionExt(DatosRequest request, Authentication authentication) throws IOException;

	Response<?> actualizarEstatus(DatosRequest request, Authentication authentication) throws IOException;

}
