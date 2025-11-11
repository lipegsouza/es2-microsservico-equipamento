package com.microsservico.equipamento.controller;

import com.microsservico.equipamento.domain.Bicicleta;
import com.microsservico.equipamento.domain.Tranca;
import com.microsservico.equipamento.dto.request.IntegrarTrancaRequest;
import com.microsservico.equipamento.dto.request.RetirarTrancaRequest;
import com.microsservico.equipamento.dto.request.TrancaAcaoRequest;
import com.microsservico.equipamento.dto.request.TrancaRequest;
import com.microsservico.equipamento.dto.response.BicicletaResponse;
import com.microsservico.equipamento.dto.response.TrancaResponse;
import com.microsservico.equipamento.service.TrancaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tranca")
@RequiredArgsConstructor
public class TrancaController {

    private final TrancaService service;

    private final TrancaConverter converter;

    private final BicicletaConverter bicicletaConverter;

    @PostMapping
    public ResponseEntity<TrancaResponse> cadastrar(@RequestBody TrancaRequest trancaRequest) {
        Tranca tranca = converter.dtoToDomain(trancaRequest);
        Tranca trancaSalva = service.cadastrar(tranca);
        TrancaResponse trancaResponse = converter.domainToDto(trancaSalva);
        return ResponseEntity.ok(trancaResponse);
    }

    @GetMapping
    public ResponseEntity<List<TrancaResponse>> listar() {
        List<Tranca> listaDeTrancas = service.listar();
        List<TrancaResponse> listaDeResponse = listaDeTrancas.stream()
                .map(converter::domainToDto)
                .toList();
        return ResponseEntity.ok(listaDeResponse);
    }

    @GetMapping("/{idTranca}")
    public ResponseEntity<TrancaResponse> buscar(@PathVariable int idTranca) {
        Tranca tranca = service.buscar(idTranca);
        TrancaResponse trancaResponse = converter.domainToDto(tranca);
        return ResponseEntity.ok(trancaResponse);
    }

    @PutMapping("/{idTranca}")
    public ResponseEntity<TrancaResponse> editar(@PathVariable int idTranca, @RequestBody TrancaRequest trancaRequest) {
        Tranca tranca = converter.dtoToDomain(trancaRequest);
        Tranca trancaEditada = service.editar(idTranca, tranca);
        TrancaResponse trancaResponse = converter.domainToDto(trancaEditada);
        return ResponseEntity.ok(trancaResponse);
    }

    @DeleteMapping("/{idTranca}")
    public ResponseEntity<Void> deletar(@PathVariable int idTranca) {
        service.deletar(idTranca);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/integrarNaRede")
    public ResponseEntity<Void> integrarNaRede(@RequestBody IntegrarTrancaRequest request) {
        service.integrarNaRede(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/retirarDaRede")
    public ResponseEntity<Void> retirarDaRede(@RequestBody RetirarTrancaRequest request) {
        service.retirarDaRede(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{idTranca}/bicicleta")
    public ResponseEntity<BicicletaResponse> getBicicleta(@PathVariable int idTranca) {
        Bicicleta bicicleta = service.getBicicleta(idTranca);
        BicicletaResponse bicicletaResponse = bicicletaConverter.domainToDto(bicicleta);
        return ResponseEntity.ok(bicicletaResponse);
    }

    @PostMapping("/{idTranca}/trancar")
    public ResponseEntity<TrancaResponse> trancar(@PathVariable int idTranca, @RequestBody TrancaAcaoRequest request) {
        Tranca tranca = service.trancar(idTranca, request);
        TrancaResponse trancaResponse = converter.domainToDto(tranca);
        return ResponseEntity.ok(trancaResponse);
    }

    @PostMapping("/{idTranca}/destrancar")
    public ResponseEntity<TrancaResponse> destrancar(@PathVariable int idTranca, @RequestBody(required = false) TrancaAcaoRequest request) {
        Tranca tranca = service.destrancar(idTranca, request);
        TrancaResponse trancaResponse = converter.domainToDto(tranca);
        return ResponseEntity.ok(trancaResponse);
    }

    @PostMapping("/{idTranca}/status/{acao}")
    public ResponseEntity<TrancaResponse> alterarStatus(@PathVariable int idTranca, @PathVariable String acao) {
        Tranca tranca = service.alterarStatus(idTranca, acao);
        TrancaResponse trancaResponse = converter.domainToDto(tranca);
        return ResponseEntity.ok(trancaResponse);
    }
}