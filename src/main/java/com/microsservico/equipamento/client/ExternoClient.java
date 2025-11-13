package com.microsservico.equipamento.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExternoClient {

    public void enviarEmail(String email, String assunto, String mensagem) {
        log.info("Para: {}", email);
        log.info("Assunto: {}", assunto);
        log.info("Mensagem: {}", mensagem);
    }
}