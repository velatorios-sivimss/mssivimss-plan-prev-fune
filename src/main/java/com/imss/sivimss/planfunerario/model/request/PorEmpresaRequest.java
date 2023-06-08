package com.imss.sivimss.planfunerario.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PorEmpresaRequest {
    @JsonProperty
    private String nombreEmpresa;
    @JsonProperty
    private String razonSocial;
    @JsonProperty
    private String rfc;
    @JsonProperty
    private String pais;
    @JsonProperty
    private String cp;
    @JsonProperty
    private String colonia;
    @JsonProperty
    private String estado;
    @JsonProperty
    private String municipio;
    @JsonProperty
    private String calle;
    @JsonProperty
    private String numeroExterior;
    @JsonProperty
    private String numeroInterior;
    @JsonProperty
    private String telefono;
    @JsonProperty
    private String correoElectronico;
    @JsonProperty
    private PersonaRequest[] personas;
}
