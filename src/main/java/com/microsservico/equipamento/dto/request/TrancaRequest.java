package com.microsservico.equipamento.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TrancaRequest {
    private int numero;
    private String localizacao;
    private String anoDeFabricacao;
    private String modelo;
    private String status;
}