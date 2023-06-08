package com.imss.sivimss.planfunerario.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Documentos {
    @JsonProperty
    private Boolean validaIneContratante;
    @JsonProperty
    private Boolean validaCurp;
    @JsonProperty
    private Boolean validaRfc;
    @JsonProperty
    private Boolean validaActaNacimientoBeneficiario;
    @JsonProperty
    private Boolean validaIneBeneficiario;
}
