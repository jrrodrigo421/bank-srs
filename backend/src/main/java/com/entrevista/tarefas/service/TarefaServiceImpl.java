package com.entrevista.tarefas.service;

import com.entrevista.tarefas.domain.StatusTarefa;
import com.entrevista.tarefas.domain.Tarefa;
import com.entrevista.tarefas.dto.TarefaRequest;
import com.entrevista.tarefas.dto.TarefaResponse;
import com.entrevista.tarefas.exception.RecursoNaoEncontradoException;
import com.entrevista.tarefas.repository.TarefaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TarefaServiceImpl implements TarefaService {

    private final TarefaRepository tarefaRepository;

    public TarefaServiceImpl(TarefaRepository tarefaRepository) {
        this.tarefaRepository = tarefaRepository;
    }

    @Override
    public TarefaResponse criar(TarefaRequest request) {
        Tarefa tarefa = new Tarefa();
        mapearRequestParaEntidade(request, tarefa);
        Tarefa salva = tarefaRepository.save(tarefa);
        return paraResponse(salva);
    }

    @Override
    @Transactional(readOnly = true)
    public TarefaResponse buscarPorId(Long id) {
        return paraResponse(buscarEntidade(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TarefaResponse> listarTodas() {
        return tarefaRepository.findAll().stream()
                .map(this::paraResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TarefaResponse> listarPorStatus(StatusTarefa status) {
        return tarefaRepository.findByStatus(status).stream()
                .map(this::paraResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TarefaResponse atualizar(Long id, TarefaRequest request) {
        Tarefa tarefa = buscarEntidade(id);
        mapearRequestParaEntidade(request, tarefa);
        return paraResponse(tarefaRepository.save(tarefa));
    }

    @Override
    public void excluir(Long id) {
        if (!tarefaRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Tarefa não encontrada: " + id);
        }
        tarefaRepository.deleteById(id);
    }

    private Tarefa buscarEntidade(Long id) {
        return tarefaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tarefa não encontrada: " + id));
    }

    private void mapearRequestParaEntidade(TarefaRequest request, Tarefa tarefa) {
        tarefa.setTitulo(request.getTitulo());
        tarefa.setDescricao(request.getDescricao());
        tarefa.setStatus(request.getStatus());
    }

    private TarefaResponse paraResponse(Tarefa tarefa) {
        return new TarefaResponse(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getStatus(),
                tarefa.getCriadoEm(),
                tarefa.getAtualizadoEm()
        );
    }
}
