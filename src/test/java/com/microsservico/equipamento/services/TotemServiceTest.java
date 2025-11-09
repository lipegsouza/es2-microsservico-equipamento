package com.microsservico.equipamento.services;

import com.microsservico.equipamento.domain.Bicicleta;
import com.microsservico.equipamento.domain.Totem;
import com.microsservico.equipamento.domain.Tranca;
import com.microsservico.equipamento.exception.InvalidActionException;
import com.microsservico.equipamento.exception.NotFoundException;
import com.microsservico.equipamento.repository.BicicletaRepository;
import com.microsservico.equipamento.repository.TotemRepository;
import com.microsservico.equipamento.repository.TrancaRepository;
import com.microsservico.equipamento.service.TotemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
    class TotemServiceTest {

    @Mock
    private TotemRepository repository;

    @Mock
    private TrancaRepository trancaRepository;

    @Mock
    private BicicletaRepository bicicletaRepository;

    @InjectMocks
    private TotemService service;

    private Totem exemploTeste() {
        Totem totem = new Totem();
        totem.setLocalizacao("Rua Teste");
        totem.setDescricao("Totem de Teste");
        return totem;
    }

    @Test
    void cadastrarSucesso() {
        Totem totemEntrada = exemploTeste();
        when(repository.salvar(any(Totem.class))).thenAnswer(inv -> inv.getArgument(0));

        Totem resultado = service.cadastrar(totemEntrada);

        assertNotNull(resultado);
        assertEquals("Rua Teste", resultado.getLocalizacao());
        verify(repository, times(1)).salvar(totemEntrada);
    }

    @Test
    void cadastrarErro() {
        Totem totemInvalido = exemploTeste();
        totemInvalido.setLocalizacao("");

        assertThrows(InvalidActionException.class, () -> {
            service.cadastrar(totemInvalido);
        });
        verify(repository, times(0)).salvar(any(Totem.class));
    }

    @Test
    void buscarSucesso() {
        Totem totem = exemploTeste();
        totem.setId(1);
        when(repository.buscar(1)).thenReturn(Optional.of(totem));

        Totem resultado = service.buscar(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        verify(repository, times(1)).buscar(1);
    }

    @Test
    void buscarErro() {
        when(repository.buscar(99)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            service.buscar(99);
        });
    }

    @Test
    void editarSucesso() {
        int idExistente = 1;
        Totem dadosNovos = new Totem();
        dadosNovos.setLocalizacao("Nova Rua");
        dadosNovos.setDescricao("Nova Descricao");

        Totem totemExistente = exemploTeste();
        totemExistente.setId(idExistente);

        when(repository.buscar(idExistente)).thenReturn(Optional.of(totemExistente));
        when(repository.salvar(any(Totem.class))).thenAnswer(inv -> inv.getArgument(0));

        Totem resultado = service.editar(idExistente, dadosNovos);

        assertNotNull(resultado);
        assertEquals(idExistente, resultado.getId());
        assertEquals("Nova Rua", resultado.getLocalizacao());
        assertEquals("Nova Descricao", resultado.getDescricao());
        verify(repository, times(1)).salvar(totemExistente);
    }

    @Test
    void deletarSucesso() {
        int idExistente = 1;
        Totem totemExistente = exemploTeste();
        totemExistente.setId(idExistente);

        when(repository.buscar(idExistente)).thenReturn(Optional.of(totemExistente));
        when(trancaRepository.buscarEmTotem(idExistente)).thenReturn(List.of());

        service.deletar(idExistente);

        verify(repository, times(1)).buscar(idExistente);
        verify(trancaRepository, times(1)).buscarEmTotem(idExistente);
        verify(repository, times(1)).deletar(idExistente);
    }

    @Test
    void deletarErro() {
        int idExistente = 1;
        Totem totemExistente = exemploTeste();
        totemExistente.setId(idExistente);

        when(repository.buscar(idExistente)).thenReturn(Optional.of(totemExistente));
        when(trancaRepository.buscarEmTotem(idExistente)).thenReturn(List.of(new Tranca()));

        assertThrows(InvalidActionException.class, () -> {
            service.deletar(idExistente);
        });

        verify(repository, times(0)).deletar(anyInt());
    }

    @Test
    void listarTrancasSucesso() {
        int idTotem = 1;
        when(repository.buscar(idTotem)).thenReturn(Optional.of(new Totem()));
        when(trancaRepository.buscarEmTotem(idTotem)).thenReturn(List.of(new Tranca(), new Tranca()));

        List<Tranca> resultado = service.listarTrancas(idTotem);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(trancaRepository, times(1)).buscarEmTotem(idTotem);
    }

    @Test
    void listarBicicletasSucesso() {
        int idTotem = 1;

        Tranca tranca1 = new Tranca();
        tranca1.setBicicleta(10);
        Tranca tranca2 = new Tranca();
        tranca2.setBicicleta(20);
        Tranca tranca3 = new Tranca();
        tranca3.setBicicleta(null);

        List<Tranca> trancas = List.of(tranca1, tranca2, tranca3);
        List<Integer> idsBicicletas = List.of(10, 20);

        Bicicleta bicicleta1 = new Bicicleta();
        bicicleta1.setId(10);
        Bicicleta bicicleta2 = new Bicicleta();
        bicicleta2.setId(20);
        List<Bicicleta> bicicletas = List.of(bicicleta1, bicicleta2);

        when(repository.buscar(idTotem)).thenReturn(Optional.of(new Totem()));
        when(trancaRepository.buscarEmTotem(idTotem)).thenReturn(trancas);
        when(bicicletaRepository.buscarEmTrancasDeTotem(idsBicicletas)).thenReturn(bicicletas);

        List<Bicicleta> resultado = service.listarBicicletas(idTotem);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(10, resultado.get(0).getId());
        assertEquals(20, resultado.get(1).getId());
        verify(trancaRepository, times(1)).buscarEmTotem(idTotem);
        verify(bicicletaRepository, times(1)).buscarEmTrancasDeTotem(idsBicicletas);
    }
}