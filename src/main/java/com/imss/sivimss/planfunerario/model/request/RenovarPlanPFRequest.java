package com.imss.sivimss.planfunerario.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@JsonIgnoreType(value = true)
public class RenovarPlanPFRequest {
	
	private String datosBancarios;
	private Integer idConvenioPf;
	private String velatorio;
	private String vigencia;
	private Integer indRenovacion;

}
