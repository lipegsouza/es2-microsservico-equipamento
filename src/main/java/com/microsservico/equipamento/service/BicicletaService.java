package com.microsservico.equipamento.service;

import com.microsservico.equipamento.domain.Bicicleta;
import com.microsservico.equipamento.domain.StatusBicicleta;
import com.microsservico.equipamento.domain.StatusTranca;
import com.microsservico.equipamento.domain.Tranca;
import com.microsservico.equipamento.dto.request.IntegrarBicicletaRequest;
import com.microsservico.equipamento.dto.request.RetirarBicicletaRequest;
import com.microsservico.equipamento.exception.InvalidActionException;
import com.microsservico.equipamento.exception.NotFoundException;
import com.microsservico.equipamento.repository.BicicletaRepository;
import com.microsservico.equipamento.repository.TrancaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Service
public class BicicletaService {

    private final BicicletaRepository repository;
    private final TrancaService trancaService;
    private final TrancaRepository trancaRepository;
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

    public void restaurarNumero() {
        numeroCounter.set(1);
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

        boolean emTranca = trancaRepository.listar().stream()
                .anyMatch(tranca -> Objects.equals(tranca.getBicicleta(), id));
        if (emTranca) {
            throw new InvalidActionException("Ação inválida. Bicicleta ainda está associada a uma tranca.");
        }

        repository.deletar(bicicleta.getId());
    }

    public void integrarNaRede(IntegrarBicicletaRequest request) {
        Bicicleta bicicleta = buscar(request.getIdBicicleta());
        Tranca tranca = trancaService.buscar(request.getIdTranca());

        if (tranca.getStatus() != StatusTranca.LIVRE) {
            throw new InvalidActionException("Tranca não está livre.");
        }

        if (bicicleta.getStatus() != StatusBicicleta.NOVA && bicicleta.getStatus() != StatusBicicleta.EM_REPARO) {
            throw new InvalidActionException("Bicicleta não está com status NOVA ou EM_REPARO.");
        }

        if(tranca.getBicicleta() != null) {
            throw new InvalidActionException("Tranca já possui uma bicicleta.");
        }

        bicicleta.setStatus(StatusBicicleta.DISPONIVEL);
        tranca.setStatus(StatusTranca.OCUPADA);
        tranca.setBicicleta(bicicleta.getId());

        repository.salvar(bicicleta);
        trancaService.salvar(tranca);
    }

    public void retirarDaRede(RetirarBicicletaRequest request) {
        Bicicleta bicicleta = buscar(request.getIdBicicleta());
        Tranca tranca = trancaService.buscar(request.getIdTranca());

        StatusBicicleta novoStatus;
        try {
            novoStatus = StatusBicicleta.valueOf(request.getStatusAcaoReparador().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidActionException("Status de ação inválido: " + request.getStatusAcaoReparador());
        }

        if (novoStatus != StatusBicicleta.EM_REPARO && novoStatus != StatusBicicleta.APOSENTADA) {
            throw new InvalidActionException("Ação de reparador deve ser EM_REPARO ou APOSENTADA.");
        }

        if (bicicleta.getStatus() != StatusBicicleta.REPARO_SOLICITADO) {
            throw new InvalidActionException("Bicicleta não está com status REPARO_SOLICITADO.");
        }

        if (tranca.getStatus() != StatusTranca.OCUPADA) {
            throw new InvalidActionException("Tranca não está ocupada.");
        }

        if (tranca.getBicicleta() == null || !Objects.equals(tranca.getBicicleta(), bicicleta.getId())) {
            throw new InvalidActionException("Bicicleta não corresponde à bicicleta na tranca.");
        }

        bicicleta.setStatus(novoStatus);
        tranca.setStatus(StatusTranca.LIVRE);
        tranca.setBicicleta(null);

        repository.salvar(bicicleta);
        trancaService.salvar(tranca);
    }

    public Bicicleta alterarStatus(int idBicicleta, String acao) {
        Bicicleta bicicleta = buscar(idBicicleta);

        StatusBicicleta novoStatus;
        try {
            novoStatus = StatusBicicleta.valueOf(acao.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidActionException("Ação de status inválida: " + acao);
        }

        if (bicicleta.getStatus() == StatusBicicleta.EM_USO) {
            throw new InvalidActionException("Bicicleta está EM_USO e não pode ter status alterado manualmente.");
        }

        bicicleta.setStatus(novoStatus);
        return repository.salvar(bicicleta);
    }
}