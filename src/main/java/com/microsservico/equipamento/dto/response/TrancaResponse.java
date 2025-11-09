package com.microsservico.equipamento.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TrancaResponse {

    private int id;
    private int numero;
    private String localizacao;
    private String anoDeFabricacao;
    private String modelo;
    private String status;
    private Integer bicicleta;

    public TrancaResponse() {
    }
}