package com.microsservico.equipamento.domain;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "tranca")
public class Tranca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int numero;
    private String localizacao;
    private String anoDeFabricacao;
    private String modelo;

    @Enumerated(EnumType.STRING)
    private StatusTranca status;

    private Integer bicicleta;

    private Integer idTotem;
}