package com.imss.sivimss.planfunerario.model;

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
public class BeneficiarioModel {
	
	private Integer idContratanteConvenioPf;
	private Integer idParentesco;
	private Integer indActa;
	private Integer indIne;
	private String actaNac;


}
