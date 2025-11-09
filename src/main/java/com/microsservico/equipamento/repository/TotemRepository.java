package com.microsservico.equipamento.repository;

import com.microsservico.equipamento.domain.Totem;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class TotemRepository {

    private static final Map<Integer, Totem> totens = new HashMap<>();
    private static final AtomicInteger idCounter = new AtomicInteger(1);

    public int gerarId() {
        return idCounter.getAndIncrement();
    }

    public Totem salvar(Totem totem) {
        if (totem.getId() == 0) {
            totem.setId(gerarId());
        }
        totens.put(totem.getId(), totem);
        return totem;
    }

    public Optional<Totem> buscar(int id) {
        return Optional.ofNullable(totens.get(id));
    }

    public List<Totem> listar() {
        return new ArrayList<>(totens.values());
    }

    public void deletar(int id) {
        totens.remove(id);
    }

}