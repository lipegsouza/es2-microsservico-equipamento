package com.microsservico.equipamento.services;

import com.microsservico.equipamento.domain.Bicicleta;
import com.microsservico.equipamento.domain.StatusBicicleta;
import com.microsservico.equipamento.exception.InvalidActionException;
import com.microsservico.equipamento.exception.NotFoundException;
import com.microsservico.equipamento.repository.BicicletaRepository;
import com.microsservico.equipamento.service.BicicletaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
    class BicicletaServiceTest {

    @Mock
    private BicicletaRepository repository;

    @InjectMocks
    private BicicletaService service;

    @Test
    public void cadastrarSucesso() {

        Bicicleta bicicletaDeEntrada = new Bicicleta();
        bicicletaDeEntrada.setMarca("Caloi");
        bicicletaDeEntrada.setModelo("Andes");
        bicicletaDeEntrada.setAno("2023");
        bicicletaDeEntrada.setNumero(123);

        Bicicleta bicicletaSalva = new Bicicleta();
        bicicletaSalva.setId(1);
        bicicletaSalva.setMarca("Caloi");
        bicicletaSalva.setModelo("Andes");
        bicicletaSalva.setAno("2023");
        bicicletaSalva.setNumero(123);
        bicicletaSalva.setStatus(StatusBicicleta.NOVA);

        when(repository.salvar(any(Bicicleta.class))).thenReturn(bicicletaSalva);

        Bicicleta resultado = service.cadastrar(bicicletaDeEntrada);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals(StatusBicicleta.NOVA, resultado.getStatus());
        verify(repository, times(1)).salvar(any(Bicicleta.class));
    }

    @Test
    void cadastrarErro() {
        Bicicleta bicicletaInvalida = new Bicicleta();
        bicicletaInvalida.setModelo("Modelo");
        bicicletaInvalida.setAno("2023");
        bicicletaInvalida.setNumero(123);

        assertThrows(InvalidActionException.class, () -> {
            service.cadastrar(bicicletaInvalida);
        });

        verify(repository, times(0)).salvar(any(Bicicleta.class));
    }

    @Test
    void buscarSucesso() {
        Bicicleta bicicleta = new Bicicleta();
        bicicleta.setId(42);
        bicicleta.setMarca("Monark");

        when(repository.buscar(42)).thenReturn(Optional.of(bicicleta));

        Bicicleta resultado = service.buscar(42);

        assertNotNull(resultado);
        assertEquals(42, resultado.getId());
        verify(repository, times(1)).buscar(42);
    }

    @Test
    void buscarErro() {
        int idInexistente = 99;

        when(repository.buscar(idInexistente)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            service.buscar(idInexistente);
        });

        assertEquals("Bicicleta não encontrada com o ID: 99", exception.getMessage());
        verify(repository, times(1)).buscar(idInexistente);
    }

    @Test
    void listarSucesso() {
        List<Bicicleta> listaDeBicicletas = Arrays.asList(new Bicicleta(), new Bicicleta());
        when(repository.listar()).thenReturn(listaDeBicicletas);

        List<Bicicleta> resultado = service.listar();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(repository, times(1)).listar();
    }

    @Test
    void editarSucesso() {
        int idExistente = 1;

        Bicicleta dadosNovos = new Bicicleta();
        dadosNovos.setMarca("Caloi-Nova");
        dadosNovos.setModelo("Andes-Novo");
        dadosNovos.setAno("2024");
        dadosNovos.setNumero(999);

        Bicicleta bicicletaExistente = new Bicicleta();
        bicicletaExistente.setId(idExistente);
        bicicletaExistente.setMarca("Caloi-Velha");
        bicicletaExistente.setModelo("Andes-Velho");
        bicicletaExistente.setAno("2023");
        bicicletaExistente.setNumero(123);
        bicicletaExistente.setStatus(StatusBicicleta.NOVA);

        when(repository.buscar(idExistente)).thenReturn(Optional.of(bicicletaExistente));
        when(repository.salvar(any(Bicicleta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bicicleta resultado = service.editar(idExistente, dadosNovos);

        assertNotNull(resultado);
        assertEquals("Caloi-Nova", resultado.getMarca());
        assertEquals("Andes-Novo", resultado.getModelo());

        assertEquals(123, resultado.getNumero());

        verify(repository, times(1)).buscar(idExistente);
        verify(repository, times(1)).salvar(bicicletaExistente);
    }

    @Test
    void editarErro() {
        int idInexistente = 99;

        Bicicleta dadosNovos = new Bicicleta();
        dadosNovos.setMarca("Marca");
        dadosNovos.setModelo("Modelo");
        dadosNovos.setAno("2020");
        dadosNovos.setNumero(111);

        when(repository.buscar(idInexistente)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            service.editar(idInexistente, dadosNovos);
        });
        verify(repository, times(0)).salvar(any(Bicicleta.class));
    }

    @Test
    void deletarSucesso() {
        int idExistente = 1;
        Bicicleta bicicletaExistente = new Bicicleta();
        bicicletaExistente.setId(idExistente);
        bicicletaExistente.setStatus(StatusBicicleta.APOSENTADA);

        when(repository.buscar(idExistente)).thenReturn(Optional.of(bicicletaExistente));

        service.deletar(idExistente);

        verify(repository, times(1)).buscar(idExistente);
        verify(repository, times(1)).deletar(idExistente);
    }

    @Test
    void deletarErro() {
        int idExistente = 1;
        Bicicleta bicicletaExistente = new Bicicleta();
        bicicletaExistente.setId(idExistente);
        bicicletaExistente.setStatus(StatusBicicleta.NOVA);

        when(repository.buscar(idExistente)).thenReturn(Optional.of(bicicletaExistente));

        InvalidActionException exception = assertThrows(InvalidActionException.class, () -> {
            service.deletar(idExistente);
        });

        assertEquals("Ação inválida. Apenas bicicletas com status APOSENTADA podem ser excluídas.", exception.getMessage());
        verify(repository, times(1)).buscar(idExistente);
        verify(repository, times(0)).deletar(idExistente);
    }
}