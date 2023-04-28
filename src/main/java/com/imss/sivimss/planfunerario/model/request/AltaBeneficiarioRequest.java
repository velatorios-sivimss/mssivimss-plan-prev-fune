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
public class AltaBeneficiarioRequest {

	private Integer idConvenioPF;
	private String nombre;
	private String apellidoP;
	private String apellidoM;
	private String fechaNac;
	private Integer IdParentesco;
	private String curp;
	private String rfc;
	private String actaNac;
	private String correoE;
	private String tel;
	
}
