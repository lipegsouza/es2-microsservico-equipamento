package com.microsservico.equipamento.repository;

import com.microsservico.equipamento.domain.Totem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TotemRepositoryTest {

    @InjectMocks
    private TotemRepository repository;

    @BeforeEach
    void setUp() {
        repository.restaurar();
    }

    private Totem criarTotem(String localizacao) {
        Totem t = new Totem();
        t.setLocalizacao(localizacao);
        t.setDescricao("Desc");
        return repository.salvar(t);
    }

    @Test
    void testSalvarTotemNovo() {
        Totem t1 = criarTotem("Local A");
        Totem t2 = criarTotem("Local B");

        assertEquals(1, t1.getId());
        assertEquals(2, t2.getId());
    }

    @Test
    void testSalvarTotemExistente() {
        Totem t1 = criarTotem("Local A");
        assertEquals(1, t1.getId());
        assertEquals("Local A", t1.getLocalizacao());

        t1.setLocalizacao("Local A-Editado");
        Totem t1Editado = repository.salvar(t1);

        assertEquals(1, t1Editado.getId());
        assertEquals("Local A-Editado", t1Editado.getLocalizacao());
    }

    @Test
    void testBuscar() {
        criarTotem("Local A");
        Optional<Totem> t1 = repository.buscar(1);
        assertTrue(t1.isPresent());

        Optional<Totem> t99 = repository.buscar(99);
        assertTrue(t99.isEmpty());
    }

    @Test
    void testListar() {
        criarTotem("Local A");
        criarTotem("Local B");

        List<Totem> totens = repository.listar();
        assertEquals(2, totens.size());
    }

    @Test
    void testDeletar() {
        criarTotem("Local A");
        assertEquals(1, repository.listar().size());

        repository.deletar(1);
        assertEquals(0, repository.listar().size());
        assertTrue(repository.buscar(1).isEmpty());
    }

    @Test
    void testRestaurar() {
        criarTotem("Local A");
        repository.restaurar();
        assertEquals(0, repository.listar().size());

        Totem tAposRestaurar = criarTotem("Outro");
        assertEquals(1, tAposRestaurar.getId());
    }
}