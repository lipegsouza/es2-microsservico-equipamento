package com.microsservico.equipamento.controller;

import com.microsservico.equipamento.service.RestaurarBancoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestaurarBancoController.class)
class RestaurarBancoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurarBancoService service;

    @MockBean
    private BicicletaConverter bicicletaConverter;

    @MockBean
    private TotemConverter totemConverter;

    @MockBean
    private TrancaConverter trancaConverter;

    @Test
    void restaurarBancoSucesso() throws Exception {
        doNothing().when(service).restaurarBanco();

        mockMvc.perform(get("/restaurarBanco"))
                .andExpect(status().isOk());

        verify(service, times(1)).restaurarBanco();
    }
}