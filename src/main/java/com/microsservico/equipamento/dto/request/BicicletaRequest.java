package com.microsservico.equipamento.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BicicletaRequest {
    private String marca;
    private String modelo;
    private String ano;
    private int numero;
    private String status;
}