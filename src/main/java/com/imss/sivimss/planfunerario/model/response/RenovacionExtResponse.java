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
public class RenovacionExtResponse {

	private Integer idConvenio;
	private String folio;
	private String rfc;
	private String matricula;
	private String nombre;
	private String primerApellido;
	private String segundoApellido;
	private Integer tipoPrevision;
	private Integer idEstatus;
	private String tipoPaquete;
	private Double cuotaRecuperacion;
	private String fecInicio;
	private String fecVigencia;
	private String correo;
	private String tel;
	private List<BenefResponse> beneficiarios;
	
}
