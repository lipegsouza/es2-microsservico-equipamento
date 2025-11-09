package com.microsservico.equipamento.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "bicicleta")
public class Bicicleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String marca;
    private String modelo;
    private String ano;
    private int numero;

    @Enumerated(EnumType.STRING)
    private StatusBicicleta status;
}