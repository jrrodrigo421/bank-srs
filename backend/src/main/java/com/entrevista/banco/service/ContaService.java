package com.entrevista.banco.service;

import com.entrevista.banco.domain.StatusConta;
import com.entrevista.banco.domain.TipoConta;
import com.entrevista.banco.dto.ContaRequest;
import com.entrevista.banco.dto.ContaResponse;
import com.entrevista.banco.dto.MovimentoRequest;

import java.util.List;

public interface ContaService {

    ContaResponse abrir(ContaRequest request);

    ContaResponse buscarPorId(Long id);

    List<ContaResponse> listar(StatusConta status, TipoConta tipo);

    ContaResponse atualizar(Long id, ContaRequest request);

    void encerrar(Long id);

    ContaResponse depositar(Long id, MovimentoRequest request, String idempotencyKey);

    ContaResponse sacar(Long id, MovimentoRequest request, String idempotencyKey);
}
