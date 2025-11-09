package com.microsservico.equipamento.controller;

import com.microsservico.equipamento.domain.StatusTranca;
import com.microsservico.equipamento.domain.Tranca;
import com.microsservico.equipamento.dto.request.TrancaRequest;
import com.microsservico.equipamento.dto.response.TrancaResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrancaConverterTest {

    private final TrancaConverter converter = new TrancaConverter();

    @Test
    void dtoToDomainSucesso() {
        TrancaRequest dto = new TrancaRequest();
        dto.setNumero(10);
        dto.setModelo("Modelo T1");
        dto.setAnoDeFabricacao("2022");
        dto.setLocalizacao("Totem 1");
        dto.setStatus("OCUPADA");

        Tranca domain = converter.dtoToDomain(dto);

        assertNotNull(domain);
        assertEquals(10, domain.getNumero());
        assertEquals("Modelo T1", domain.getModelo());
        assertEquals("2022", domain.getAnoDeFabricacao());
        assertEquals("Totem 1", domain.getLocalizacao());
        assertNull(domain.getStatus());
        assertNull(domain.getIdTotem());
        assertNull(domain.getBicicleta());
    }

    @Test
    void domainToDtoSucesso() {
        Tranca domain = new Tranca();
        domain.setId(1);
        domain.setNumero(10);
        domain.setModelo("Modelo T1");
        domain.setAnoDeFabricacao("2022");
        domain.setLocalizacao("Totem 1");
        domain.setStatus(StatusTranca.LIVRE);
        domain.setIdTotem(5);
        domain.setBicicleta(100);

        TrancaResponse dto = converter.domainToDto(domain);

        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals(10, dto.getNumero());
        assertEquals("Modelo T1", dto.getModelo());
        assertEquals("2022", dto.getAnoDeFabricacao());
        assertEquals("Totem 1", dto.getLocalizacao());
        assertEquals("LIVRE", dto.getStatus());
        assertEquals(100, dto.getBicicleta());
    }
}