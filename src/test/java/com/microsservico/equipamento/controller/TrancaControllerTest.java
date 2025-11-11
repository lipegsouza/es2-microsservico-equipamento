package com.microsservico.equipamento.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsservico.equipamento.domain.StatusTranca;
import com.microsservico.equipamento.domain.Tranca;
import com.microsservico.equipamento.dto.request.TrancaRequest;
import com.microsservico.equipamento.dto.response.TrancaResponse;
import com.microsservico.equipamento.exception.InvalidActionException;
import com.microsservico.equipamento.exception.NotFoundException;
import com.microsservico.equipamento.service.TrancaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.microsservico.equipamento.domain.Bicicleta;
import com.microsservico.equipamento.dto.request.IntegrarTrancaRequest;
import com.microsservico.equipamento.dto.request.RetirarTrancaRequest;
import com.microsservico.equipamento.dto.request.TrancaAcaoRequest;
import com.microsservico.equipamento.dto.response.BicicletaResponse;

@WebMvcTest(TrancaController.class)
    class TrancaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrancaService service;

    @MockBean
    private TrancaConverter converter;

    @MockBean
    private BicicletaConverter bicicletaConverter;

    private TrancaRequest exemploTeste() {
        TrancaRequest requestDto = new TrancaRequest();
        requestDto.setNumero(10);
        requestDto.setModelo("Modelo X");
        requestDto.setAnoDeFabricacao("2023");
        return requestDto;
    }

    @Test
    void cadastrarSucesso() throws Exception {
        TrancaRequest requestDto = exemploTeste();

        Tranca trancaConvertida = new Tranca();
        Tranca trancaSalva = new Tranca();
        trancaSalva.setId(1);
        trancaSalva.setStatus(StatusTranca.NOVA);

        TrancaResponse responseDto = new TrancaResponse();
        responseDto.setId(1);
        responseDto.setStatus("NOVA");

        when(converter.dtoToDomain(any(TrancaRequest.class))).thenReturn(trancaConvertida);
        when(service.cadastrar(any(Tranca.class))).thenReturn(trancaSalva);
        when(converter.domainToDto(any(Tranca.class))).thenReturn(responseDto);

        mockMvc.perform(post("/tranca")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("NOVA"));
    }

    @Test
    void cadastrarErro() throws Exception {
        TrancaRequest requestDtoInvalido = exemploTeste();
        requestDtoInvalido.setModelo("");

        Tranca trancaConvertida = new Tranca();
        trancaConvertida.setModelo("");

        String msgErro = "Dados inválidos. Modelo e Ano de Fabricação são obrigatórios.";

        when(converter.dtoToDomain(any(TrancaRequest.class))).thenReturn(trancaConvertida);
        when(service.cadastrar(any(Tranca.class))).thenThrow(new InvalidActionException(msgErro));

        mockMvc.perform(post("/tranca")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDtoInvalido)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("422"))
                .andExpect(jsonPath("$.mensagem").value(msgErro));
    }

    @Test
    void buscarSucesso() throws Exception {
        Tranca tranca = new Tranca();
        tranca.setId(1);
        TrancaResponse responseDto = new TrancaResponse();
        responseDto.setId(1);

        when(service.buscar(1)).thenReturn(tranca);
        when(converter.domainToDto(tranca)).thenReturn(responseDto);

        mockMvc.perform(get("/tranca/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void buscarErro() throws Exception {
        int idInexistente = 99;
        String msgErro = "Tranca não encontrada com o ID: " + idInexistente;
        when(service.buscar(idInexistente)).thenThrow(new NotFoundException(msgErro));

        mockMvc.perform(get("/tranca/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("404"))
                .andExpect(jsonPath("$.mensagem").value(msgErro));
    }

    @Test
    void listarSucesso() throws Exception {
        Tranca tranca1 = new Tranca();
        tranca1.setId(1);
        Tranca tranca2 = new Tranca();
        tranca2.setId(2);
        List<Tranca> listaDomain = Arrays.asList(tranca1, tranca2);

        TrancaResponse response1 = new TrancaResponse();
        response1.setId(1);
        TrancaResponse response2 = new TrancaResponse();
        response2.setId(2);

        when(service.listar()).thenReturn(listaDomain);
        when(converter.domainToDto(tranca1)).thenReturn(response1);
        when(converter.domainToDto(tranca2)).thenReturn(response2);

        mockMvc.perform(get("/tranca")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void editarSucesso() throws Exception {
        int idExistente = 1;
        TrancaRequest requestDto = exemploTeste();
        requestDto.setModelo("Modelo-Novo");

        Tranca trancaConvertida = new Tranca();
        trancaConvertida.setModelo("Modelo-Novo");

        Tranca trancaEditada = new Tranca();
        trancaEditada.setId(idExistente);
        trancaEditada.setModelo("Modelo-Novo");

        TrancaResponse responseDto = new TrancaResponse();
        responseDto.setId(idExistente);
        responseDto.setModelo("Modelo-Novo");

        when(converter.dtoToDomain(any(TrancaRequest.class))).thenReturn(trancaConvertida);
        when(service.editar(eq(idExistente), any(Tranca.class))).thenReturn(trancaEditada);
        when(converter.domainToDto(trancaEditada)).thenReturn(responseDto);

        mockMvc.perform(put("/tranca/" + idExistente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idExistente))
                .andExpect(jsonPath("$.modelo").value("Modelo-Novo"));
    }

    @Test
    void deletarSucesso() throws Exception {
        int idExistente = 1;

        mockMvc.perform(delete("/tranca/" + idExistente)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deletarErro() throws Exception {
        int idExistente = 1;
        String msgErro = "Ação inválida. Apenas trancas sem bicicletas podem ser excluídas.";

        doThrow(new InvalidActionException(msgErro))
                .when(service)
                .deletar(idExistente);

        mockMvc.perform(delete("/tranca/" + idExistente)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("422"))
                .andExpect(jsonPath("$.mensagem").value(msgErro));
    }

    @Test
    void integrarNaRedeSucesso() throws Exception {
        IntegrarTrancaRequest request = new IntegrarTrancaRequest();
        request.setIdTranca(1);
        request.setIdTotem(1);

        mockMvc.perform(post("/tranca/integrarNaRede")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void integrarNaRedeErro() throws Exception {
        IntegrarTrancaRequest request = new IntegrarTrancaRequest();
        request.setIdTranca(1);
        request.setIdTotem(1);

        String msgErro = "Ação inválida. Tranca deve estar com status NOVA ou EM_REPARO.";
        doThrow(new InvalidActionException(msgErro)).when(service).integrarNaRede(any(IntegrarTrancaRequest.class));

        mockMvc.perform(post("/tranca/integrarNaRede")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensagem").value(msgErro));
    }

    @Test
    void retirarDaRedeSucesso() throws Exception {
        RetirarTrancaRequest request = new RetirarTrancaRequest();
        request.setIdTranca(1);
        request.setIdTotem(1);
        request.setStatusAcaoReparador("EM_REPARO");

        mockMvc.perform(post("/tranca/retirarDaRede")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void getBicicletaSucesso() throws Exception {
        Bicicleta bicicleta = new Bicicleta();
        bicicleta.setId(100);
        BicicletaResponse responseDto = new BicicletaResponse();
        responseDto.setId(100);

        when(service.getBicicleta(1)).thenReturn(bicicleta);
        when(bicicletaConverter.domainToDto(bicicleta)).thenReturn(responseDto);

        mockMvc.perform(get("/tranca/1/bicicleta")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100));
    }

    @Test
    void getBicicletaErro() throws Exception {
        String msgErro = "Tranca está livre ou não possui bicicleta associada.";
        when(service.getBicicleta(1)).thenThrow(new NotFoundException(msgErro));

        mockMvc.perform(get("/tranca/1/bicicleta")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value(msgErro));
    }

    @Test
    void trancarSucesso() throws Exception {
        TrancaAcaoRequest request = new TrancaAcaoRequest();
        request.setBicicleta(100);

        Tranca tranca = new Tranca();
        tranca.setId(1);
        tranca.setStatus(StatusTranca.OCUPADA);

        TrancaResponse responseDto = new TrancaResponse();
        responseDto.setId(1);
        responseDto.setStatus("OCUPADA");

        when(service.trancar(eq(1), any(TrancaAcaoRequest.class))).thenReturn(tranca);
        when(converter.domainToDto(tranca)).thenReturn(responseDto);

        mockMvc.perform(post("/tranca/1/trancar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OCUPADA"));
    }

    @Test
    void destrancarSucesso() throws Exception {
        TrancaAcaoRequest request = new TrancaAcaoRequest();
        request.setBicicleta(100);

        Tranca tranca = new Tranca();
        tranca.setId(1);
        tranca.setStatus(StatusTranca.LIVRE);

        TrancaResponse responseDto = new TrancaResponse();
        responseDto.setId(1);
        responseDto.setStatus("LIVRE");

        when(service.destrancar(eq(1), any(TrancaAcaoRequest.class))).thenReturn(tranca);
        when(converter.domainToDto(tranca)).thenReturn(responseDto);

        mockMvc.perform(post("/tranca/1/destrancar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("LIVRE"));
    }

    @Test
    void alterarStatusSucesso() throws Exception {
        Tranca tranca = new Tranca();
        tranca.setId(1);
        tranca.setStatus(StatusTranca.LIVRE);

        TrancaResponse responseDto = new TrancaResponse();
        responseDto.setId(1);
        responseDto.setStatus("LIVRE");

        when(service.alterarStatus(1, "DESTRANCAR")).thenReturn(tranca);
        when(converter.domainToDto(tranca)).thenReturn(responseDto);

        mockMvc.perform(post("/tranca/1/status/DESTRANCAR")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("LIVRE"));
    }
}