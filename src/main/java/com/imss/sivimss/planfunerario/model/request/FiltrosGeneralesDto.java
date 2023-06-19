package com.imss.sivimss.planfunerario.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FiltrosGeneralesDto {
    private String folioConvenio;
    private String rfc;
    private String nombre; // a que se refiere este nombre
    private String curp;
    private Integer estatusConvenio;
}
