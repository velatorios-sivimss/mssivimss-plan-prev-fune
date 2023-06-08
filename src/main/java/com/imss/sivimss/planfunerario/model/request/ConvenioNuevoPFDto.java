package com.imss.sivimss.planfunerario.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ConvenioNuevoPFDto {
    @JsonProperty
    private String folioConvenio;
    @JsonProperty
    private String fechaInicio;
    @JsonProperty
    private String fechaVigencia;
    @JsonProperty
    private String idVelatorio;
    @JsonProperty
    private String nombreVelatorio;
    @JsonProperty
    private String indSiniestros;
    @JsonProperty
    private String idPromotor;
    @JsonProperty
    private Integer indTipoContratacion;
    @JsonProperty
    private PorEmpresaRequest empresa;
    @JsonProperty
    private PersonaRequest persona;
}
