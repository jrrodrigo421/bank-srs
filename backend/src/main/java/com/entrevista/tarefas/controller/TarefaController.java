package com.entrevista.tarefas.controller;

import com.entrevista.tarefas.domain.StatusTarefa;
import com.entrevista.tarefas.dto.TarefaRequest;
import com.entrevista.tarefas.dto.TarefaResponse;
import com.entrevista.tarefas.service.TarefaService;
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
@RequestMapping("/api/tarefas")
@Tag(name = "Tarefas", description = "CRUD de lista de tarefas")
public class TarefaController {

    private final TarefaService tarefaService;

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @PostMapping
    @Operation(summary = "Criar tarefa")
    public ResponseEntity<TarefaResponse> criar(@Valid @RequestBody TarefaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tarefaService.criar(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tarefa por ID")
    public ResponseEntity<TarefaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tarefaService.buscarPorId(id));
    }

    @GetMapping
    @Operation(summary = "Listar tarefas (opcional filtro por status)")
    public ResponseEntity<List<TarefaResponse>> listar(
            @RequestParam(required = false) StatusTarefa status) {
        if (status != null) {
            return ResponseEntity.ok(tarefaService.listarPorStatus(status));
        }
        return ResponseEntity.ok(tarefaService.listarTodas());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar tarefa")
    public ResponseEntity<TarefaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody TarefaRequest request) {
        return ResponseEntity.ok(tarefaService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir tarefa")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        tarefaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
