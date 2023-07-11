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
	private String nombre;
	private String tipoPrevision;
	private String numInt;
	private String fechaInicio;
	private String correo;
	private Integer idTipoPf;
	private String telefono;
	private String fechaVigencia;
	private String numExt;
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
