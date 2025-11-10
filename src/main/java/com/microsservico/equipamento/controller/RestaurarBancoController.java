package com.microsservico.equipamento.controller;

import com.microsservico.equipamento.service.RestaurarBancoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurarBanco")
@RequiredArgsConstructor
public class RestaurarBancoController {

    private final RestaurarBancoService service;

    @GetMapping
    public ResponseEntity<Void> restaurarBanco() {
        service.restaurarBanco();
        return ResponseEntity.ok().build();
    }
}