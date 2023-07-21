package com.imss.sivimss.planfunerario.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.planfunerario.beans.RenovarBean;
import com.imss.sivimss.planfunerario.util.Response;

public interface PagosService {

	void insertar(RenovarBean renovarBean, Response<?> response, Authentication authentication)throws IOException;

}
