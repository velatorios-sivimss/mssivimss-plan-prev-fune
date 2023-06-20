package com.imss.sivimss.planfunerario.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.imss.sivimss.planfunerario.beans.ConvenioNuevoPF;
import com.imss.sivimss.planfunerario.beans.ModificarConvenioNuevoPf;
import com.imss.sivimss.planfunerario.model.request.*;
import com.imss.sivimss.planfunerario.model.response.BeneficiarioResponse;
import com.imss.sivimss.planfunerario.model.response.BusquedaInformacionReporteResponse;
import com.imss.sivimss.planfunerario.model.response.BusquedaPersonaFolioResponse;
import com.imss.sivimss.planfunerario.model.response.ContratanteResponse;
import com.imss.sivimss.planfunerario.service.ContratarPlanPFService;
import com.imss.sivimss.planfunerario.util.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ContratarPlanPFServiceImpl implements ContratarPlanPFService {
    @Value("${endpoints.rutas.dominio-consulta}")
    private String urlDominioConsulta;
    @Value("${endpoints.ms-dominio-convenios}")
    private String urlDominioConvenio;
    @Value("${endpoints.rutas.dominio-consulta}")
    private String urlDominioActualizar;
    @Value("${endpoints.ms-reportes}")
    private String urlReportes;
    @Autowired
    private ProviderServiceRestTemplate providerRestTemplate;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ContratarPlanPFServiceImpl.class);
    JsonParser jsonParser = new JsonParser();
    @Autowired
    private ConvenioNuevoPF convenioBean;

    ModificarConvenioNuevoPf modificar = new ModificarConvenioNuevoPf();
    Gson json = new Gson();
    @Autowired
    ModelMapper modelMapper;

    @Override
    public Response<?> agregarConvenioNuevoPF(DatosRequest request, Authentication authentication) throws IOException {

        String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
        UsuarioDto usuarioDto = json.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
        PersonaConvenioRequest persona = json.fromJson(datosJson, PersonaConvenioRequest.class);
        String queryPersona = "";
        String queryDomicilio = "";
        String queryContratante = "";
        if (persona.getIdPersona() == null) {
            queryPersona = convenioBean.generarQueryPersona(persona.getPersona(), usuarioDto.getIdUsuario().toString());
        }
        if (persona.getIdContratante() == null) {
            queryContratante = convenioBean.generarQueryContratante(persona.getPersona(), usuarioDto.getIdUsuario().toString());
        }
        if (persona.getIdDomicilio() == null) {
            queryDomicilio = convenioBean.generarQueryDomicilio(persona.getPersona().getCalle(), persona.getPersona().getNumeroExterior(),
                    persona.getPersona().getNumeroInterior(), persona.getPersona().getCp(), persona.getPersona().getColonia(),
                    persona.getPersona().getMunicipio(), persona.getPersona().getEstado(), usuarioDto.getIdUsuario().toString());
        }
        String queryConvenioPf = convenioBean.generarQueryConvenioPf(persona.getNombreVelatorio(), persona.getIdPromotor(), persona.getIdVelatorio(), usuarioDto.getIdUsuario().toString(), "1");
        String queryContratantePaquete = convenioBean.generarQueryContratantePaquete(persona, usuarioDto.getIdUsuario().toString());
        String[] queryBeneficiario = new String[persona.getPersona().getBeneficiarios().length];
        String[] queryContratanteBeneficiarios = new String[persona.getPersona().getBeneficiarios().length];
        for (int i = 0; i < persona.getPersona().getBeneficiarios().length; i++) {
            queryBeneficiario[i] = DatatypeConverter.printBase64Binary(convenioBean.generarQueryPersonaBeneficiaria(persona.getPersona().getBeneficiarios()[i], usuarioDto.getIdUsuario().toString()).getBytes("UTF-8"));
            queryContratanteBeneficiarios[i] = DatatypeConverter.printBase64Binary(convenioBean.generarQueryContratanteBeneficiarios(persona.getPersona().getBeneficiarios()[i].getParentesco(), persona.getPersona().getClaveActa(), usuarioDto.getIdUsuario().toString(),persona.getPersona(),authentication).getBytes("UTF-8"));
        }
        HashMap mapa = new HashMap();
        mapa.put("datosPersonaContratante", DatatypeConverter.printBase64Binary(queryPersona.getBytes("UTF-8")));
        mapa.put("datosDomicilio", DatatypeConverter.printBase64Binary(queryDomicilio.getBytes("UTF-8")));
        mapa.put("datosContratante", DatatypeConverter.printBase64Binary(queryContratante.getBytes("UTF-8")));
        mapa.put("datosConvenioPf", DatatypeConverter.printBase64Binary(queryConvenioPf.getBytes("UTF-8")));
        mapa.put("datosContratantePaquete", DatatypeConverter.printBase64Binary(queryContratantePaquete.getBytes("UTF-8")));
        mapa.put("datosBeneficiario", queryBeneficiario);
        mapa.put("datosContratanteBeneficiarios", queryContratanteBeneficiarios);
        mapa.put("datosValidacionDocumentos", DatatypeConverter.printBase64Binary(convenioBean.generarQueryValidacionDocumentos(persona, String.valueOf(usuarioDto.getIdUsuario())).getBytes("UTF-8")));
        mapa.put("idPersona", persona.getIdPersona());
        mapa.put("idContratante", persona.getIdContratante());
        mapa.put("idDomicilio", persona.getIdDomicilio());
        return providerRestTemplate.consumirServicio(mapa, urlDominioConvenio, authentication);
    }

    @Override
    public Response<?> agregarConvenioNuevoPFEmpresa(DatosRequest request, Authentication authentication) throws IOException {

        String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
        UsuarioDto usuarioDto = json.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
        EmpresaAltaConvenio empresa = json.fromJson(datosJson, EmpresaAltaConvenio.class);
        String queryConvenioPf = convenioBean.generarQueryConvenioPf(empresa.getNombreVelatorio(), empresa.getIdPromotor(), empresa.getIdVelatorio(), usuarioDto.getIdUsuario().toString(), "0");
        String queryDomicilio = convenioBean.generarQueryDomicilio(empresa.getEmpresa().getCalle(), empresa.getEmpresa().getNumeroExterior(),
                empresa.getEmpresa().getNumeroInterior(), empresa.getEmpresa().getCp(), empresa.getEmpresa().getColonia(),
                empresa.getEmpresa().getMunicipio(), empresa.getEmpresa().getEstado(), usuarioDto.getIdUsuario().toString());
        String queryEmpresaConvenioPF = convenioBean.generarQueryEmpresaConvenioPf(empresa.getEmpresa(), usuarioDto.getIdUsuario().toString());

        log.info("--------- datos a enviar ----------");
        HashMap mapa = new HashMap();
        mapa.put("datosConvenio", DatatypeConverter.printBase64Binary(queryConvenioPf.getBytes("UTF-8")));
        mapa.put("datosDireccion", DatatypeConverter.printBase64Binary(queryDomicilio.getBytes("UTF-8")));
        mapa.put("datosEmpresaConvenio", DatatypeConverter.printBase64Binary(queryEmpresaConvenioPF.getBytes("UTF-8")));
        mapa.put("datosPersonas", empresa.getEmpresa().getPersonas());
        mapa.put("usuario", usuarioDto.getIdUsuario().toString());
        return providerRestTemplate.consumirServicio(mapa, urlDominio + "/convenioPf/insertConvenios/empresa", authentication);
    }


    @Override
    public Response<?> consultaPromotores(DatosRequest request, Authentication authentication) throws IOException {
        return providerRestTemplate.consumirServicio(convenioBean.consultarPromotores().getDatos(), urlDominioConsulta, authentication);
    }

    @Override
    public Response<?> validaCurpRfc(DatosRequest request, Authentication authentication) throws IOException {
        JsonObject objeto = (JsonObject) jsonParser.parse((String) request.getDatos().get(AppConstantes.DATOS));
        String curp = String.valueOf(objeto.get("curp"));
        String rfc = String.valueOf(objeto.get("rfc"));
        return providerRestTemplate.consumirServicio(convenioBean.consultarCurpRfc(curp, rfc).getDatos(), urlDominioConsulta, authentication);
    }

    @Override
    public Response<?> consultaCP(DatosRequest request, Authentication authentication) throws IOException {
        JsonObject objeto = (JsonObject) jsonParser.parse((String) request.getDatos().get(AppConstantes.DATOS));
        String cp = String.valueOf(objeto.get("cp"));
        return providerRestTemplate.consumirServicio(convenioBean.consultarCP(cp).getDatos(), urlDominioConsulta, authentication);
    }

    @Override
    public Response<?> generarPDF(DatosRequest request, Authentication authentication) throws IOException {
        String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
        PdfDto pdfDto = json.fromJson(datosJson, PdfDto.class);
        Map<String, Object> envioDatos = new ConvenioNuevoPF().generarReporte(pdfDto, buscarInformacionReporte(pdfDto.getFolioConvenio(), authentication));
        return providerRestTemplate.consumirServicioReportes(envioDatos, urlReportes,
                authentication);
    }

    @Override
    public Response<?> busquedaFolioPersona(DatosRequest request, Authentication authentication) throws IOException {
        Response<?> response = new Response<>();
        List<BeneficiarioResponse> beneficiariosResponse;
        List<ContratanteResponse> contratanteResponse;
        BusquedaPersonaFolioResponse busquedaFolio = new BusquedaPersonaFolioResponse();
        JsonObject objeto = (JsonObject) jsonParser.parse((String) request.getDatos().get(AppConstantes.DATOS));
        String folioConvenio = String.valueOf(objeto.get("folioConvenio"));
        Response<?> responseContratante = providerRestTemplate.consumirServicio(convenioBean.busquedaFolioPersona(folioConvenio).getDatos(), urlDominioConsulta, authentication);
        if (!responseContratante.getDatos().toString().equals("[]")) {
            contratanteResponse = Arrays.asList(modelMapper.map(responseContratante.getDatos(), ContratanteResponse[].class));
            beneficiariosResponse = Arrays.asList(modelMapper.map(providerRestTemplate.consumirServicio(convenioBean.busquedaBeneficiarios(folioConvenio).getDatos(), urlDominioConsulta, authentication).getDatos(), BeneficiarioResponse[].class));
            busquedaFolio.setDatosContratante(contratanteResponse.get(0));
            busquedaFolio.setBeneficiarios(beneficiariosResponse);
            busquedaFolio.setFolioConvenio(folioConvenio);
            response.setCodigo(200);
            response.setError(false);
            response.setMensaje("");
            response.setDatos(ConvertirGenerico.convertInstanceOfObject(busquedaFolio));
            return response;
        }
        response.setCodigo(200);
        response.setError(true);
        response.setMensaje("52");
        return response;
    }

    @Override
    public Response<?> busquedaFolioEmpresa(DatosRequest request, Authentication authentication) throws IOException {
        JsonObject objeto = (JsonObject) jsonParser.parse((String) request.getDatos().get(AppConstantes.DATOS));
        String folioConvenio = String.valueOf(objeto.get("folioConvenio"));
        return providerRestTemplate.consumirServicio(convenioBean.busquedaFolioEmpresa(folioConvenio).getDatos(), urlDominioConsulta, authentication);
    }

    @Override
    public Response<?> busquedaRfcEmpresa(DatosRequest request, Authentication authentication) throws IOException {
        log.info("- Se entra a consulta rfc empresa -");
        JsonObject objeto = (JsonObject) jsonParser.parse((String) request.getDatos().get(AppConstantes.DATOS));
        String rfc = String.valueOf(objeto.get("rfc"));
        return providerRestTemplate.consumirServicio(convenioBean.busquedaRfcEmpresa(rfc).getDatos(), urlDominioConsulta, authentication);
    }

    @Override
    public Response<?> activarDesactivarConvenio(DatosRequest request, Authentication authentication) throws IOException {
        JsonObject objeto = (JsonObject) jsonParser.parse((String) request.getDatos().get(AppConstantes.DATOS));
        UsuarioDto usuarioDto = json.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
        String folioConvenio = String.valueOf(objeto.get("folioConvenio"));
        String bandera = String.valueOf(objeto.get("banderaActivo"));
        switch (bandera) {
            case "1":
                log.info("Activando convenio");
                return providerRestTemplate.consumirServicio(convenioBean.cambiarEstatusConvenio("2", folioConvenio, usuarioDto).getDatos(), urlDominioActualizar, authentication);
            case "0":
                log.info("Desactivando convenio");
                return providerRestTemplate.consumirServicio(convenioBean.cambiarEstatusConvenio("3", folioConvenio, usuarioDto).getDatos(), urlDominioActualizar, authentication);
            default:
                log.warn("No se pudo activar o desactivar el convenio");
        }
        return new Response<>();
    }



    public BusquedaInformacionReporteResponse buscarInformacionReporte(String folioConvenio, Authentication authentication) throws IOException {
        BusquedaInformacionReporteResponse resultadoBusquedaInfo;
        Response<?> respuestaBusqueda = providerRestTemplate.consumirServicio(convenioBean.busquedaFolioParaReporte(folioConvenio).getDatos(), urlDominioConsulta, authentication);
        List<BusquedaInformacionReporteResponse> infoReporte = Arrays.asList(modelMapper.map(respuestaBusqueda.getDatos(), BusquedaInformacionReporteResponse[].class));
        resultadoBusquedaInfo = infoReporte.get(0);
        return resultadoBusquedaInfo;
    }
}


