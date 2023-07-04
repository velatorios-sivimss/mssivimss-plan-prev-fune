package com.imss.sivimss.planfunerario.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.imss.sivimss.planfunerario.beans.ModificarConvenioNuevoPf;
import com.imss.sivimss.planfunerario.model.request.EmpresaAltaConvenio;
import com.imss.sivimss.planfunerario.model.request.PersonaConvenioRequest;
import com.imss.sivimss.planfunerario.model.request.UsuarioDto;
import com.imss.sivimss.planfunerario.service.ModificarConvenioPfService;
import com.imss.sivimss.planfunerario.util.AppConstantes;
import com.imss.sivimss.planfunerario.util.DatosRequest;
import com.imss.sivimss.planfunerario.util.ProviderServiceRestTemplate;
import com.imss.sivimss.planfunerario.util.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.HashMap;

@Service

public class ModificarConvenioPfImpl implements ModificarConvenioPfService {
    @Value("${endpoints.rutas.dominio-actualizar-convenio}")
    private String urlDominioConvenioModificar;
    @Value("${endpoints.ms-reportes}")
    private String urlReportes;
    @Autowired
    private ProviderServiceRestTemplate providerRestTemplate;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ModificarConvenioPfImpl.class);
    JsonParser jsonParser = new JsonParser();

    ModificarConvenioNuevoPf modificar = new ModificarConvenioNuevoPf();
    Gson json = new Gson();
    @Autowired
    ModelMapper modelMapper;
    @Override
    public Response<?> modificarConvenioPersona(DatosRequest request, Authentication authentication) throws IOException {
        HashMap mapa = new HashMap();
        String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
        UsuarioDto usuarioDto = json.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
        PersonaConvenioRequest persona = json.fromJson(datosJson, PersonaConvenioRequest.class);
        String consultaModificarDatosPersona = modificar.generaQueryActualizaPersona(persona.getPersona(),usuarioDto.getIdUsuario().toString(), persona.getIdPersona());
        String consultaModificaDomicilio = modificar.queryModificaDomicilio(persona.getPersona().getCalle(),persona.getPersona().getNumeroExterior(),
                persona.getPersona().getNumeroInterior(),persona.getPersona().getCp(),persona.getPersona().getColonia(),
                persona.getPersona().getMunicipio(),persona.getPersona().getEstado(),usuarioDto.getIdUsuario().toString());
        String[] queryBeneficiarios = new String[persona.getPersona().getBeneficiarios().length];
        for (int i = 0; i < persona.getPersona().getBeneficiarios().length; i++) {
            queryBeneficiarios[i] = DatatypeConverter.printBase64Binary(modificar.generaQueryActualizaPersona(persona.getPersona().getBeneficiarios()[i], usuarioDto.getIdUsuario().toString(), persona.getPersona().getBeneficiarios()[i].getIdPersona()).getBytes("UTF-8"));
         }
        mapa.put("datosPersonaContratante", DatatypeConverter.printBase64Binary(consultaModificarDatosPersona.getBytes("UTF-8")));
        mapa.put("datosDomicilioContratante",DatatypeConverter.printBase64Binary(consultaModificaDomicilio.getBytes("UTF-8")));
        mapa.put("beneficiariosContratante",queryBeneficiarios);
        mapa.put("folioConvenio",persona.getFolioConvenio());
        return providerRestTemplate.consumirServicio(mapa, urlDominioConvenioModificar, authentication);
    }

    @Override
    public Response<?> modificarConvenioEmpresa(DatosRequest request, Authentication authentication) throws IOException {
        HashMap mapa = new HashMap();
        String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
        UsuarioDto usuarioDto = json.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
        EmpresaAltaConvenio empresa = json.fromJson(datosJson, EmpresaAltaConvenio.class);
        String consultaModificaDomicilio = modificar.queryModificaDomicilio(empresa.getEmpresa().getCalle(),empresa.getEmpresa().getNumeroExterior(),
                empresa.getEmpresa().getNumeroInterior(),empresa.getEmpresa().getCp(),empresa.getEmpresa().getColonia(),
                empresa.getEmpresa().getMunicipio(),empresa.getEmpresa().getEstado(),usuarioDto.getIdUsuario().toString());
        String consultaEmpresaConvenio = modificar.queryModificarEmpresaConvenio(empresa.getEmpresa(),usuarioDto.getIdUsuario().toString());

        mapa.put("domicilioEmpresa",DatatypeConverter.printBase64Binary(consultaModificaDomicilio.getBytes("UTF-8")));
        mapa.put("empresaConvenio",DatatypeConverter.printBase64Binary(consultaEmpresaConvenio.getBytes("UTF-8")));
        mapa.put("datosPersonas", empresa.getEmpresa().getPersonas());
        mapa.put("usuario", usuarioDto.getIdUsuario().toString());
        mapa.put("folioConvenio",empresa.getFolioConvenio());
        return providerRestTemplate.consumirServicio(mapa, urlDominioConvenioModificar + "/empresa", authentication);
    }
}
