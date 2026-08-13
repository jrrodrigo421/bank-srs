package com.entrevista.tarefas.repository;

import com.entrevista.tarefas.domain.StatusTarefa;
import com.entrevista.tarefas.domain.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    List<Tarefa> findByStatus(StatusTarefa status);
}
