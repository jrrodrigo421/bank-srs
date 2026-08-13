package com.entrevista.tarefas.service;

import com.entrevista.tarefas.domain.StatusTarefa;
import com.entrevista.tarefas.dto.TarefaRequest;
import com.entrevista.tarefas.dto.TarefaResponse;

import java.util.List;

public interface TarefaService {

    TarefaResponse criar(TarefaRequest request);

    TarefaResponse buscarPorId(Long id);

    List<TarefaResponse> listarTodas();

    List<TarefaResponse> listarPorStatus(StatusTarefa status);

    TarefaResponse atualizar(Long id, TarefaRequest request);

    void excluir(Long id);
}
