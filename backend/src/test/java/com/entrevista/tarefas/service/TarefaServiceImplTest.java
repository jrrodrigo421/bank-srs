package com.entrevista.tarefas.service;

import com.entrevista.tarefas.domain.StatusTarefa;
import com.entrevista.tarefas.domain.Tarefa;
import com.entrevista.tarefas.dto.TarefaRequest;
import com.entrevista.tarefas.dto.TarefaResponse;
import com.entrevista.tarefas.exception.RecursoNaoEncontradoException;
import com.entrevista.tarefas.repository.TarefaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TarefaServiceImplTest {

    @Mock
    private TarefaRepository tarefaRepository;

    @InjectMocks
    private TarefaServiceImpl tarefaService;

    private Tarefa tarefa;
    private TarefaRequest request;

    @BeforeEach
    void setUp() {
        tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setTitulo("Estudar Spring Boot");
        tarefa.setDescricao("Revisar CRUD e SOLID");
        tarefa.setStatus(StatusTarefa.PENDENTE);
        tarefa.setCriadoEm(LocalDateTime.now());

        request = new TarefaRequest();
        request.setTitulo("Estudar Spring Boot");
        request.setDescricao("Revisar CRUD e SOLID");
        request.setStatus(StatusTarefa.PENDENTE);
    }

    @Test
    void deveCriarTarefa() {
        when(tarefaRepository.save(any(Tarefa.class))).thenReturn(tarefa);

        TarefaResponse response = tarefaService.criar(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Estudar Spring Boot", response.getTitulo());
        verify(tarefaRepository).save(any(Tarefa.class));
    }

    @Test
    void deveBuscarPorId() {
        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));

        TarefaResponse response = tarefaService.buscarPorId(1L);

        assertEquals(1L, response.getId());
        verify(tarefaRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoNaoEncontrar() {
        when(tarefaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> tarefaService.buscarPorId(99L));
    }

    @Test
    void deveListarTodas() {
        when(tarefaRepository.findAll()).thenReturn(Collections.singletonList(tarefa));

        List<TarefaResponse> lista = tarefaService.listarTodas();

        assertEquals(1, lista.size());
        verify(tarefaRepository).findAll();
    }

    @Test
    void deveListarPorStatus() {
        when(tarefaRepository.findByStatus(StatusTarefa.PENDENTE))
                .thenReturn(Arrays.asList(tarefa));

        List<TarefaResponse> lista = tarefaService.listarPorStatus(StatusTarefa.PENDENTE);

        assertEquals(1, lista.size());
        assertEquals(StatusTarefa.PENDENTE, lista.get(0).getStatus());
    }

    @Test
    void deveAtualizarTarefa() {
        request.setTitulo("Título atualizado");
        request.setStatus(StatusTarefa.EM_ANDAMENTO);
        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        when(tarefaRepository.save(any(Tarefa.class))).thenReturn(tarefa);

        TarefaResponse response = tarefaService.atualizar(1L, request);

        assertEquals("Título atualizado", response.getTitulo());
        assertEquals(StatusTarefa.EM_ANDAMENTO, response.getStatus());
    }

    @Test
    void deveExcluirTarefa() {
        when(tarefaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(tarefaRepository).deleteById(1L);

        tarefaService.excluir(1L);

        verify(tarefaRepository).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoExcluirInexistente() {
        when(tarefaRepository.existsById(99L)).thenReturn(false);

        assertThrows(RecursoNaoEncontradoException.class, () -> tarefaService.excluir(99L));
        verify(tarefaRepository, never()).deleteById(99L);
    }
}
