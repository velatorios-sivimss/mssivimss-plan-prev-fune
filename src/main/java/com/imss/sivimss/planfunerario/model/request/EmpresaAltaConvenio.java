package com.imss.sivimss.planfunerario.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmpresaAltaConvenio {
    @JsonProperty
    private String idVelatorio;
    @JsonProperty
    private String nombreVelatorio;
    @JsonProperty
    private String indTipoContratacion;
    @JsonProperty
    private String idPromotor;
    @JsonProperty
    private PorEmpresaRequest empresa;
}
