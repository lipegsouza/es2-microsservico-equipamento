package com.microsservico.equipamento.service;

import com.microsservico.equipamento.client.AluguelClient;
import com.microsservico.equipamento.client.ExternoClient;
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
    private final AluguelClient aluguelClient;
    private final ExternoClient externoClient;

    private static final AtomicInteger numeroCounter = new AtomicInteger(1);

    // UC10-R2: Validar dados obrigatórios
    private void validar(Bicicleta bicicleta) {
        if (bicicleta.getMarca() == null || bicicleta.getMarca().isBlank() ||
                bicicleta.getModelo() == null || bicicleta.getModelo().isBlank() ||
                bicicleta.getAno() == null || bicicleta.getAno().isBlank()) {
            throw new InvalidActionException("Dados inválidos. Marca, Modelo e Ano são obrigatórios.");
        }
    }

    // UC10-R5: Numero deve ser gerado pelo sistema
    public int gerarNumero() {
        return numeroCounter.getAndIncrement();
    }

    public void restaurarNumero() {
        numeroCounter.set(1);
    }

    public Bicicleta cadastrar(Bicicleta bicicleta) {
        validar(bicicleta);
        bicicleta.setNumero(gerarNumero());
        bicicleta.setStatus(StatusBicicleta.NOVA); // UC10-R1: Status será NOVA
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

        // UC10-R3: Numero e bicicleta não podem ser editados
        bicicletaExistente.setMarca(dadosNovos.getMarca());
        bicicletaExistente.setModelo(dadosNovos.getModelo());
        bicicletaExistente.setAno(dadosNovos.getAno());

        return repository.salvar(bicicletaExistente);
    }

    public void deletar(int id) {
        Bicicleta bicicleta = buscar(id);

        // UC10-R4: Só pode excluir se status for 'APOSENTADA'
        if (bicicleta.getStatus() != StatusBicicleta.APOSENTADA) {
            throw new InvalidActionException("Ação inválida. Apenas bicicletas com status APOSENTADA podem ser excluídas.");
        }

        // UC10-R4: Só pode excluir se não estiver em nenhuma tranca
        boolean emTranca = trancaRepository.listar().stream()
                .anyMatch(tranca -> Objects.equals(tranca.getBicicleta(), id));
        if (emTranca) {
            throw new InvalidActionException("Ação inválida. Bicicleta ainda está associada a uma tranca.");
        }

        repository.deletar(bicicleta.getId());
    }

    public void integrarNaRede(IntegrarBicicletaRequest request) {
        aluguelClient.validarReparador(request.getIdFuncionario());

        Bicicleta bicicleta = buscar(request.getIdBicicleta());
        Tranca tranca = trancaService.buscar(request.getIdTranca());

        // Tranca precisa estar 'LIVRE'
        if (tranca.getStatus() != StatusTranca.LIVRE) {
            throw new InvalidActionException("Tranca não está livre.");
        }

        // Bicicleta precisa ser 'NOVA' ou estar 'EM_REPARO'
        if (bicicleta.getStatus() != StatusBicicleta.NOVA && bicicleta.getStatus() != StatusBicicleta.EM_REPARO) {
            throw new InvalidActionException("Bicicleta não está com status NOVA ou EM_REPARO.");
        }

        bicicleta.setStatus(StatusBicicleta.DISPONIVEL);
        tranca.setStatus(StatusTranca.OCUPADA);
        tranca.setBicicleta(bicicleta.getId());

        repository.salvar(bicicleta);
        trancaService.salvar(tranca);

        externoClient.enviarEmail(
                aluguelClient.getFuncionarioEmail(request.getIdFuncionario()),
                "Bicicleta Integrada à Rede",
                "A bicicleta ID " + bicicleta.getId() + " foi integrada na tranca ID " + tranca.getId() + "."
        );
    }

    public void retirarDaRede(RetirarBicicletaRequest request) {
        aluguelClient.validarReparador(request.getIdFuncionario());

        Bicicleta bicicleta = buscar(request.getIdBicicleta());
        Tranca tranca = trancaService.buscar(request.getIdTranca());

        StatusBicicleta novoStatus = StatusBicicleta.valueOf(request.getStatusAcaoReparador().toUpperCase());

        // Bicicleta deve estar 'REPARO_SOLICITADO'
        if (bicicleta.getStatus() != StatusBicicleta.REPARO_SOLICITADO) {
            throw new InvalidActionException("Bicicleta não está com status REPARO_SOLICITADO.");
        }

        // Tranca deve estar 'OCUPADA'
        if (tranca.getStatus() != StatusTranca.OCUPADA) {
            throw new InvalidActionException("Tranca não está ocupada.");
        }

        bicicleta.setStatus(novoStatus);
        tranca.setStatus(StatusTranca.LIVRE);
        tranca.setBicicleta(null);

        // UC09-R1
        repository.salvar(bicicleta);
        trancaService.salvar(tranca);

        // UC09-R2
        externoClient.enviarEmail(
                aluguelClient.getFuncionarioEmail(request.getIdFuncionario()),
                "Bicicleta Retirada da Rede",
                "A bicicleta ID " + bicicleta.getId() + " foi retirada da tranca ID " + tranca.getId() + " para " + novoStatus.name()
        );
    }

    public Bicicleta alterarStatus(int idBicicleta, String acao) {
        Bicicleta bicicleta = buscar(idBicicleta);

        StatusBicicleta novoStatus;
        try {
            novoStatus = StatusBicicleta.valueOf(acao.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidActionException("Ação de status inválida: " + acao);
        }

        bicicleta.setStatus(novoStatus);
        return repository.salvar(bicicleta);
    }
}