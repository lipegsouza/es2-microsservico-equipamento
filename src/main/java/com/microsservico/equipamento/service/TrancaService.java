package com.microsservico.equipamento.service;

import com.microsservico.equipamento.domain.StatusTranca;
import com.microsservico.equipamento.domain.Tranca;
import com.microsservico.equipamento.exception.InvalidActionException;
import com.microsservico.equipamento.exception.NotFoundException;
import com.microsservico.equipamento.repository.TrancaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrancaService {

    @Autowired
    private TrancaRepository repository;

    private void validar(Tranca tranca) {
        if (tranca.getModelo() == null || tranca.getModelo().isBlank() ||
                tranca.getAnoDeFabricacao() == null || tranca.getAnoDeFabricacao().isBlank()) {
            throw new InvalidActionException("Dados inválidos. Modelo e Ano de Fabricação são obrigatórios.");
        }
        if (tranca.getNumero() == 0) {
            throw new InvalidActionException("Dados inválidos. O número da tranca é obrigatório.");
        }
    }

    public Tranca cadastrar(Tranca tranca) {
        validar(tranca);

        tranca.setStatus(StatusTranca.NOVA);

        return repository.salvar(tranca);
    }

    public Tranca buscar(int id) {
        return repository.buscar(id)
                .orElseThrow(() -> new NotFoundException("Tranca não encontrada com o ID: " + id));
    }

    public List<Tranca> listar() {
        return repository.listar();
    }

    public Tranca editar(int id, Tranca dadosNovos) {
        Tranca trancaExistente = buscar(id);

        validar(dadosNovos);

        trancaExistente.setLocalizacao(dadosNovos.getLocalizacao());
        trancaExistente.setAnoDeFabricacao(dadosNovos.getAnoDeFabricacao());
        trancaExistente.setModelo(dadosNovos.getModelo());

        return repository.salvar(trancaExistente);
    }

    public void deletar(int id) {
        Tranca tranca = buscar(id);

        if (tranca.getBicicleta() != null) {
            throw new InvalidActionException("Ação inválida. Apenas trancas sem bicicletas podem ser excluídas.");
        }

        repository.deletar(tranca.getId());
    }
}