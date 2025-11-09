package com.microsservico.equipamento.repository;

import com.microsservico.equipamento.domain.Tranca;
import org.springframework.stereotype.Repository;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class TrancaRepository {
    private static final Map<Integer, Tranca> trancas = new HashMap<>();

    private static final AtomicInteger idCounter = new AtomicInteger(1);

    public int gerarId() {
        return idCounter.getAndIncrement();
    }

    public Tranca salvar(Tranca tranca) {
        if (tranca.getId() == 0) {
            tranca.setId(gerarId());
        }
        trancas.put(tranca.getId(), tranca);
        return tranca;
    }

    public Optional<Tranca> buscar(int id) {
        return Optional.ofNullable(trancas.get(id));
    }

    public List<Tranca> listar() {
        return new ArrayList<>(trancas.values());
    }

    public void deletar(int id) {
        trancas.remove(id);
    }

    public List<Tranca> buscarEmTotem(int idTotem) {
        return trancas.values().stream()
                .filter(tranca -> tranca.getIdTotem() != null && tranca.getIdTotem() == idTotem)
                .toList();
    }
}