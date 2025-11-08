package com.microsservico.equipamento.domain;

import javax.persistence.*;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getAnoDeFabricacao() {
        return anoDeFabricacao;
    }

    public void setAnoDeFabricacao(String anoDeFabricacao) {
        this.anoDeFabricacao = anoDeFabricacao;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public StatusTranca getStatus() {
        return status;
    }

    public void setStatus(StatusTranca status) {
        this.status = status;
    }

    public Integer getBicicleta() {
        return bicicleta;
    }

    public void setBicicleta(Integer bicicleta) {
        this.bicicleta = bicicleta;
    }
}