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
public class DatosConvenioResponse {
	
	private String estado;
	private Integer costoRenovacion;
	private Integer indRenovacion;
	private String segundoApellido;
	private Integer estatusConvenio;
	private String nomContratante; 
	private String tipoPrevision;
	private String numInterior;   
	private String fecInicio;     
	private String correo;
	private Integer idTipoPrevision;
	private String tel;   
	private String fecVigencia;    
	private String numExterior;     
	private String primerApellido;
	private String municipio;
	private String calle;
	private Integer idPaquete;
	private Integer cp;
    private String rfc;
    private List<BenefResponse> beneficiarios;
    private String tipoPaquete;
    private String folio;
    private String matricula;
    private Integer idConvenio;
}
