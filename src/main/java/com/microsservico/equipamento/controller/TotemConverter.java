package com.microsservico.equipamento.controller;

import com.microsservico.equipamento.domain.Totem;
import com.microsservico.equipamento.dto.request.TotemRequest;
import com.microsservico.equipamento.dto.response.TotemResponse;
import org.springframework.stereotype.Component;

@Component
public class TotemConverter {

    public Totem dtoToDomain(TotemRequest dto) {
        Totem totem = new Totem();
        totem.setLocalizacao(dto.getLocalizacao());
        totem.setDescricao(dto.getDescricao());
        return totem;
    }

    public TotemResponse domainToDto(Totem totem) {
        TotemResponse dto = new TotemResponse();
        dto.setId(totem.getId());
        dto.setLocalizacao(totem.getLocalizacao());
        dto.setDescricao(totem.getDescricao());
        return dto;
    }
}