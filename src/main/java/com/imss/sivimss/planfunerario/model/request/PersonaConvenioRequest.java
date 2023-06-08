package com.imss.sivimss.planfunerario.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PersonaConvenioRequest {
    @JsonProperty
    private String idVelatorio;
    @JsonProperty
    private String nombreVelatorio;
    @JsonProperty
    private String indTipoContratacion;
    @JsonProperty
    private String idPromotor;
    @JsonProperty
    private String idPersona;
    @JsonProperty
    private String idDomicilio;
    @JsonProperty
    private String idContratante;
    @JsonProperty
    private PersonaAltaConvenio persona;
}
