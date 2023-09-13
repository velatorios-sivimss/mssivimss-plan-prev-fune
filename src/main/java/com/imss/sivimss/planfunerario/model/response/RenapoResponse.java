package com.imss.sivimss.planfunerario.model.response;

import lombok.Data;

@Data
public class RenapoResponse {
    private String nomPersona ;
    private String tipoPersona= "";
    private String primerApellido;
    private String fechaNacimiento;
    private String idPais= "";
    private String segundoApellido;
    private String rfc= "";
    private String nss= "";
    private String idEstado= "";
    private String desEstado= "";
    private String correo= "";
    private String sexo= "";
    private String telefono= "";
    private String idPersona= "";
    private String curp;
}
