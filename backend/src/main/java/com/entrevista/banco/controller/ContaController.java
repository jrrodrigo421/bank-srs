package com.entrevista.banco.controller;

import com.entrevista.banco.domain.StatusConta;
import com.entrevista.banco.domain.TipoConta;
import com.entrevista.banco.dto.ContaRequest;
import com.entrevista.banco.dto.ContaResponse;
import com.entrevista.banco.dto.MovimentoRequest;
import com.entrevista.banco.service.ContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/contas")
@Tag(name = "Contas", description = "Cadastro de contas e movimentos (depósito/saque)")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @PostMapping
    @Operation(summary = "Abrir conta (saldo inicia em 0)")
    public ResponseEntity<ContaResponse> abrir(@Valid @RequestBody ContaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contaService.abrir(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar conta por ID")
    public ResponseEntity<ContaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.buscarPorId(id));
    }

    @GetMapping
    @Operation(summary = "Listar contas (filtro opcional por status ou tipo)")
    public ResponseEntity<List<ContaResponse>> listar(
            @RequestParam(required = false) StatusConta status,
            @RequestParam(required = false) TipoConta tipo) {
        return ResponseEntity.ok(contaService.listar(status, tipo));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados cadastrais (não altera saldo)")
    public ResponseEntity<ContaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ContaRequest request) {
        return ResponseEntity.ok(contaService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Encerrar conta (somente saldo zero)")
    public ResponseEntity<Void> encerrar(@PathVariable Long id) {
        contaService.encerrar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/depositar")
    @Operation(summary = "Depositar (somente conta ATIVA)")
    public ResponseEntity<ContaResponse> depositar(
            @PathVariable Long id,
            @Valid @RequestBody MovimentoRequest request) {
        return ResponseEntity.ok(contaService.depositar(id, request));
    }

    @PostMapping("/{id}/sacar")
    @Operation(summary = "Sacar (somente conta ATIVA e com saldo)")
    public ResponseEntity<ContaResponse> sacar(
            @PathVariable Long id,
            @Valid @RequestBody MovimentoRequest request) {
        return ResponseEntity.ok(contaService.sacar(id, request));
    }
}
