package com.imss.sivimss.planfunerario.model.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class BuscarBeneficiariosResponse {
	
	private Integer idConvenio;
	private Integer idContratanteConvenioPf;
	private List<BenefResponse> beneficiarios;

}
