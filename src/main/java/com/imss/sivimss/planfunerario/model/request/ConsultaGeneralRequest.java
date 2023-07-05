package com.imss.sivimss.planfunerario.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsultaGeneralRequest extends FiltrosGeneralesDto {
    // agregar los campos que faltan para el filtrado de cada seccion
    private String numeroFactura;
    private String folioSiniestro;
    private String nombreBeneficiario;
    private Integer idVelatorio;
}
