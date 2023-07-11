package com.imss.sivimss.planfunerario.model;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.imss.sivimss.planfunerario.model.request.PersonaRequest;

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
public class DocBeneficiariosPlanAnteriorModel {
	
	private Integer indComprobanteEstudios;
	private Integer indActaMatrimonio;
	private Integer indDeclaracionConcubinato;

}
