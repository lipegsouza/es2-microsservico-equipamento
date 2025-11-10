package com.microsservico.equipamento.dto.request;

import lombok.Data;

@Data
public class RetirarBicicletaRequest {
    private Integer idTranca;
    private Integer idBicicleta;
    private Integer idFuncionario;
    private String statusAcaoReparador;
}