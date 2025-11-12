package com.microsservico.equipamento.client;

import com.microsservico.equipamento.exception.InvalidActionException;
import com.microsservico.equipamento.exception.NotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AluguelClient {

    public void validarReparador(Integer idFuncionario) {
        if (idFuncionario == 3) {
            throw new NotFoundException("Funcionário não encontrado.");
        }

        if (idFuncionario == 2) {
            throw new InvalidActionException("Funcionário não autorizado (é administrativo).");
        }
    }

    public String getFuncionarioEmail(Integer idFuncionario) {
        if (idFuncionario == 2) {
            return "admin@email.com";
        }
        return "reparador." + idFuncionario + "@email.com";
    }
}