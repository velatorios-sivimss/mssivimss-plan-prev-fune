package com.imss.sivimss.planfunerario.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReporteDto {

	private String tipoReporte;
	private String rutaNombreReporte;
	private String folio;
	private Integer costoRenovacion;
	private Integer idConvenio;
	private String observaciones;
	private String directoraFibeso;
}
