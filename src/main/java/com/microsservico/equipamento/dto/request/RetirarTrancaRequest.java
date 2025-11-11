package com.microsservico.equipamento.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RetirarTrancaRequest {
    private Integer idTotem;
    private Integer idTranca;
    private Integer idFuncionario;
    private String statusAcaoReparador;
}