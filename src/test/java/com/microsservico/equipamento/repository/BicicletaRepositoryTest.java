package com.microsservico.equipamento.repository;

import com.microsservico.equipamento.domain.Bicicleta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BicicletaRepositoryTest {

    @InjectMocks
    private BicicletaRepository repository;

    @BeforeEach
    void setUp() {
        repository.restaurar();
    }

    private Bicicleta criarBicicleta(String marca) {
        Bicicleta b = new Bicicleta();
        b.setMarca(marca);
        b.setModelo("Modelo");
        b.setAno("2023");
        return repository.salvar(b);
    }

    @Test
    void testSalvarBicicletaNova() {
        Bicicleta b1 = criarBicicleta("Caloi");
        Bicicleta b2 = criarBicicleta("Monark");

        assertEquals(1, b1.getId());
        assertEquals(2, b2.getId());
    }

    @Test
    void testSalvarBicicletaExistente() {
        Bicicleta b1 = criarBicicleta("Caloi");
        assertEquals(1, b1.getId());
        assertEquals("Caloi", b1.getMarca());

        b1.setMarca("Caloi-Editada");
        Bicicleta b1Editada = repository.salvar(b1);

        assertEquals(1, b1Editada.getId());
        assertEquals("Caloi-Editada", b1Editada.getMarca());
    }

    @Test
    void testBuscar() {
        criarBicicleta("Caloi");
        Optional<Bicicleta> b1 = repository.buscar(1);
        assertTrue(b1.isPresent());

        Optional<Bicicleta> b99 = repository.buscar(99);
        assertTrue(b99.isEmpty());
    }

    @Test
    void testListar() {
        criarBicicleta("Caloi");
        criarBicicleta("Monark");

        List<Bicicleta> bicicletas = repository.listar();
        assertEquals(2, bicicletas.size());
    }

    @Test
    void testDeletar() {
        criarBicicleta("Caloi");
        assertEquals(1, repository.listar().size());

        repository.deletar(1);
        assertEquals(0, repository.listar().size());
        assertTrue(repository.buscar(1).isEmpty());
    }

    @Test
    void testRestaurar() {
        criarBicicleta("Caloi");
        repository.restaurar();
        assertEquals(0, repository.listar().size());

        Bicicleta bAposRestaurar = criarBicicleta("Outra");
        assertEquals(1, bAposRestaurar.getId());
    }

    @Test
    void testBuscarEmTrancasDeTotem() {
        Bicicleta b1 = criarBicicleta("B1");
        Bicicleta b2 = criarBicicleta("B2");
        Bicicleta b3 = criarBicicleta("B3");

        List<Integer> ids = List.of(b1.getId(), b3.getId());
        List<Bicicleta> resultado = repository.buscarEmTrancasDeTotem(ids);

        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(b1));
        assertFalse(resultado.contains(b2));
        assertTrue(resultado.contains(b3));
    }
}