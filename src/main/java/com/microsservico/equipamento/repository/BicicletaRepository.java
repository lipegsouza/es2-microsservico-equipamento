package com.microsservico.equipamento.repository;

import com.microsservico.equipamento.domain.Bicicleta;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class BicicletaRepository {

    private static final Map<Integer, Bicicleta> bicicletas = new HashMap<>();
    private static final AtomicInteger idCounter = new AtomicInteger(1);

    public int gerarId() {
        return idCounter.getAndIncrement();
    }

    public Bicicleta salvar(Bicicleta bicicleta) {
        if (bicicleta.getId() == 0) {
            bicicleta.setId(gerarId());
        }
        bicicletas.put(bicicleta.getId(), bicicleta);
        return bicicleta;
    }

    public Optional<Bicicleta> buscar(int id) {
        return Optional.ofNullable(bicicletas.get(id));
    }

    public List<Bicicleta> listar() {
        return new ArrayList<>(bicicletas.values());
    }

    public void deletar(int id) {
        bicicletas.remove(id);
    }

    public List<Bicicleta> buscarEmTrancasDeTotem(List<Integer> ids) {
        return bicicletas.values().stream()
                .filter(bicicleta -> ids.contains(bicicleta.getId()))
                .toList();
    }
}