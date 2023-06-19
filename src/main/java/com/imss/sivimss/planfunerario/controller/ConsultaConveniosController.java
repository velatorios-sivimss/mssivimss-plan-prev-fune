package com.imss.sivimss.planfunerario.controller;

import com.imss.sivimss.planfunerario.service.ConsultaConveniosService;
import com.imss.sivimss.planfunerario.util.DatosRequest;
import com.imss.sivimss.planfunerario.util.ProviderServiceRestTemplate;
import com.imss.sivimss.planfunerario.util.Response;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/consulta-convenios")
public class ConsultaConveniosController {
    private final ProviderServiceRestTemplate restTemplate;
    private final ConsultaConveniosService consultaConveniosService;

    public ConsultaConveniosController(ProviderServiceRestTemplate restTemplate,
                                       ConsultaConveniosService consultaConveniosService) {
        this.restTemplate = restTemplate;
        this.consultaConveniosService = consultaConveniosService;
    }

    @CircuitBreaker(name = "msflujo", fallbackMethod = "fallbackGenerico")
    @Retry(name = "msflujo", fallbackMethod = "fallbackGenerico")
    @TimeLimiter(name = "msflujo")
    @PostMapping("/consulta-general")
    public CompletableFuture<?> consultarTodo(@RequestBody DatosRequest request, Authentication authentication) throws IOException {
        Response<?> response = consultaConveniosService.consultarTodo(request, authentication);
        return CompletableFuture.supplyAsync(
                () -> getResponseEntity(response)
        );
    }

    @CircuitBreaker(name = "msflujo", fallbackMethod = "fallbackGenerico")
    @Retry(name = "msflujo", fallbackMethod = "fallbackGenerico")
    @TimeLimiter(name = "msflujo")
    @PostMapping("/consultar-convenios")
    public CompletableFuture<?> consultarConvenios(@RequestBody DatosRequest request, Authentication authentication) throws IOException {
        Response<?> response = consultaConveniosService.consultarConvenios(request, authentication);
        return CompletableFuture.supplyAsync(
                () -> getResponseEntity(response)
        );
    }

    @CircuitBreaker(name = "msflujo", fallbackMethod = "fallbackGenerico")
    @Retry(name = "msflujo", fallbackMethod = "fallbackGenerico")
    @TimeLimiter(name = "msflujo")
    @PostMapping("/consultar-beneficiarios")
    public CompletableFuture<?> consultarBeneficiarios(@RequestBody DatosRequest request, Authentication authentication) throws IOException {
        Response<?> response = consultaConveniosService.consultarBeneficiarios(request, authentication);
        return CompletableFuture.supplyAsync(
                () -> getResponseEntity(response)
        );
    }

    @CircuitBreaker(name = "msflujo", fallbackMethod = "fallbackGenerico")
    @Retry(name = "msflujo", fallbackMethod = "fallbackGenerico")
    @TimeLimiter(name = "msflujo")
    @PostMapping("/consultar-afiliados")
    public CompletableFuture<?> consultarAfiliados(@RequestBody DatosRequest request, Authentication authentication) throws IOException {
        Response<?> response = consultaConveniosService.consultarAfiliados(request, authentication);
        return CompletableFuture.supplyAsync(
                () -> getResponseEntity(response)
        );
    }

    @CircuitBreaker(name = "msflujo", fallbackMethod = "fallbackGenerico")
    @Retry(name = "msflujo", fallbackMethod = "fallbackGenerico")
    @TimeLimiter(name = "msflujo")
    @PostMapping("/consultar-siniestros")
    public CompletableFuture<?> consultarSiniestros(@RequestBody DatosRequest request, Authentication authentication) throws IOException {
        Response<?> response = consultaConveniosService.consultarSiniestros(request, authentication);
        return CompletableFuture.supplyAsync(
                () -> getResponseEntity(response)
        );
    }

    @CircuitBreaker(name = "msflujo", fallbackMethod = "fallbackGenerico")
    @Retry(name = "msflujo", fallbackMethod = "fallbackGenerico")
    @TimeLimiter(name = "msflujo")
    @PostMapping("/consultar-vigencias")
    public CompletableFuture<?> consultarVigencias(@RequestBody DatosRequest request, Authentication authentication) throws IOException {
        Response<?> response = consultaConveniosService.consultarVigencias(request, authentication);
        return CompletableFuture.supplyAsync(
                () -> getResponseEntity(response)
        );
    }

    @CircuitBreaker(name = "msflujo", fallbackMethod = "fallbackGenerico")
    @Retry(name = "msflujo", fallbackMethod = "fallbackGenerico")
    @TimeLimiter(name = "msflujo")
    @PostMapping("/consultar-facturas")
    public CompletableFuture<?> consultarFacturas(@RequestBody DatosRequest request, Authentication authentication) throws IOException {
        Response<?> response = consultaConveniosService.consultarFacturas(request, authentication);
        return CompletableFuture.supplyAsync(
                () -> getResponseEntity(response)
        );
    }

    @CircuitBreaker(name = "msflujo", fallbackMethod = "fallbackGenerico")
    @Retry(name = "msflujo", fallbackMethod = "fallbackGenerico")
    @TimeLimiter(name = "msflujo")
    @PostMapping("/descargar-reporte-tabla")
    public CompletableFuture<?> descargarReporteTabla(@RequestBody DatosRequest request, Authentication authentication) {
        Response<?> response = consultaConveniosService.generarReporteTabla(request, authentication);
        return CompletableFuture
                .supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
    }

    // consultar afiliados
    // consultar beneficiarios
    // consultar vigencias
    // consultar facturas
    // consultar siniestros

    /**
     * Crea el responseEntity para contestar la petici&oacute;n.
     *
     * @param response
     * @return
     */
    private static ResponseEntity<? extends Response<?>> getResponseEntity(Response<?> response) {
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo()));
    }

    /**
     * fallbacks generico
     *
     * @return respuestas
     */
    private CompletableFuture<?> fallbackGenerico(@RequestBody DatosRequest request, Authentication authentication,
                                                  CallNotPermittedException e) {
        Response<?> response = restTemplate.respuestaProvider(e.getMessage());
        return CompletableFuture
                .supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
    }

    private CompletableFuture<?> fallbackGenerico(@RequestBody DatosRequest request, Authentication authentication,
                                                  RuntimeException e) {
        Response<?> response = restTemplate.respuestaProvider(e.getMessage());
        return CompletableFuture
                .supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
    }

    private CompletableFuture<?> fallbackGenerico(@RequestBody DatosRequest request, Authentication authentication,
                                                  NumberFormatException e) {
        Response<?> response = restTemplate.respuestaProvider(e.getMessage());
        return CompletableFuture
                .supplyAsync(() -> new ResponseEntity<>(response, HttpStatus.valueOf(response.getCodigo())));
    }
}
