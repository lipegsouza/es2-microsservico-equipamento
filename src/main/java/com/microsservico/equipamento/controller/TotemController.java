package com.microsservico.equipamento.controller;

import com.microsservico.equipamento.domain.Bicicleta;
import com.microsservico.equipamento.domain.Totem;
import com.microsservico.equipamento.domain.Tranca;
import com.microsservico.equipamento.dto.request.TotemRequest;
import com.microsservico.equipamento.dto.response.BicicletaResponse;
import com.microsservico.equipamento.dto.response.TotemResponse;
import com.microsservico.equipamento.dto.response.TrancaResponse;
import com.microsservico.equipamento.service.TotemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/totem")
@RequiredArgsConstructor
public class TotemController {
    private final TotemService service;

    private final TotemConverter converter;

    private final TrancaConverter trancaConverter;

    private final BicicletaConverter bicicletaConverter;

    @PostMapping
    public ResponseEntity<TotemResponse> cadastrar(@RequestBody TotemRequest totemRequest) {
        Totem totem = converter.dtoToDomain(totemRequest);
        Totem totemSalvo = service.cadastrar(totem);
        TotemResponse totemResponse = converter.domainToDto(totemSalvo);
        return ResponseEntity.ok(totemResponse);
    }

    @GetMapping
    public ResponseEntity<List<TotemResponse>> listar() {
        List<Totem> listaDeTotens = service.listar();
        List<TotemResponse> listaDeResponse = listaDeTotens.stream()
                .map(converter::domainToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaDeResponse);
    }

    @PutMapping("/{idTotem}")
    public ResponseEntity<TotemResponse> editar(@PathVariable int idTotem, @RequestBody TotemRequest totemRequest) {
        Totem totem = converter.dtoToDomain(totemRequest);
        Totem totemEditado = service.editar(idTotem, totem);
        TotemResponse totemResponse = converter.domainToDto(totemEditado);
        return ResponseEntity.ok(totemResponse);
    }

    @DeleteMapping("/{idTotem}")
    public ResponseEntity<Void> deletar(@PathVariable int idTotem) {
        service.deletar(idTotem);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{idTotem}/trancas")
    public ResponseEntity<List<TrancaResponse>> listarTrancas(@PathVariable int idTotem) {
        List<Tranca> listaDeTrancas = service.listarTrancas(idTotem);
        List<TrancaResponse> listaDeResponse = listaDeTrancas.stream()
                .map(trancaConverter::domainToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaDeResponse);
    }

    @GetMapping("/{idTotem}/bicicletas")
    public ResponseEntity<List<BicicletaResponse>> listarBicicletas(@PathVariable int idTotem) {
        List<Bicicleta> listaDeBicicletas = service.listarBicicletas(idTotem);
        List<BicicletaResponse> listaDeResponse = listaDeBicicletas.stream()
                .map(bicicletaConverter::domainToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaDeResponse);
    }
}