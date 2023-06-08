package com.imss.sivimss.planfunerario.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class BusquedaInformacionReporteResponse {
    private String rfc;
    private String curp;
    private String nss;
    private String nombrePersona;
    private String primerApellido;
    private String segundoApellido;
    private String numIne;
    private String idPaquete;
    private String nombrePaquete;
    private String desPaquete;
    private String monPrecio;
}
