package com.imss.sivimss.planfunerario.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.planfunerario.beans.RenovarPlanPFBean;
import com.imss.sivimss.planfunerario.service.RenovarPlanPFService;
import com.imss.sivimss.planfunerario.util.DatosRequest;
import com.imss.sivimss.planfunerario.util.LogUtil;
import com.imss.sivimss.planfunerario.util.ProviderServiceRestTemplate;
import com.imss.sivimss.planfunerario.util.Response;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.logging.Level;

@Slf4j
@Service
public class RenovarPlanPFImpl implements RenovarPlanPFService{
	
	private static final String ALTA = "alta";
	private static final String BAJA = "baja";
	private static final String MODIFICACION = "modificacion";
	private static final String CONSULTA = "consulta";
	
	@Autowired
	private LogUtil logUtil;
	
	@Value("${endpoints.dominio-consulta}")
	private String urlConsulta;
	
	@Value("${endpoints.dominio-consulta-paginado}")
	private String urlPaginado;
	
	@Value("${endpoints.dominio-crear-multiple}")
	private String urlInsertarMultiple;
	
	@Value("${endpoints.ms-reportes}")
	private String urlReportes;
	
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	Gson gson = new Gson();
	
	RenovarPlanPFBean renovarBean = new RenovarPlanPFBean();

	@Override
	public Response<?> buscarBeneficiarios(DatosRequest request, Authentication authentication) throws IOException {
		return providerRestTemplate.consumirServicio(renovarBean.beneficiarios(request).getDatos(), urlConsulta,
				authentication);
	}

}
