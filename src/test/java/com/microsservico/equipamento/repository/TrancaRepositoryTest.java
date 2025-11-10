package com.microsservico.equipamento.repository;

import com.microsservico.equipamento.domain.Tranca;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TrancaRepositoryTest {

    @InjectMocks
    private TrancaRepository repository;

    @BeforeEach
    void setUp() {
        repository.restaurar();
    }

    private Tranca criarTranca(int numero, Integer idTotem) {
        Tranca t = new Tranca();
        t.setNumero(numero);
        t.setIdTotem(idTotem);
        t.setModelo("M");
        t.setAnoDeFabricacao("2023");
        return repository.salvar(t);
    }

    @Test
    void testSalvarTrancaNova() {
        Tranca t1 = criarTranca(101, 1);
        Tranca t2 = criarTranca(102, 1);

        assertEquals(1, t1.getId());
        assertEquals(2, t2.getId());
    }

    @Test
    void testSalvarTrancaExistente() {
        Tranca t1 = criarTranca(101, 1);
        assertEquals(1, t1.getId());
        assertEquals(101, t1.getNumero());

        t1.setNumero(201);
        Tranca t1Editada = repository.salvar(t1);

        assertEquals(1, t1Editada.getId());
        assertEquals(201, t1Editada.getNumero());
    }

    @Test
    void testBuscar() {
        criarTranca(101, 1);
        Optional<Tranca> t1 = repository.buscar(1);
        assertTrue(t1.isPresent());

        Optional<Tranca> t99 = repository.buscar(99);
        assertTrue(t99.isEmpty());
    }

    @Test
    void testListar() {
        criarTranca(101, 1);
        criarTranca(102, 1);

        List<Tranca> trancas = repository.listar();
        assertEquals(2, trancas.size());
    }

    @Test
    void testDeletar() {
        criarTranca(101, 1);
        assertEquals(1, repository.listar().size());

        repository.deletar(1);
        assertEquals(0, repository.listar().size());
        assertTrue(repository.buscar(1).isEmpty());
    }

    @Test
    void testRestaurar() {
        criarTranca(101, 1);
        repository.restaurar();
        assertEquals(0, repository.listar().size());

        Tranca tAposRestaurar = criarTranca(201, 2);
        assertEquals(1, tAposRestaurar.getId());
    }

    @Test
    void testBuscarEmTotem() {
        Tranca t1_T5 = criarTranca(101, 5);
        Tranca t2_T5 = criarTranca(102, 5);
        Tranca t3_T10 = criarTranca(103, 10);

        List<Tranca> trancasTotem5 = repository.buscarEmTotem(5);
        assertEquals(2, trancasTotem5.size());
        assertTrue(trancasTotem5.contains(t1_T5));
        assertTrue(trancasTotem5.contains(t2_T5));

        List<Tranca> trancasTotem10 = repository.buscarEmTotem(10);
        assertEquals(1, trancasTotem10.size());
        assertTrue(trancasTotem10.contains(t3_T10));

        List<Tranca> trancasTotem99 = repository.buscarEmTotem(99);
        assertEquals(0, trancasTotem99.size());
    }
}