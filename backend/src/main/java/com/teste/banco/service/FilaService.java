package com.teste.banco.service;

import com.teste.banco.domain.FilaMovimento;
import com.teste.banco.domain.TipoMovimento;
import com.teste.banco.dto.FilaMovimentoResponse;
import com.teste.banco.dto.MovimentoRequest;

public interface FilaService {

    FilaMovimentoResponse enfileirar(Long contaId, TipoMovimento tipo, MovimentoRequest request, String idempotencyKey);

    FilaMovimento processarProxima();
}
