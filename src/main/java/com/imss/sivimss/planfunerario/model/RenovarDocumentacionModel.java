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
public class RenovarDocumentacionModel {
	
	private Integer convenioAnterior;
	private Integer comprobanteEstudios;
	private Integer actaMatrimonio;
	private Integer declaracionConcubinato;
	private Integer cartaPoder;
	private Integer ineTestigo;
	private Integer ineTestigoDos;

}
