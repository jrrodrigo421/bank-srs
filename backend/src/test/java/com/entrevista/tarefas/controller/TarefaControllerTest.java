package com.entrevista.tarefas.controller;

import com.entrevista.tarefas.domain.StatusTarefa;
import com.entrevista.tarefas.dto.TarefaRequest;
import com.entrevista.tarefas.dto.TarefaResponse;
import com.entrevista.tarefas.exception.RecursoNaoEncontradoException;
import com.entrevista.tarefas.service.TarefaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TarefaController.class)
class TarefaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TarefaService tarefaService;

    @Test
    void deveCriarTarefa() throws Exception {
        TarefaRequest request = new TarefaRequest();
        request.setTitulo("Nova tarefa");
        request.setDescricao("Desc");
        request.setStatus(StatusTarefa.PENDENTE);

        TarefaResponse response = new TarefaResponse(
                1L, "Nova tarefa", "Desc", StatusTarefa.PENDENTE,
                LocalDateTime.now(), null);

        when(tarefaService.criar(any(TarefaRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Nova tarefa"));
    }

    @Test
    void deveListarTarefas() throws Exception {
        TarefaResponse response = new TarefaResponse(
                1L, "Tarefa", "Desc", StatusTarefa.PENDENTE,
                LocalDateTime.now(), null);
        when(tarefaService.listarTodas()).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/tarefas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Tarefa"));
    }

    @Test
    void deveRetornar404QuandoNaoEncontrar() throws Exception {
        when(tarefaService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Tarefa não encontrada: 99"));

        mockMvc.perform(get("/api/tarefas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAtualizarTarefa() throws Exception {
        TarefaRequest request = new TarefaRequest();
        request.setTitulo("Atualizada");
        request.setStatus(StatusTarefa.CONCLUIDA);

        TarefaResponse response = new TarefaResponse(
                1L, "Atualizada", null, StatusTarefa.CONCLUIDA,
                LocalDateTime.now(), LocalDateTime.now());

        when(tarefaService.atualizar(eq(1L), any(TarefaRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/tarefas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONCLUIDA"));
    }

    @Test
    void deveExcluirTarefa() throws Exception {
        doNothing().when(tarefaService).excluir(1L);

        mockMvc.perform(delete("/api/tarefas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar400QuandoTituloVazio() throws Exception {
        TarefaRequest request = new TarefaRequest();
        request.setTitulo("");
        request.setStatus(StatusTarefa.PENDENTE);

        mockMvc.perform(post("/api/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
