package com.microsservico.equipamento.controller;

import com.microsservico.equipamento.domain.Tranca;
import com.microsservico.equipamento.dto.request.TrancaRequest;
import com.microsservico.equipamento.dto.response.TrancaResponse;
import com.microsservico.equipamento.service.TrancaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tranca")
@RequiredArgsConstructor
public class TrancaController {

    private final TrancaService service;

    private final TrancaConverter converter;

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
                .collect(Collectors.toList());
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
}