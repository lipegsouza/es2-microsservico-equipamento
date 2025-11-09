package com.microsservico.equipamento.service;

import com.microsservico.equipamento.domain.Bicicleta;
import com.microsservico.equipamento.domain.StatusBicicleta;
import com.microsservico.equipamento.exception.InvalidActionException;
import com.microsservico.equipamento.exception.NotFoundException;
import com.microsservico.equipamento.repository.BicicletaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Service
public class BicicletaService {

    private final BicicletaRepository repository;
    private static final AtomicInteger numeroCounter = new AtomicInteger(1);

    private void validar(Bicicleta bicicleta) {
        if (bicicleta.getMarca() == null || bicicleta.getMarca().isBlank() ||
                bicicleta.getModelo() == null || bicicleta.getModelo().isBlank() ||
                bicicleta.getAno() == null || bicicleta.getAno().isBlank()) {
            throw new InvalidActionException("Dados inválidos. Marca, Modelo e Ano são obrigatórios.");
        }
    }

    public int gerarNumero() {
        return numeroCounter.getAndIncrement();
    }

    public Bicicleta cadastrar(Bicicleta bicicleta) {
        validar(bicicleta);
        bicicleta.setNumero(gerarNumero());
        bicicleta.setStatus(StatusBicicleta.NOVA);
        return repository.salvar(bicicleta);
    }

    public Bicicleta buscar(int id) {
        return repository.buscar(id)
                .orElseThrow(() -> new NotFoundException("Bicicleta não encontrada com o ID: " + id));
    }

    public List<Bicicleta> listar() {
        return repository.listar();
    }

    public Bicicleta editar(int id, Bicicleta dadosNovos) {
        Bicicleta bicicletaExistente = buscar(id);

        validar(dadosNovos);

        bicicletaExistente.setMarca(dadosNovos.getMarca());
        bicicletaExistente.setModelo(dadosNovos.getModelo());
        bicicletaExistente.setAno(dadosNovos.getAno());

        return repository.salvar(bicicletaExistente);
    }

    public void deletar(int id) {
        Bicicleta bicicleta = buscar(id);

        if (bicicleta.getStatus() != StatusBicicleta.APOSENTADA) {
            throw new InvalidActionException("Ação inválida. Apenas bicicletas com status APOSENTADA podem ser excluídas.");
        }

        repository.deletar(bicicleta.getId());
    }
}