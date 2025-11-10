package com.microsservico.equipamento.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IntegrarBicicletaRequest {
    private Integer idTranca;
    private Integer idBicicleta;
    private Integer idFuncionario;
}