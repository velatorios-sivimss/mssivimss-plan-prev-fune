package com.imss.sivimss.planfunerario.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatosReporteRequest extends FiltrosGeneralesDto {
    private String ruta;
    private String tipoReporte;
}
