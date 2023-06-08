package com.imss.sivimss.planfunerario.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PdfDto {
    private String rutaNombreReporte;
    private String ciudadExpedicion;
    private String fechaExpedicion;
    private String folioConvenio;
}
