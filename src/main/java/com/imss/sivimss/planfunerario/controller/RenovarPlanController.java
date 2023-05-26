package com.imss.sivimss.planfunerario.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.imss.sivimss.planfunerario.service.RenovarPlanService;
import com.imss.sivimss.planfunerario.util.DatosRequest;
import com.imss.sivimss.planfunerario.util.LogUtil;
import com.imss.sivimss.planfunerario.util.ProviderServiceRestTemplate;
import com.imss.sivimss.planfunerario.util.Response;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/")
public class RenovarPlanController {
	
	private static final String ALTA = "alta";
	private static final String IMPRIMIR = "imprimir";
	private static final String MODIFICACION = "modificacion";
	private static final String CONSULTA = "consulta";

	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	@Autowired
	private RenovarPlanService renovarPlan;
	
	@Autowired
	private LogUtil logUtil;
	
	@CircuitBreaker(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@Retry(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@TimeLimiter(name = "msflujo")
	@PostMapping("buscar-convenio-nuevo")
	public CompletableFuture<?> buscarConvenioPFNuevo(@RequestBody DatosRequest request,Authentication authentication) throws IOException {
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"Buscar Convenio PF", CONSULTA, authentication);
		Response<?> response = renovarPlan.buscarConvenioNuevo(request,authentication); 
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}
	
	@CircuitBreaker(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@Retry(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@TimeLimiter(name = "msflujo")
	@PostMapping("buscar-convenio-anterior")
	public CompletableFuture<?> buscarConvenioPFAnterior(@RequestBody DatosRequest request,Authentication authentication) throws IOException {
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"Buscar Convenio PF", CONSULTA, authentication);
		Response<?> response = renovarPlan.buscarConvenioAnterior(request,authentication); 
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}
	
	@CircuitBreaker(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@Retry(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@TimeLimiter(name = "msflujo")
	@PostMapping("renovar-convenio-pf")
	public CompletableFuture<?> renovarConvenioPF(@RequestBody DatosRequest request,Authentication authentication) throws IOException {
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"Renovar Convenio PF", ALTA, authentication);
		Response<?> response = renovarPlan.renovarConvenio(request,authentication); 
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}
	
	@CircuitBreaker(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@Retry(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@TimeLimiter(name = "msflujo")
	@PostMapping("/descargar-reporte/adenda-renovacion")
	public CompletableFuture<?> descargarReporte(@RequestBody DatosRequest request,Authentication authentication) throws IOException {
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"Imprimir Plantilla de renovacion contrato nuevo", IMPRIMIR, authentication);
		Response<?> response = renovarPlan.descargarAdendaRenovacionAnual(request,authentication);
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}
	
	@CircuitBreaker(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@Retry(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@TimeLimiter(name = "msflujo")
	@PostMapping("/descargar-reporte/convenio-anterior")
	public CompletableFuture<?> descargarReporteConvenioAnterior(@RequestBody DatosRequest request,Authentication authentication) throws IOException {
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"Imprimir plantilla de renovacion contrato anterior", IMPRIMIR, authentication);
		Response<?> response = renovarPlan.descargarConvenioPlanAnterior(request,authentication);
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}
	
	@CircuitBreaker(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@Retry(name = "msflujo", fallbackMethod = "fallbackGenerico")
	@TimeLimiter(name = "msflujo")
	@PostMapping("/descargar-reporte/hoja-afiliacion")
	public CompletableFuture<?> descargarHojaAfilicion(@RequestBody DatosRequest request,Authentication authentication) throws IOException {
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(),this.getClass().getPackage().toString(),"Imprimir Plantilla Hoja de afiliacion", IMPRIMIR, authentication);
		Response<?> response = renovarPlan.descargarHojaAfiliacion(request,authentication);
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}
	
	/**
	 * fallbacks generico
	 * 
	 * @return respuestas
	 */
	private CompletableFuture<?> fallbackGenerico(@RequestBody DatosRequest request, Authentication authentication,
			CallNotPermittedException e) {
		Response<?> response = providerRestTemplate.respuestaProvider(e.getMessage());
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}

	private CompletableFuture<?> fallbackGenerico(@RequestBody DatosRequest request, Authentication authentication,
			RuntimeException e) {
		Response<?> response = providerRestTemplate.respuestaProvider(e.getMessage());
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}

	private CompletableFuture<?> fallbackGenerico(@RequestBody DatosRequest request, Authentication authentication,
			NumberFormatException e) {
		Response<?> response = providerRestTemplate.respuestaProvider(e.getMessage());
		return CompletableFuture
				.supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
	}

}
