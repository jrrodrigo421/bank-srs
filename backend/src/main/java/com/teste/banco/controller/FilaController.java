package com.teste.banco.controller;

import com.teste.banco.domain.FilaMovimento;
import com.teste.banco.dto.FilaMovimentoResponse;
import com.teste.banco.service.FilaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fila")
@Tag(name = "Fila", description = "Processa um item PENDENTE da FILA_MOVIMENTO (lock FOR UPDATE SKIP LOCKED)")
public class FilaController {

    private final FilaService filaService;

    public FilaController(FilaService filaService) {
        this.filaService = filaService;
    }

    @PostMapping("/processar")
    @Operation(summary = "Processa a próxima movimentação da fila")
    public ResponseEntity<FilaMovimentoResponse> processar() {
        FilaMovimento item = filaService.processarProxima();
        if (item == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(FilaMovimentoResponse.from(item));
    }
}
