package com.microsservico.equipamento.services;

import com.microsservico.equipamento.repository.BicicletaRepository;
import com.microsservico.equipamento.repository.TotemRepository;
import com.microsservico.equipamento.repository.TrancaRepository;
import com.microsservico.equipamento.service.BicicletaService;
import com.microsservico.equipamento.service.RestaurarBancoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RestaurarBancoServiceTest {

    @Mock
    private BicicletaRepository bicicletaRepository;

    @Mock
    private TotemRepository totemRepository;

    @Mock
    private TrancaRepository trancaRepository;

    @Mock
    private BicicletaService bicicletaService;

    @InjectMocks
    private RestaurarBancoService restaurarBancoService;

    @Test
    void testRestaurarBanco() {
        restaurarBancoService.restaurarBanco();

        verify(bicicletaRepository, times(1)).restaurar();
        verify(totemRepository, times(1)).restaurar();
        verify(trancaRepository, times(1)).restaurar();
        verify(bicicletaService, times(1)).restaurarNumeroCounter();
    }
}