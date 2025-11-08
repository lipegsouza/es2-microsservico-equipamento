package com.microsservico.equipamento.dto.response;

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

    public TrancaResponse(int id, int numero, String modelo, String status, Integer bicicleta) {
        this.id = id;
        this.numero = numero;
        this.modelo = modelo;
        this.status = status;
        this.bicicleta = bicicleta;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getBicicleta() {
        return bicicleta;
    }

    public void setBicicleta(Integer bicicleta) {
        this.bicicleta = bicicleta;
    }
}