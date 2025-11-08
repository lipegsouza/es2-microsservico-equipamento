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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrancaServiceTest {

    @Mock
    private TrancaRepository repository;

    @InjectMocks
    private TrancaService service;

    private Tranca exemploTeste() {
        Tranca tranca = new Tranca();
        tranca.setNumero(10);
        tranca.setModelo("Modelo X");
        tranca.setAnoDeFabricacao("2023");
        tranca.setLocalizacao("Totem 1");
        return tranca;
    }

    @Test
    public void cadastrarSucesso() {
        Tranca trancaEntrada = exemploTeste();

        when(repository.salvar(any(Tranca.class))).thenAnswer(inv -> inv.getArgument(0));

        Tranca resultado = service.cadastrar(trancaEntrada);

        assertNotNull(resultado);
        assertEquals(StatusTranca.NOVA, resultado.getStatus());
        verify(repository, times(1)).salvar(trancaEntrada);
    }

    @Test
    public void cadastrarErro() {
        Tranca trancaInvalida = exemploTeste();
        trancaInvalida.setNumero(0);

        assertThrows(InvalidActionException.class, () -> {
            service.cadastrar(trancaInvalida);
        });
        verify(repository, times(0)).salvar(any(Tranca.class));
    }

    @Test
    public void cadastrarErro2() {
        Tranca trancaInvalida = exemploTeste();
        trancaInvalida.setModelo("");

        assertThrows(InvalidActionException.class, () -> {
            service.cadastrar(trancaInvalida);
        });
        verify(repository, times(0)).salvar(any(Tranca.class));
    }

    @Test
    public void buscarSucesso() {
        Tranca tranca = exemploTeste();
        tranca.setId(1);
        when(repository.buscar(1)).thenReturn(Optional.of(tranca));

        Tranca resultado = service.buscar(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        verify(repository, times(1)).buscar(1);
    }

    @Test
    public void buscarErro() {
        when(repository.buscar(99)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            service.buscar(99);
        });
    }

    @Test
    public void editarSucesso() {
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
    public void deletarSucesso() {
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
    public void deletarErro() {
        int idExistente = 1;
        Tranca trancaExistente = exemploTeste();
        trancaExistente.setId(idExistente);
        trancaExistente.setBicicleta(100);

        when(repository.buscar(idExistente)).thenReturn(Optional.of(trancaExistente));

        assertThrows(InvalidActionException.class, () -> {
            service.deletar(idExistente);
        });

        verify(repository, times(0)).deletar(idExistente);
    }
}