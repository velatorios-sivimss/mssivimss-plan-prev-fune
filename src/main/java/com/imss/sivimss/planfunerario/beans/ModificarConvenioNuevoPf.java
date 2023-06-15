package com.imss.sivimss.planfunerario.beans;

import com.imss.sivimss.planfunerario.model.request.PersonaAltaConvenio;
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
        queryPersona.agregarParametroValues("NUM_SEXO", "'" + persona.getSexo() + "'");
        queryPersona.agregarParametroValues("DES_OTRO_SEXO", "'" + persona.getOtroSexo() + "'");
        queryPersona.agregarParametroValues("FEC_NAC", "'" + persona.getFechaNacimiento() + "'");
        queryPersona.agregarParametroValues("ID_PAIS", "'" + persona.getPais() + "'");
        queryPersona.agregarParametroValues("ID_ESTADO", "'" + persona.getEstado() + "'");
        queryPersona.agregarParametroValues("DES_TELEFONO", "'" + persona.getTelefono() + "'");
        queryPersona.agregarParametroValues("DES_CORREO", "'" + persona.getCorreoElectronico() + "'");
        queryPersona.agregarParametroValues("TIPO_PERSONA", "'" + persona.getTipoPersona() + "'");
        queryPersona.agregarParametroValues("NUM_INE", "'" + persona.getNumIne() + "'");
        queryPersona.addWhere("ID_PERSONA = " + idPersona);
        String consultaActualizaPersona = queryPersona.obtenerQueryActualizar();
        return consultaActualizaPersona;
    }
}
