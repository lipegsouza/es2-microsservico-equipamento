package com.microsservico.equipamento.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TotemResponse {

    private int id;
    private String localizacao;
    private String descricao;
}