package com.microsservico.equipamento.controller;

import com.microsservico.equipamento.service.restaurarBancoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurarBanco")
@RequiredArgsConstructor
public class restaurarBancoController {

    private final restaurarBancoService service;

    @GetMapping
    public ResponseEntity<Void> restaurarBanco() {
        service.restaurarBanco();
        return ResponseEntity.ok().build();
    }
}