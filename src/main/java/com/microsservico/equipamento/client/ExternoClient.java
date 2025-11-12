package com.microsservico.equipamento.client;

import org.springframework.stereotype.Component;

@Component
public class ExternoClient {

    public void enviarEmail(String email, String assunto, String mensagem) {
        System.out.println("Para: " + email);
        System.out.println("Assunto: " + assunto);
        System.out.println("Mensagem: " + mensagem);
    }
}