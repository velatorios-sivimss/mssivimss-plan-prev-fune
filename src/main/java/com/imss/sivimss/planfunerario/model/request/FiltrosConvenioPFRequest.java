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
public class FiltrosConvenioPFRequest {

	private Integer numeroConvenio;
	private Integer numeroContratante;
	private String folio;
	private String rfc;
	private String numIne;
	private Integer vigencia;
	
}
