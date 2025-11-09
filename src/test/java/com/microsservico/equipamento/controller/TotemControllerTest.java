package com.microsservico.equipamento.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsservico.equipamento.domain.Bicicleta;
import com.microsservico.equipamento.domain.Totem;
import com.microsservico.equipamento.domain.Tranca;
import com.microsservico.equipamento.dto.request.TotemRequest;
import com.microsservico.equipamento.dto.response.BicicletaResponse;
import com.microsservico.equipamento.dto.response.TotemResponse;
import com.microsservico.equipamento.dto.response.TrancaResponse;
import com.microsservico.equipamento.exception.InvalidActionException;
import com.microsservico.equipamento.exception.NotFoundException;
import com.microsservico.equipamento.service.TotemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TotemController.class)
public class TotemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TotemService service;

    @MockBean
    private TotemConverter converter;

    @MockBean
    private TrancaConverter trancaConverter;

    @MockBean
    private BicicletaConverter bicicletaConverter;

    private TotemRequest exemploTeste() {
        TotemRequest requestDto = new TotemRequest();
        requestDto.setLocalizacao("Rua Teste");
        requestDto.setDescricao("Totem Teste");
        return requestDto;
    }

    @Test
    public void cadastrarSucesso() throws Exception {
        TotemRequest requestDto = exemploTeste();
        Totem totem = new Totem();
        totem.setId(1);
        TotemResponse responseDto = new TotemResponse();
        responseDto.setId(1);

        when(converter.dtoToDomain(any(TotemRequest.class))).thenReturn(totem);
        when(service.cadastrar(any(Totem.class))).thenReturn(totem);
        when(converter.domainToDto(any(Totem.class))).thenReturn(responseDto);

        mockMvc.perform(post("/totem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void cadastrarErro() throws Exception {
        TotemRequest requestDtoInvalido = exemploTeste();
        requestDtoInvalido.setLocalizacao("");

        String msgErro = "Dados inválidos. Localização e Descrição são obrigatórios.";

        when(converter.dtoToDomain(any(TotemRequest.class))).thenReturn(new Totem());
        when(service.cadastrar(any(Totem.class))).thenThrow(new InvalidActionException(msgErro));

        mockMvc.perform(post("/totem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDtoInvalido)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("422"))
                .andExpect(jsonPath("$.mensagem").value(msgErro));
    }

    @Test
    public void listarSucesso() throws Exception {
        when(service.listar()).thenReturn(List.of(new Totem(), new Totem()));
        when(converter.domainToDto(any(Totem.class))).thenReturn(new TotemResponse());

        mockMvc.perform(get("/totem")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    public void editarSucesso() throws Exception {
        int idExistente = 1;
        TotemRequest requestDto = exemploTeste();
        requestDto.setLocalizacao("Nova");

        Totem totem = new Totem();
        totem.setId(idExistente);
        totem.setLocalizacao("Nova");

        TotemResponse responseDto = new TotemResponse();
        responseDto.setId(idExistente);
        responseDto.setLocalizacao("Nova");

        when(converter.dtoToDomain(any(TotemRequest.class))).thenReturn(totem);
        when(service.editar(eq(idExistente), any(Totem.class))).thenReturn(totem);
        when(converter.domainToDto(totem)).thenReturn(responseDto);

        mockMvc.perform(put("/totem/" + idExistente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idExistente))
                .andExpect(jsonPath("$.localizacao").value("Nova"));
    }

    @Test
    public void editarErro() throws Exception {
        int idInexistente = 99;
        String msgErro = "Totem não encontrado com o ID: " + idInexistente;

        when(converter.dtoToDomain(any(TotemRequest.class))).thenReturn(new Totem());
        when(service.editar(eq(idInexistente), any(Totem.class)))
                .thenThrow(new NotFoundException(msgErro));

        mockMvc.perform(put("/totem/" + idInexistente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exemploTeste())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deletarSucesso() throws Exception {
        mockMvc.perform(delete("/totem/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deletarErro() throws Exception {
        int idExistente = 1;
        String msgErro = "Ação inválida. Apenas totens sem trancas podem ser excluídos.";

        doThrow(new InvalidActionException(msgErro))
                .when(service)
                .deletar(idExistente);

        mockMvc.perform(delete("/totem/" + idExistente)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("422"))
                .andExpect(jsonPath("$.mensagem").value(msgErro));
    }

    @Test
    public void listarTrancasSucesso() throws Exception {
        int idTotem = 1;
        when(service.listarTrancas(idTotem)).thenReturn(List.of(new Tranca()));
        when(trancaConverter.domainToDto(any(Tranca.class))).thenReturn(new TrancaResponse());

        mockMvc.perform(get("/totem/" + idTotem + "/trancas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    public void listarBicicletasSucesso() throws Exception {
        int idTotem = 1;
        when(service.listarBicicletas(idTotem)).thenReturn(List.of(new Bicicleta()));
        when(bicicletaConverter.domainToDto(any(Bicicleta.class))).thenReturn(new BicicletaResponse());

        mockMvc.perform(get("/totem/" + idTotem + "/bicicletas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    public void listarTrancasErro() throws Exception {
        int idInexistente = 99;
        String msgErro = "Totem não encontrado com o ID: " + idInexistente;
        when(service.listarTrancas(idInexistente)).thenThrow(new NotFoundException(msgErro));

        mockMvc.perform(get("/totem/" + idInexistente + "/trancas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value(msgErro));
    }
}