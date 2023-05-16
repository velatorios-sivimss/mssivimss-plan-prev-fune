package com.imss.sivimss.planfunerario.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.planfunerario.util.DatosRequest;
import com.imss.sivimss.planfunerario.util.Response;

public interface BeneficiariosService {

	Response<?> buscarBeneficiarios(DatosRequest request, Authentication authentication) throws IOException;

	Response<?> detalleBeneficiario(DatosRequest request, Authentication authentication) throws IOException;

	Response<?> crearBeneficiario(DatosRequest request, Authentication authentication) throws IOException;

	Response<?> editarBeneficiario(DatosRequest request, Authentication authentication) throws IOException;

	Response<?> estatusBeneficiario(DatosRequest request, Authentication authentication) throws IOException;

	Response<?> buscarBeneficiariosPlanAnterior(DatosRequest request, Authentication authentication)throws IOException;

}
