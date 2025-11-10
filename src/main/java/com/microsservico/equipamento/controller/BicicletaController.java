package com.microsservico.equipamento.controller;

import com.microsservico.equipamento.domain.Bicicleta;
import com.microsservico.equipamento.dto.request.BicicletaRequest;
import com.microsservico.equipamento.dto.request.IntegrarBicicletaRequest;
import com.microsservico.equipamento.dto.request.RetirarBicicletaRequest;
import com.microsservico.equipamento.dto.response.BicicletaResponse;
import com.microsservico.equipamento.service.BicicletaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/bicicleta")
@RequiredArgsConstructor
public class BicicletaController {

    private final BicicletaService service;

    private final BicicletaConverter converter;

    @PostMapping
    public ResponseEntity<BicicletaResponse> cadastrar(@RequestBody BicicletaRequest bicicletaRequest) {
        Bicicleta bicicletaDomain = converter.dtoToDomain(bicicletaRequest);
        Bicicleta bicicleta = service.cadastrar(bicicletaDomain);
        BicicletaResponse bicicletaResponse = converter.domainToDto(bicicleta);
        return ResponseEntity.ok(bicicletaResponse);
    }

    @GetMapping("/{idBicicleta}")
    public ResponseEntity<BicicletaResponse> buscar(@PathVariable int idBicicleta) {
        Bicicleta bicicleta = service.buscar(idBicicleta);
        BicicletaResponse bicicletaResponse = converter.domainToDto(bicicleta);
        return ResponseEntity.ok(bicicletaResponse);
    }

    @GetMapping
    public ResponseEntity<List<BicicletaResponse>> listar() {
        List<Bicicleta> bicicletas = service.listar();
        List<BicicletaResponse> bicicletaResponses = bicicletas.stream()
                .map(converter::domainToDto)
                .toList();
        return ResponseEntity.ok(bicicletaResponses);
    }

    @PutMapping("/{idBicicleta}")
    public ResponseEntity<BicicletaResponse> editar(@PathVariable int idBicicleta, @RequestBody BicicletaRequest bicicletaRequest) {
        Bicicleta bicicleta = converter.dtoToDomain(bicicletaRequest);

        Bicicleta bicicletaEditada = service.editar(idBicicleta, bicicleta);

        BicicletaResponse bicicletaResponse = converter.domainToDto(bicicletaEditada);

        return ResponseEntity.ok(bicicletaResponse);
    }

    @DeleteMapping("/{idBicicleta}")
    public ResponseEntity<Void> deletar(@PathVariable int idBicicleta) {
        service.deletar(idBicicleta);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/integrarNaRede")
    public ResponseEntity<Void> integrarNaRede(@RequestBody IntegrarBicicletaRequest request) {
        service.integrarNaRede(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/retirarDaRede")
    public ResponseEntity<Void> retirarDaRede(@RequestBody RetirarBicicletaRequest request) {
        service.retirarDaRede(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{idBicicleta}/status/{acao}")
    public ResponseEntity<BicicletaResponse> alterarStatus(@PathVariable int idBicicleta, @PathVariable String acao) {
        Bicicleta bicicleta = service.alterarStatus(idBicicleta, acao);
        BicicletaResponse bicicletaResponse = converter.domainToDto(bicicleta);
        return ResponseEntity.ok(bicicletaResponse);
    }
}