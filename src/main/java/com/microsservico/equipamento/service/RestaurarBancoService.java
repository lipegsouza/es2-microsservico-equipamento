package com.microsservico.equipamento.service;

import com.microsservico.equipamento.repository.BicicletaRepository;
import com.microsservico.equipamento.repository.TotemRepository;
import com.microsservico.equipamento.repository.TrancaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurarBancoService {

    private final BicicletaRepository bicicletaRepository;
    private final TotemRepository totemRepository;
    private final TrancaRepository trancaRepository;
    private final BicicletaService bicicletaService;

    public void restaurarBanco() {
        bicicletaRepository.restaurar();
        totemRepository.restaurar();
        trancaRepository.restaurar();
        bicicletaService.restaurarNumero();
    }
}