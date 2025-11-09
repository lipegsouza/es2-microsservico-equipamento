package com.microsservico.equipamento.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TotemRequest {
    private String localizacao;
    private String descricao;
}