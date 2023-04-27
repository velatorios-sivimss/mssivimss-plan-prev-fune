package com.imss.sivimss.planfunerario.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.planfunerario.util.DatosRequest;
import com.imss.sivimss.planfunerario.util.Response;

public interface RenovarPlanPFService {

	Response<?> buscarBeneficiarios(DatosRequest request, Authentication authentication) throws IOException;

	Response<?> detalleBeneficiario(DatosRequest request, Authentication authentication) throws IOException;

}
