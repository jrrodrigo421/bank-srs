package com.entrevista.banco.service;

import com.entrevista.banco.domain.FilaMovimento;
import com.entrevista.banco.domain.TipoMovimento;
import com.entrevista.banco.dto.FilaMovimentoResponse;
import com.entrevista.banco.dto.MovimentoRequest;

public interface FilaService {

    FilaMovimentoResponse enfileirar(Long contaId, TipoMovimento tipo, MovimentoRequest request, String idempotencyKey);

    FilaMovimento processarProxima();
}
