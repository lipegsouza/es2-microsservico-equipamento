package com.microsservico.equipamento.dto.request;

import lombok.Data;

@Data
public class IntegrarBicicletaRequest {
    private Integer idTranca;
    private Integer idBicicleta;
    private Integer idFuncionario;
}