package com.microsservico.equipamento.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RetirarBicicletaRequest {
    private Integer idTranca;
    private Integer idBicicleta;
    private Integer idFuncionario;
    private String statusAcaoReparador;
}