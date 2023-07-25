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
public class FiltrosConvenioExtRequest {
	
	private Integer idDelegacion;
	private Integer idVelatorio;
	private Integer numConvenio;
	private String folio;
	private String rfc;
	private String pagina;
	private String tamanio;

}
