package com.microsservico.equipamento.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsservico.equipamento.domain.Bicicleta;
import com.microsservico.equipamento.domain.StatusBicicleta;
import com.microsservico.equipamento.dto.request.BicicletaRequest;
import com.microsservico.equipamento.dto.response.BicicletaResponse;
import com.microsservico.equipamento.exception.NotFoundException;
import com.microsservico.equipamento.service.BicicletaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq; // NOVO IMPORT
import static org.mockito.Mockito.when;
// NOVOS IMPORTS
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
// FIM NOVOS IMPORTS
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BicicletaController.class)
public class BicicletaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BicicletaService service;

    @MockBean
    private BicicletaConverter converter;

    @Test
    void cadastrarSucesso() throws Exception {

        BicicletaRequest requestDto = new BicicletaRequest();
        requestDto.setMarca("Caloi");

        Bicicleta bicicletaParaSalvar = new Bicicleta();
        Bicicleta bicicletaSalva = new Bicicleta();
        bicicletaSalva.setId(1);
        bicicletaSalva.setStatus(StatusBicicleta.NOVA);

        BicicletaResponse responseDto = new BicicletaResponse();
        responseDto.setId(1);
        responseDto.setStatus("NOVA");

        when(converter.dtoToDomain(any(BicicletaRequest.class))).thenReturn(bicicletaParaSalvar);
        when(service.cadastrar(any(Bicicleta.class))).thenReturn(bicicletaSalva);
        when(converter.domainToDto(bicicletaSalva)).thenReturn(responseDto);

        mockMvc.perform(post("/bicicleta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("NOVA"));
    }

    @Test
    void buscarSucesso() throws Exception {

        Bicicleta bicicletaDoServico = new Bicicleta();
        bicicletaDoServico.setId(1);

        BicicletaResponse dtoDeResposta = new BicicletaResponse();
        dtoDeResposta.setId(1);

        when(service.buscar(1)).thenReturn(bicicletaDoServico);
        when(converter.domainToDto(bicicletaDoServico)).thenReturn(dtoDeResposta);

        mockMvc.perform(get("/bicicleta/1")
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void buscarErro() throws Exception {
        int idInexistente = 99;
        String mensagemErro = "Bicicleta não encontrada com o ID: " + idInexistente;

        when(service.buscar(idInexistente)).thenThrow(new NotFoundException(mensagemErro));

        mockMvc.perform(get("/bicicleta/99")
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("404"))
                .andExpect(jsonPath("$.mensagem").value(mensagemErro));
    }

    @Test
    void listarSucesso() throws Exception {

        Bicicleta bicicleta1 = new Bicicleta();
        bicicleta1.setId(1);
        Bicicleta bicicleta2 = new Bicicleta();
        bicicleta2.setId(2);
        List<Bicicleta> listaDeBicicletas = Arrays.asList(bicicleta1, bicicleta2);

        BicicletaResponse responseDto1 = new BicicletaResponse();
        responseDto1.setId(1);
        BicicletaResponse responseDto2 = new BicicletaResponse();
        responseDto2.setId(2);

        when(service.listar()).thenReturn(listaDeBicicletas);
        when(converter.domainToDto(bicicleta1)).thenReturn(responseDto1);
        when(converter.domainToDto(bicicleta2)).thenReturn(responseDto2);

        mockMvc.perform(get("/bicicleta")
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void editarSucesso() throws Exception {
        int idExistente = 1;

        BicicletaRequest requestDto = new BicicletaRequest();
        requestDto.setMarca("Marca-Nova");

        Bicicleta bicicletaConvertida = new Bicicleta();
        bicicletaConvertida.setMarca("Marca-Nova");

        Bicicleta bicicletaEditada = new Bicicleta();
        bicicletaEditada.setId(idExistente);
        bicicletaEditada.setMarca("Marca-Nova");

        BicicletaResponse responseDto = new BicicletaResponse();
        responseDto.setId(idExistente);
        responseDto.setMarca("Marca-Nova");

        when(converter.dtoToDomain(any(BicicletaRequest.class))).thenReturn(bicicletaConvertida);
        when(service.editar(eq(idExistente), any(Bicicleta.class))).thenReturn(bicicletaEditada);
        when(converter.domainToDto(bicicletaEditada)).thenReturn(responseDto);

        mockMvc.perform(put("/bicicleta/" + idExistente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.id").value(idExistente))
                .andExpect(jsonPath("$.marca").value("Marca-Nova"));
    }

    @Test
    void editarErro() throws Exception {
        int idInexistente = 99;
        String mensagemErro = "Bicicleta não encontrada com o ID: " + idInexistente;

        BicicletaRequest requestDto = new BicicletaRequest();
        requestDto.setMarca("Marca-Nova");

        when(converter.dtoToDomain(any(BicicletaRequest.class))).thenReturn(new Bicicleta());
        when(service.editar(eq(idInexistente), any(Bicicleta.class)))
                .thenThrow(new NotFoundException(mensagemErro));

        mockMvc.perform(put("/bicicleta/" + idInexistente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))

                .andExpect(status().isNotFound())

                .andExpect(jsonPath("$.codigo").value("404"))
                .andExpect(jsonPath("$.mensagem").value(mensagemErro));
    }

    @Test
    void deletarSucesso() throws Exception {
        int idExistente = 1;

        mockMvc.perform(delete("/bicicleta/" + idExistente)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk());
    }

    @Test
    void deletarErro() throws Exception {
        int idInexistente = 99;
        String mensagemErro = "Bicicleta não encontrada com o ID: " + idInexistente;

        org.mockito.Mockito.doThrow(new NotFoundException(mensagemErro))
                .when(service)
                .deletar(idInexistente);

        mockMvc.perform(delete("/bicicleta/" + idInexistente)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound())

                .andExpect(jsonPath("$.codigo").value("404"))
                .andExpect(jsonPath("$.mensagem").value(mensagemErro));
    }
}