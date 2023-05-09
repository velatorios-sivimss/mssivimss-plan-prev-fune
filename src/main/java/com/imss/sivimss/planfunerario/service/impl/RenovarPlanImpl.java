package com.imss.sivimss.planfunerario.service.impl;

import java.io.IOException;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.planfunerario.beans.RenovarBean;
import com.imss.sivimss.planfunerario.model.request.FiltrosBeneficiariosRequest;
import com.imss.sivimss.planfunerario.model.request.FiltrosConvenioPFRequest;
import com.imss.sivimss.planfunerario.service.RenovarPlanService;
import com.imss.sivimss.planfunerario.util.DatosRequest;
import com.imss.sivimss.planfunerario.util.LogUtil;
import com.imss.sivimss.planfunerario.util.ProviderServiceRestTemplate;
import com.imss.sivimss.planfunerario.util.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RenovarPlanImpl implements RenovarPlanService {

	private static final String ALTA = "alta";
	private static final String BAJA = "baja";
	private static final String MODIFICACION = "modificacion";
	private static final String CONSULTA = "consulta";
	
	@Autowired
	private LogUtil logUtil;
	
	@Value("${endpoints.dominio-consulta}")
	private String urlConsulta;
	
	@Value("${endpoints.ms-reportes}")
	private String urlReportes;
	
	 private static final String PATH_CONSULTA="generico/consulta";
	 private static final String PATH_ACTUALIZAR="generico/actualizar";
	 
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	Gson gson = new Gson();
	
	RenovarBean renovarBean = new RenovarBean();
	
	
	@Override
	public Response<?> buscarConvenioNuevo(DatosRequest request, Authentication authentication) throws IOException {
		String datosJson = String.valueOf(request.getDatos().get("datos"));
		FiltrosConvenioPFRequest filtros = gson.fromJson(datosJson, FiltrosConvenioPFRequest .class);
		Response<?> response = providerRestTemplate.consumirServicio(renovarBean.buscarNuevo(request, filtros).getDatos(), urlConsulta + PATH_CONSULTA,
				authentication);
	      if(response.getDatos().toString().equals("{}")){
	    		logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"45 No se encontro informacion relacionada a tu busqueda", CONSULTA, authentication);
	    		response.setMensaje("45");
	    		return response;
		}
		return response;
	}
	
	public Response<?> buscarConvenioAnterior(DatosRequest request, Authentication authentication) throws IOException {
		String datosJson = String.valueOf(request.getDatos().get("datos"));
		FiltrosConvenioPFRequest filtros = gson.fromJson(datosJson, FiltrosConvenioPFRequest .class);
		Response<?> response = providerRestTemplate.consumirServicio(renovarBean.buscarAnterior(request, filtros).getDatos(), urlConsulta + PATH_CONSULTA,
				authentication);
	      if(response.getDatos().toString().equals("{}")){
	    		logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"45 No se encontro informacion relacionada a tu busqueda", CONSULTA, authentication);
	    		response.setMensaje("45");
	    		return response;
		}
		return response;
	}

}
