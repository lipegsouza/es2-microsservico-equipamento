package com.microsservico.equipamento.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BicicletaResponse {

    private int id;
    private String marca;
    private String modelo;
    private String ano;
    private int numero;
    private String status;

    public BicicletaResponse() {
    }
}