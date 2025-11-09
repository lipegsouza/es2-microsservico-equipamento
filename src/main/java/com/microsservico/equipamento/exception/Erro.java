package com.microsservico.equipamento.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Erro {
    private String codigo;
    private String mensagem;

    public Erro(String codigo, String mensagem) {
        this.codigo = codigo;
        this.mensagem = mensagem;
    }

}