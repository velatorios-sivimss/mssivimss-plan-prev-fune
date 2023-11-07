package com.imss.sivimss.planfunerario.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.imss.sivimss.planfunerario.model.RenovarDocumentacionModel;

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
public class VerificarDocumentacionRequest {

	private Integer idConvenio;
	private Integer idValidacionDoc;
	private Integer ineAfiliado;
	private Integer curp;
	private Integer rfc;
	private Integer actaNac;
	private Integer ineBeneficiario;
	private RenovarDocumentacionModel renovarDoc;
	
}
