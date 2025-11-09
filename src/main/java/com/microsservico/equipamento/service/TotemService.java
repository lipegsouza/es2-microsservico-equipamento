package com.microsservico.equipamento.service;

import com.microsservico.equipamento.domain.Bicicleta;
import com.microsservico.equipamento.domain.Totem;
import com.microsservico.equipamento.domain.Tranca;
import com.microsservico.equipamento.exception.InvalidActionException;
import com.microsservico.equipamento.exception.NotFoundException;
import com.microsservico.equipamento.repository.BicicletaRepository;
import com.microsservico.equipamento.repository.TotemRepository;
import com.microsservico.equipamento.repository.TrancaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TotemService {
    private final TotemRepository repository;

    private final TrancaRepository trancaRepository;

    private final  BicicletaRepository bicicletaRepository;

    private void validar(Totem totem) {
        if (totem.getLocalizacao() == null || totem.getLocalizacao().isBlank() ||
                totem.getDescricao() == null || totem.getDescricao().isBlank()) {
            throw new InvalidActionException("Dados inválidos. Localização e Descrição são obrigatórios.");
        }
    }

    public Totem cadastrar(Totem totem) {
        validar(totem);
        return repository.salvar(totem);
    }

    public Totem buscar(int id) {
        return repository.buscar(id)
                .orElseThrow(() -> new NotFoundException("Totem não encontrado com o ID: " + id));
    }

    public List<Totem> listar() {
        return repository.listar();
    }

    public Totem editar(int id, Totem dadosNovos) {
        Totem totemExistente = buscar(id);
        validar(dadosNovos);

        totemExistente.setLocalizacao(dadosNovos.getLocalizacao());
        totemExistente.setDescricao(dadosNovos.getDescricao());

        return repository.salvar(totemExistente);
    }

    public void deletar(int id) {
        Totem totem = buscar(id);

        List<Tranca> trancasNoTotem = trancaRepository.buscarEmTotem(id);
        if (!trancasNoTotem.isEmpty()) {
            throw new InvalidActionException("Ação inválida. Apenas totens sem trancas podem ser excluídos.");
        }

        repository.deletar(totem.getId());
    }

    public List<Tranca> listarTrancas(int idTotem) {
        buscar(idTotem);
        return trancaRepository.buscarEmTotem(idTotem);
    }

    public List<Bicicleta> listarBicicletas(int idTotem) {
        buscar(idTotem);

        List<Tranca> trancasNoTotem = trancaRepository.buscarEmTotem(idTotem);

        List<Integer> idsBicicletas = trancasNoTotem.stream()
                .map(Tranca::getBicicleta)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (idsBicicletas.isEmpty()) {
            return List.of();
        }

        return bicicletaRepository.buscarEmTrancasDeTotem(idsBicicletas);
    }
}