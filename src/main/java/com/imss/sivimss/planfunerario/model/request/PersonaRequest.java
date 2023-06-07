package com.imss.sivimss.planfunerario.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.imss.sivimss.planfunerario.model.BeneficiarioModel;

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
public class PersonaRequest {

	private Integer idBeneficiario;
	private Integer idPersona;
	private Boolean estatusBenefic;
	private String nombre;
	private String apellidoP;
	private String apellidoM;
	private String fechaNac;
	private String curp;
	private String rfc;
	private String correoE;
	private String tel;
	private BeneficiarioModel beneficiario;
		
	
}
