package com.microsservico.equipamento.service;

import com.microsservico.equipamento.client.AluguelClient;
import com.microsservico.equipamento.client.ExternoClient;
import com.microsservico.equipamento.domain.Bicicleta;
import com.microsservico.equipamento.domain.StatusBicicleta;
import com.microsservico.equipamento.domain.StatusTranca;
import com.microsservico.equipamento.domain.Tranca;
import com.microsservico.equipamento.dto.request.IntegrarTrancaRequest;
import com.microsservico.equipamento.dto.request.RetirarTrancaRequest;
import com.microsservico.equipamento.dto.request.TrancaAcaoRequest;
import com.microsservico.equipamento.exception.InvalidActionException;
import com.microsservico.equipamento.exception.NotFoundException;
import com.microsservico.equipamento.repository.BicicletaRepository;
import com.microsservico.equipamento.repository.TrancaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class TrancaService {

    private final TrancaRepository repository;
    private final TotemService totemService;
    private final BicicletaRepository bicicletaRepository;
    private final AluguelClient aluguelClient;
    private final ExternoClient externoClient;

    private static final String BICICLETA_NAO_ENCONTRADA = "Bicicleta não encontrada com o ID: ";

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

    public Tranca salvar(Tranca tranca) {
        return repository.salvar(tranca);
    }

    public void integrarNaRede(IntegrarTrancaRequest request) {
        aluguelClient.validarReparador(request.getIdFuncionario());
        Tranca tranca = buscar(request.getIdTranca());
        totemService.buscar(request.getIdTotem());

        if (tranca.getStatus() != StatusTranca.NOVA && tranca.getStatus() != StatusTranca.EM_REPARO) {
            throw new InvalidActionException("Ação inválida. Tranca deve estar com status NOVA ou EM_REPARO.");
        }

        if (tranca.getIdTotem() != null) {
            throw new InvalidActionException("Tranca já está associada a um totem.");
        }

        tranca.setIdTotem(request.getIdTotem());
        tranca.setStatus(StatusTranca.LIVRE);
        repository.salvar(tranca);

        externoClient.enviarEmail(
                aluguelClient.getFuncionarioEmail(request.getIdFuncionario()),
                "Tranca Integrada ao Totem",
                "A tranca ID " + tranca.getId() + " foi integrada no totem ID " + request.getIdTotem() + "."
        );
    }

    public void retirarDaRede(RetirarTrancaRequest request) {
        aluguelClient.validarReparador(request.getIdFuncionario());
        Tranca tranca = buscar(request.getIdTranca());

        if (!Objects.equals(tranca.getIdTotem(), request.getIdTotem())) {
            throw new InvalidActionException("Tranca não pertence ao totem informado.");
        }

        if (tranca.getBicicleta() != null) {
            throw new InvalidActionException("Ação inválida. Tranca possui uma bicicleta.");
        }

        StatusTranca novoStatus;
        try {
            novoStatus = StatusTranca.valueOf(request.getStatusAcaoReparador().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidActionException("Status de ação inválido: " + request.getStatusAcaoReparador());
        }

        if (novoStatus != StatusTranca.EM_REPARO && novoStatus != StatusTranca.APOSENTADA) {
            throw new InvalidActionException("Ação de reparador deve ser EM_REPARO ou APOSENTADA.");
        }

        tranca.setStatus(novoStatus);
        tranca.setIdTotem(null);
        repository.salvar(tranca);

        externoClient.enviarEmail(
                aluguelClient.getFuncionarioEmail(request.getIdFuncionario()),
                "Tranca Retirada do Totem",
                "A tranca ID " + tranca.getId() + " foi retirada do totem ID " + request.getIdTotem() + " para " + novoStatus.name()
        );
    }

    public Bicicleta getBicicleta(int idTranca) {
        Tranca tranca = buscar(idTranca);
        if (tranca.getBicicleta() == null) {
            throw new NotFoundException("Tranca está livre ou não possui bicicleta associada.");
        }
        return bicicletaRepository.buscar(tranca.getBicicleta())
                .orElseThrow(() -> new NotFoundException(BICICLETA_NAO_ENCONTRADA + tranca.getBicicleta()));
    }

    public Tranca trancar(int idTranca, TrancaAcaoRequest request) {
        Tranca tranca = buscar(idTranca);

        if (request.getBicicleta() == null) {
            throw new InvalidActionException("ID da bicicleta é obrigatório para trancar.");
        }

        if (tranca.getStatus() != StatusTranca.LIVRE) {
            throw new InvalidActionException("Tranca não está livre.");
        }

        Bicicleta bicicleta = bicicletaRepository.buscar(request.getBicicleta())
                .orElseThrow(() -> new NotFoundException(BICICLETA_NAO_ENCONTRADA + request.getBicicleta()));

        if (bicicleta.getStatus() != StatusBicicleta.EM_USO) {
            throw new InvalidActionException("Bicicleta não está EM_USO.");
        }

        tranca.setStatus(StatusTranca.OCUPADA);
        tranca.setBicicleta(bicicleta.getId());

        bicicleta.setStatus(StatusBicicleta.DISPONIVEL);
        bicicletaRepository.salvar(bicicleta);

        return repository.salvar(tranca);
    }

    public Tranca destrancar(int idTranca, TrancaAcaoRequest request) {
        Tranca tranca = buscar(idTranca);

        if (tranca.getStatus() != StatusTranca.OCUPADA) {
            throw new InvalidActionException("Tranca não está ocupada.");
        }
        if (tranca.getBicicleta() == null) {
            throw new InvalidActionException("Tranca ocupada, mas sem bicicleta (estado inconsistente).");
        }

        Bicicleta bicicleta = bicicletaRepository.buscar(tranca.getBicicleta())
                .orElseThrow(() -> new NotFoundException(BICICLETA_NAO_ENCONTRADA + tranca.getBicicleta()));

        if (bicicleta.getStatus() != StatusBicicleta.DISPONIVEL) {
            throw new InvalidActionException("Bicicleta na tranca não está DISPONIVEL.");
        }

        if (request != null && request.getBicicleta() != null && !Objects.equals(request.getBicicleta(), bicicleta.getId())) {
            throw new InvalidActionException("ID da bicicleta informado não corresponde ao da tranca.");
        }

        tranca.setStatus(StatusTranca.LIVRE);
        tranca.setBicicleta(null);

        bicicleta.setStatus(StatusBicicleta.EM_USO);
        bicicletaRepository.salvar(bicicleta);

        return repository.salvar(tranca);
    }

    public Tranca alterarStatus(int idTranca, String acao) {
        Tranca tranca = buscar(idTranca);

        if (acao.equalsIgnoreCase("TRANCAR")) {
            tranca.setStatus(StatusTranca.OCUPADA);
        } else if (acao.equalsIgnoreCase("DESTRANCAR")) {
            tranca.setStatus(StatusTranca.LIVRE);
        } else {
            throw new InvalidActionException("Ação inválida. Use 'TRANCAR' ou 'DESTRANCAR'.");
        }

        return repository.salvar(tranca);
    }
}