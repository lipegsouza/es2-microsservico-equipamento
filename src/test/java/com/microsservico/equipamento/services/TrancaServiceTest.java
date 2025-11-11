package com.microsservico.equipamento.services;

import com.microsservico.equipamento.domain.StatusTranca;
import com.microsservico.equipamento.domain.Tranca;
import com.microsservico.equipamento.exception.InvalidActionException;
import com.microsservico.equipamento.exception.NotFoundException;
import com.microsservico.equipamento.repository.TrancaRepository;
import com.microsservico.equipamento.service.TrancaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.microsservico.equipamento.domain.Bicicleta;
import com.microsservico.equipamento.domain.StatusBicicleta;
import com.microsservico.equipamento.domain.Totem;
import com.microsservico.equipamento.dto.request.IntegrarTrancaRequest;
import com.microsservico.equipamento.dto.request.RetirarTrancaRequest;
import com.microsservico.equipamento.dto.request.TrancaAcaoRequest;
import com.microsservico.equipamento.repository.BicicletaRepository;
import com.microsservico.equipamento.service.TotemService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
    class TrancaServiceTest {

    @Mock
    private TrancaRepository repository;

    @InjectMocks
    private TrancaService service;

    @Mock
    private TotemService totemService;

    @Mock
    private BicicletaRepository bicicletaRepository;

    private Tranca exemploTeste() {
        Tranca tranca = new Tranca();
        tranca.setNumero(10);
        tranca.setModelo("Modelo X");
        tranca.setAnoDeFabricacao("2023");
        tranca.setLocalizacao("Totem 1");
        return tranca;
    }

    @Test
    void cadastrarSucesso() {
        Tranca trancaEntrada = exemploTeste();

        when(repository.salvar(any(Tranca.class))).thenAnswer(inv -> inv.getArgument(0));

        Tranca resultado = service.cadastrar(trancaEntrada);

        assertNotNull(resultado);
        assertEquals(StatusTranca.NOVA, resultado.getStatus());
        verify(repository, times(1)).salvar(trancaEntrada);
    }

    @Test
    void cadastrarErro() {
        Tranca trancaInvalida = exemploTeste();
        trancaInvalida.setNumero(0);

        assertThrows(InvalidActionException.class, () -> service.cadastrar(trancaInvalida));
        verify(repository, times(0)).salvar(any(Tranca.class));
    }

    @Test
    void cadastrarErro2() {
        Tranca trancaInvalida = exemploTeste();
        trancaInvalida.setModelo("");

        assertThrows(InvalidActionException.class, () -> service.cadastrar(trancaInvalida));
        verify(repository, times(0)).salvar(any(Tranca.class));
    }

    @Test
    void buscarSucesso() {
        Tranca tranca = exemploTeste();
        tranca.setId(1);
        when(repository.buscar(1)).thenReturn(Optional.of(tranca));

        Tranca resultado = service.buscar(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        verify(repository, times(1)).buscar(1);
    }

    @Test
    void buscarErro() {
        when(repository.buscar(99)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.buscar(99));
    }

    @Test
    void editarSucesso() {
        int idExistente = 1;

        Tranca dadosNovos = new Tranca();
        dadosNovos.setNumero(999);
        dadosNovos.setModelo("Modelo Novo");
        dadosNovos.setAnoDeFabricacao("2025");
        dadosNovos.setLocalizacao("Totem 2");

        Tranca trancaExistente = exemploTeste();
        trancaExistente.setId(idExistente);
        trancaExistente.setStatus(StatusTranca.NOVA);

        when(repository.buscar(idExistente)).thenReturn(Optional.of(trancaExistente));
        when(repository.salvar(any(Tranca.class))).thenAnswer(inv -> inv.getArgument(0));

        Tranca resultado = service.editar(idExistente, dadosNovos);

        assertNotNull(resultado);
        assertEquals(idExistente, resultado.getId());
        assertEquals("Modelo Novo", resultado.getModelo());
        assertEquals("Totem 2", resultado.getLocalizacao());

        assertEquals(10, resultado.getNumero());
        assertEquals(StatusTranca.NOVA, resultado.getStatus());

        verify(repository, times(1)).buscar(idExistente);
        verify(repository, times(1)).salvar(trancaExistente);
    }

    @Test
    void deletarSucesso() {
        int idExistente = 1;
        Tranca trancaExistente = exemploTeste();
        trancaExistente.setId(idExistente);
        trancaExistente.setBicicleta(null);

        when(repository.buscar(idExistente)).thenReturn(Optional.of(trancaExistente));

        service.deletar(idExistente);

        verify(repository, times(1)).buscar(idExistente);
        verify(repository, times(1)).deletar(idExistente);
    }

    @Test
    void deletarErro() {
        int idExistente = 1;
        Tranca trancaExistente = exemploTeste();
        trancaExistente.setId(idExistente);
        trancaExistente.setBicicleta(100);

        when(repository.buscar(idExistente)).thenReturn(Optional.of(trancaExistente));

        assertThrows(InvalidActionException.class, () -> service.deletar(idExistente));

        verify(repository, times(0)).deletar(idExistente);
    }

    @Test
    void integrarNaRedeSucesso() {
        IntegrarTrancaRequest request = new IntegrarTrancaRequest();
        request.setIdTranca(1);
        request.setIdTotem(1);

        Tranca tranca = exemploTeste();
        tranca.setId(1);
        tranca.setStatus(StatusTranca.NOVA);
        tranca.setIdTotem(null);

        when(repository.buscar(1)).thenReturn(Optional.of(tranca));
        when(totemService.buscar(1)).thenReturn(new Totem());

        service.integrarNaRede(request);

        assertEquals(StatusTranca.LIVRE, tranca.getStatus());
        assertEquals(1, tranca.getIdTotem());
        verify(repository, times(1)).salvar(tranca);
    }

    @Test
    void integrarNaRedeErroStatusInvalido() {
        IntegrarTrancaRequest request = new IntegrarTrancaRequest();
        request.setIdTranca(1);
        request.setIdTotem(1);

        Tranca tranca = exemploTeste();
        tranca.setId(1);
        tranca.setStatus(StatusTranca.LIVRE);

        when(repository.buscar(1)).thenReturn(Optional.of(tranca));
        when(totemService.buscar(1)).thenReturn(new Totem());

        assertThrows(InvalidActionException.class, () -> service.integrarNaRede(request));
    }

    @Test
    void retirarDaRedeSucesso() {
        RetirarTrancaRequest request = new RetirarTrancaRequest();
        request.setIdTranca(1);
        request.setIdTotem(1);
        request.setStatusAcaoReparador("EM_REPARO");

        Tranca tranca = exemploTeste();
        tranca.setId(1);
        tranca.setIdTotem(1);
        tranca.setBicicleta(null);

        when(repository.buscar(1)).thenReturn(Optional.of(tranca));

        service.retirarDaRede(request);

        assertEquals(StatusTranca.EM_REPARO, tranca.getStatus());
        assertNull(tranca.getIdTotem());
        verify(repository, times(1)).salvar(tranca);
    }

    @Test
    void retirarDaRedeErroComBicicleta() {
        RetirarTrancaRequest request = new RetirarTrancaRequest();
        request.setIdTranca(1);
        request.setIdTotem(1);
        request.setStatusAcaoReparador("APOSENTADA");

        Tranca tranca = exemploTeste();
        tranca.setId(1);
        tranca.setIdTotem(1);
        tranca.setBicicleta(100);

        when(repository.buscar(1)).thenReturn(Optional.of(tranca));

        assertThrows(InvalidActionException.class, () -> service.retirarDaRede(request));
    }

    @Test
    void retirarDaRedeErroTotemIncorreto() {
        RetirarTrancaRequest request = new RetirarTrancaRequest();
        request.setIdTranca(1);
        request.setIdTotem(2);
        request.setStatusAcaoReparador("APOSENTADA");

        Tranca tranca = exemploTeste();
        tranca.setId(1);
        tranca.setIdTotem(1);
        tranca.setBicicleta(null);

        when(repository.buscar(1)).thenReturn(Optional.of(tranca));

        assertThrows(InvalidActionException.class, () -> service.retirarDaRede(request));
    }

    @Test
    void getBicicletaSucesso() {
        Tranca tranca = exemploTeste();
        tranca.setId(1);
        tranca.setBicicleta(100);

        Bicicleta bicicleta = new Bicicleta();
        bicicleta.setId(100);

        when(repository.buscar(1)).thenReturn(Optional.of(tranca));
        when(bicicletaRepository.buscar(100)).thenReturn(Optional.of(bicicleta));

        Bicicleta resultado = service.getBicicleta(1);

        assertNotNull(resultado);
        assertEquals(100, resultado.getId());
    }

    @Test
    void getBicicletaErroSemBicicleta() {
        Tranca tranca = exemploTeste();
        tranca.setId(1);
        tranca.setBicicleta(null);

        when(repository.buscar(1)).thenReturn(Optional.of(tranca));

        assertThrows(NotFoundException.class, () -> service.getBicicleta(1));
    }

    @Test
    void trancarSucesso() {
        TrancaAcaoRequest request = new TrancaAcaoRequest();
        request.setBicicleta(100);

        Tranca tranca = exemploTeste();
        tranca.setId(1);
        tranca.setStatus(StatusTranca.LIVRE);

        Bicicleta bicicleta = new Bicicleta();
        bicicleta.setId(100);
        bicicleta.setStatus(StatusBicicleta.EM_USO);

        when(repository.buscar(1)).thenReturn(Optional.of(tranca));
        when(bicicletaRepository.buscar(100)).thenReturn(Optional.of(bicicleta));

        service.trancar(1, request);

        assertEquals(StatusTranca.OCUPADA, tranca.getStatus());
        assertEquals(100, tranca.getBicicleta());
        assertEquals(StatusBicicleta.DISPONIVEL, bicicleta.getStatus());
        verify(repository, times(1)).salvar(tranca);
        verify(bicicletaRepository, times(1)).salvar(bicicleta);
    }

    @Test
    void trancarErroTrancaNaoLivre() {
        TrancaAcaoRequest request = new TrancaAcaoRequest();
        request.setBicicleta(100);

        Tranca tranca = exemploTeste();
        tranca.setId(1);
        tranca.setStatus(StatusTranca.OCUPADA);

        when(repository.buscar(1)).thenReturn(Optional.of(tranca));

        assertThrows(InvalidActionException.class, () -> service.trancar(1, request));
    }

    @Test
    void trancarErroBicicletaNaoEmUso() {
        TrancaAcaoRequest request = new TrancaAcaoRequest();
        request.setBicicleta(100);

        Tranca tranca = exemploTeste();
        tranca.setId(1);
        tranca.setStatus(StatusTranca.LIVRE);

        Bicicleta bicicleta = new Bicicleta();
        bicicleta.setId(100);
        bicicleta.setStatus(StatusBicicleta.DISPONIVEL);

        when(repository.buscar(1)).thenReturn(Optional.of(tranca));
        when(bicicletaRepository.buscar(100)).thenReturn(Optional.of(bicicleta));

        assertThrows(InvalidActionException.class, () -> service.trancar(1, request));
    }

    @Test
    void destrancarSucesso() {
        Tranca tranca = exemploTeste();
        tranca.setId(1);
        tranca.setStatus(StatusTranca.OCUPADA);
        tranca.setBicicleta(100);

        Bicicleta bicicleta = new Bicicleta();
        bicicleta.setId(100);
        bicicleta.setStatus(StatusBicicleta.DISPONIVEL);

        when(repository.buscar(1)).thenReturn(Optional.of(tranca));
        when(bicicletaRepository.buscar(100)).thenReturn(Optional.of(bicicleta));

        service.destrancar(1, null);

        assertEquals(StatusTranca.LIVRE, tranca.getStatus());
        assertNull(tranca.getBicicleta());
        assertEquals(StatusBicicleta.EM_USO, bicicleta.getStatus());
        verify(repository, times(1)).salvar(tranca);
        verify(bicicletaRepository, times(1)).salvar(bicicleta);
    }

    @Test
    void destrancarErroTrancaNaoOcupada() {
        Tranca tranca = exemploTeste();
        tranca.setId(1);
        tranca.setStatus(StatusTranca.LIVRE);

        when(repository.buscar(1)).thenReturn(Optional.of(tranca));

        assertThrows(InvalidActionException.class, () -> service.destrancar(1, null));
    }

    @Test
    void alterarStatusSucesso() {
        Tranca tranca = exemploTeste();
        tranca.setId(1);
        tranca.setStatus(StatusTranca.LIVRE);

        when(repository.buscar(1)).thenReturn(Optional.of(tranca));

        service.alterarStatus(1, "TRANCAR");
        assertEquals(StatusTranca.OCUPADA, tranca.getStatus());

        service.alterarStatus(1, "DESTRANCAR");
        assertEquals(StatusTranca.LIVRE, tranca.getStatus());
    }

    @Test
    void alterarStatusErro() {
        Tranca tranca = exemploTeste();
        tranca.setId(1);

        when(repository.buscar(1)).thenReturn(Optional.of(tranca));

        assertThrows(InvalidActionException.class, () -> service.alterarStatus(1, "ACAO_INVALIDA"));
    }
}