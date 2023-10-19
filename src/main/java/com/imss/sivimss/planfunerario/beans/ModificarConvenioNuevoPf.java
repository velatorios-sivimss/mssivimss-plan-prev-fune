package com.imss.sivimss.planfunerario.beans;

import com.imss.sivimss.planfunerario.model.request.PersonaAltaConvenio;
import com.imss.sivimss.planfunerario.model.request.PorEmpresaRequest;
import com.imss.sivimss.planfunerario.util.QueryHelper;

public class ModificarConvenioNuevoPf {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ConvenioNuevoPF.class);

    public String generaQueryActualizaPersona(PersonaAltaConvenio persona, String usuario, String idPersona){
        final QueryHelper queryPersona = new QueryHelper("UPDATE SVC_PERSONA");
        queryPersona.agregarParametroValues("CVE_RFC", "'" + persona.getRfc() + "'");
        queryPersona.agregarParametroValues("CVE_CURP", "'" + persona.getCurp() + "'");
        queryPersona.agregarParametroValues("CVE_NSS", "'" + persona.getNss() + "'");
        queryPersona.agregarParametroValues("NOM_PERSONA", "'" + persona.getNombre() + "'");
        queryPersona.agregarParametroValues("NOM_PRIMER_APELLIDO", "'" + persona.getPrimerApellido() + "'");
        queryPersona.agregarParametroValues("NOM_SEGUNDO_APELLIDO", "'" + persona.getSegundoApellido() + "'");
        //queryPersona.agregarParametroValues("NUM_SEXO", "'" + persona.getSexo() + "'");
        queryPersona.agregarParametroValues("REF_OTRO_SEXO", "'" + persona.getOtroSexo() + "'");
       // queryPersona.agregarParametroValues("FEC_NAC", "'" + persona.getFechaNacimiento() + "'");
        log.info("id pais persona");
        queryPersona.agregarParametroValues("ID_PAIS", "'" + persona.getPais() + "'");
        //queryPersona.agregarParametroValues("ID_ESTADO", "'" + persona.getEstado() + "'");
        queryPersona.agregarParametroValues("REF_TELEFONO", "'" + persona.getTelefono() + "'");
        queryPersona.agregarParametroValues("REF_CORREO", "'" + persona.getCorreoElectronico() + "'");
        queryPersona.agregarParametroValues("TIP_PERSONA", "'" + persona.getTipoPersona() + "'");
        queryPersona.agregarParametroValues("NUM_INE", "'" + persona.getNumIne() + "'");
        queryPersona.agregarParametroValues("ID_USUARIO_MODIFICA", usuario);
        queryPersona.agregarParametroValues("FEC_ACTUALIZACION", "NOW()");
        queryPersona.addWhere("ID_PERSONA = " + idPersona);
        return queryPersona.obtenerQueryActualizar();
    }

    public String generaQueryActualizaPersonaBeneficiario(PersonaAltaConvenio persona, String usuario, String idPersona){
        final QueryHelper queryPersona = new QueryHelper("UPDATE SVC_PERSONA");
        queryPersona.agregarParametroValues("CVE_RFC", "'" + persona.getRfc() + "'");
        queryPersona.agregarParametroValues("CVE_CURP", "'" + persona.getCurp() + "'");
        queryPersona.agregarParametroValues("CVE_NSS", "'" + persona.getNss() + "'");
        queryPersona.agregarParametroValues("NOM_PERSONA", "'" + persona.getNombre() + "'");
        queryPersona.agregarParametroValues("NOM_PRIMER_APELLIDO", "'" + persona.getPrimerApellido() + "'");
        queryPersona.agregarParametroValues("NOM_SEGUNDO_APELLIDO", "'" + persona.getSegundoApellido() + "'");
        //queryPersona.agregarParametroValues("NUM_SEXO", "'" + persona.getSexo() + "'");
        queryPersona.agregarParametroValues("REF_OTRO_SEXO", "'" + persona.getOtroSexo() + "'");
        queryPersona.agregarParametroValues("FEC_NAC", "'" + persona.getFechaNacimiento() + "'");
        queryPersona.agregarParametroValues("ID_PAIS", "'" + persona.getPais() + "'");
        //queryPersona.agregarParametroValues("ID_ESTADO", "'" + persona.getEstado() + "'");
        queryPersona.agregarParametroValues("REF_TELEFONO", "'" + persona.getTelefono() + "'");
        queryPersona.agregarParametroValues("REF_CORREO", "'" + persona.getCorreoElectronico() + "'");
        queryPersona.agregarParametroValues("TIP_PERSONA", "'" + persona.getTipoPersona() + "'");
        queryPersona.agregarParametroValues("NUM_INE", "'" + persona.getNumIne() + "'");
        queryPersona.agregarParametroValues("ID_USUARIO_MODIFICA", usuario);
        queryPersona.agregarParametroValues("FEC_ACTUALIZACION", "NOW()");
        queryPersona.addWhere("ID_PERSONA = " + idPersona);
        return queryPersona.obtenerQueryActualizar();
    }

    public String queryModificaDomicilio(String calle, String numExt, String numInt, String cp, String colonia, String municipio, String estado, String usuario){
        final QueryHelper queryDomicilio = new QueryHelper("UPDATE SVT_DOMICILIO");
        queryDomicilio.agregarParametroValues("REF_CALLE", "'" + calle + "'");
        queryDomicilio.agregarParametroValues("NUM_EXTERIOR", "'" + numExt + "'");
        queryDomicilio.agregarParametroValues("NUM_INTERIOR", "'" + numInt + "'");
        queryDomicilio.agregarParametroValues("REF_CP", "'" + cp + "'");
        queryDomicilio.agregarParametroValues("REF_COLONIA", "'" + colonia + "'");
        queryDomicilio.agregarParametroValues("REF_MUNICIPIO", "'" + municipio + "'");
        queryDomicilio.agregarParametroValues("REF_ESTADO", "'" + estado + "'");
        queryDomicilio.agregarParametroValues("ID_USUARIO_MODIFICA", usuario);
        queryDomicilio.agregarParametroValues("FEC_ACTUALIZACION", "NOW()");
        queryDomicilio.addWhere("ID_DOMICILIO = idDomicilio" );
        log.info(queryDomicilio.obtenerQueryActualizar());
        return queryDomicilio.obtenerQueryActualizar();
    }

    public String queryModificarEmpresaConvenio(PorEmpresaRequest empresa, String usuario){
        final QueryHelper queryEmpresaConvenio = new QueryHelper("UPDATE SVT_EMPRESA_CONVENIO_PF");
        queryEmpresaConvenio.agregarParametroValues("REF_NOMBRE", "'" + empresa.getNombreEmpresa() + "'");
        queryEmpresaConvenio.agregarParametroValues("REF_RAZON_SOCIAL", "'" + empresa.getRazonSocial() + "'");
        queryEmpresaConvenio.agregarParametroValues("CVE_RFC", "'" + empresa.getRfc() + "'");
        queryEmpresaConvenio.agregarParametroValues("ID_PAIS", "'" + empresa.getPais() + "'");
        queryEmpresaConvenio.agregarParametroValues("ID_DOMICILIO", "idDomicilio");
        queryEmpresaConvenio.agregarParametroValues("REF_TELEFONO", "'" + empresa.getTelefono() + "'");
        queryEmpresaConvenio.agregarParametroValues("REF_CORREO", "'" + empresa.getCorreoElectronico() + "'");
        queryEmpresaConvenio.agregarParametroValues("ID_USUARIO_MODIFICA", usuario);
        queryEmpresaConvenio.agregarParametroValues("FEC_ACTUALIZACION", "NOW()");
        queryEmpresaConvenio.addWhere("ID_EMPRESA_CONVENIO_PF = idEmpresaConvenio");
        log.info(queryEmpresaConvenio.obtenerQueryActualizar());
       return queryEmpresaConvenio.obtenerQueryActualizar();
    }
}
