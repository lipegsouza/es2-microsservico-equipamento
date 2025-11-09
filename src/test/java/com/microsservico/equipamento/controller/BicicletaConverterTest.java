package com.microsservico.equipamento.controller;

import com.microsservico.equipamento.domain.Bicicleta;
import com.microsservico.equipamento.domain.StatusBicicleta;
import com.microsservico.equipamento.dto.request.BicicletaRequest;
import com.microsservico.equipamento.dto.response.BicicletaResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BicicletaConverterTest {

    private final BicicletaConverter converter = new BicicletaConverter();

    @Test
    void dtoToDomainSucesso() {
        BicicletaRequest dto = new BicicletaRequest();
        dto.setMarca("Caloi");
        dto.setModelo("Andes");
        dto.setAno("2023");
        dto.setNumero(123);
        dto.setStatus("EM_USO");

        Bicicleta domain = converter.dtoToDomain(dto);

        assertNotNull(domain);
        assertEquals("Caloi", domain.getMarca());
        assertEquals("Andes", domain.getModelo());
        assertEquals("2023", domain.getAno());
        assertEquals(123, domain.getNumero());
        assertNull(domain.getStatus());
    }

    @Test
    void domainToDtoSucesso() {
        Bicicleta domain = new Bicicleta();
        domain.setId(1);
        domain.setMarca("Caloi");
        domain.setModelo("Andes");
        domain.setAno("2023");
        domain.setNumero(123);
        domain.setStatus(StatusBicicleta.NOVA);

        BicicletaResponse dto = converter.domainToDto(domain);

        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Caloi", dto.getMarca());
        assertEquals("Andes", dto.getModelo());
        assertEquals("2023", dto.getAno());
        assertEquals(123, dto.getNumero());
        assertEquals("NOVA", dto.getStatus());
    }
}