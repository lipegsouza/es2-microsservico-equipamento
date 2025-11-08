package com.microsservico.equipamento.controller;

import com.microsservico.equipamento.domain.Bicicleta;
import com.microsservico.equipamento.dto.request.BicicletaRequest;
import com.microsservico.equipamento.dto.response.BicicletaResponse;
import org.springframework.stereotype.Component;

@Component
public class BicicletaConverter {

    public Bicicleta dtoToDomain(BicicletaRequest dto) {
        Bicicleta bicicleta = new Bicicleta();

        bicicleta.setMarca(dto.getMarca());
        bicicleta.setModelo(dto.getModelo());
        bicicleta.setAno(dto.getAno());
        bicicleta.setNumero(dto.getNumero());

        return bicicleta;
    }

    public BicicletaResponse domainToDto(Bicicleta bicicleta) {
        BicicletaResponse dto = new BicicletaResponse();

        dto.setId(bicicleta.getId());
        dto.setMarca(bicicleta.getMarca());
        dto.setModelo(bicicleta.getModelo());
        dto.setAno(bicicleta.getAno());
        dto.setNumero(bicicleta.getNumero());
        dto.setStatus(bicicleta.getStatus().name());


        return dto;
    }
}