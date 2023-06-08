package com.imss.sivimss.planfunerario.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class BusquedaPersonaFolioResponse {
    private String folioConvenio;
    private ContratanteResponse datosContratante;
    private List<BeneficiarioResponse> beneficiarios;
}
