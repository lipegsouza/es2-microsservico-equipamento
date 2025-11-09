package com.microsservico.equipamento.controller;

import com.microsservico.equipamento.domain.Totem;
import com.microsservico.equipamento.dto.request.TotemRequest;
import com.microsservico.equipamento.dto.response.TotemResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TotemConverterTest {

    private final TotemConverter converter = new TotemConverter();

    @Test
    void dtoToDomainSucesso() {
        TotemRequest dto = new TotemRequest();
        dto.setLocalizacao("Rua X");
        dto.setDescricao("Totem da Rua X");

        Totem domain = converter.dtoToDomain(dto);

        assertNotNull(domain);
        assertEquals("Rua X", domain.getLocalizacao());
        assertEquals("Totem da Rua X", domain.getDescricao());
    }

    @Test
    void domainToDtoSucesso() {
        Totem domain = new Totem();
        domain.setId(1);
        domain.setLocalizacao("Rua X");
        domain.setDescricao("Totem da Rua X");

        TotemResponse dto = converter.domainToDto(domain);

        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Rua X", dto.getLocalizacao());
        assertEquals("Totem da Rua X", dto.getDescricao());
    }
}