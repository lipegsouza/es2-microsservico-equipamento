package com.microsservico.equipamento.controller;

import com.microsservico.equipamento.domain.Tranca;
import com.microsservico.equipamento.dto.request.TrancaRequest;
import com.microsservico.equipamento.dto.response.TrancaResponse;
import org.springframework.stereotype.Component;

@Component
public class TrancaConverter {

    public Tranca dtoToDomain(TrancaRequest dto) {
        Tranca tranca = new Tranca();

        tranca.setNumero(dto.getNumero());
        tranca.setLocalizacao(dto.getLocalizacao());
        tranca.setAnoDeFabricacao(dto.getAnoDeFabricacao());
        tranca.setModelo(dto.getModelo());

        return tranca;
    }

    public TrancaResponse domainToDto(Tranca tranca) {
        TrancaResponse dto = new TrancaResponse();

        dto.setId(tranca.getId());
        dto.setNumero(tranca.getNumero());
        dto.setLocalizacao(tranca.getLocalizacao());
        dto.setAnoDeFabricacao(tranca.getAnoDeFabricacao());
        dto.setModelo(tranca.getModelo());
        dto.setBicicleta(tranca.getBicicleta());
        dto.setStatus(tranca.getStatus().name());


        return dto;
    }
}