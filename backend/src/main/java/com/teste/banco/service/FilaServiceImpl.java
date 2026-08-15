package com.teste.banco.service;

import com.teste.banco.domain.FilaMovimento;
import com.teste.banco.domain.StatusFila;
import com.teste.banco.domain.TipoMovimento;
import com.teste.banco.dto.FilaMovimentoResponse;
import com.teste.banco.dto.MovimentoRequest;
import com.teste.banco.repository.FilaMovimentoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FilaServiceImpl implements FilaService {

    private final FilaMovimentoRepository filaRepository;
    private final ContaService contaService;

    public FilaServiceImpl(FilaMovimentoRepository filaRepository, ContaService contaService) {
        this.filaRepository = filaRepository;
        this.contaService = contaService;
    }

    @Override
    @Transactional
    public FilaMovimentoResponse enfileirar(Long contaId, TipoMovimento tipo, MovimentoRequest request,
                                           String idempotencyKey) {
        contaService.buscarPorId(contaId);
        FilaMovimento item = new FilaMovimento();
        item.setContaId(contaId);
        item.setTipo(tipo);
        item.setValor(request.getValor());
        item.setStatus(StatusFila.PENDENTE);
        if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
            item.setIdempotencyKey(UUID.randomUUID().toString());
        } else {
            item.setIdempotencyKey(idempotencyKey);
        }
        FilaMovimento salvo = filaRepository.save(item);
        return paraResponse(salvo);
    }

    @Override
    @Transactional
    public FilaMovimento processarProxima() {
        List<FilaMovimento> lote = filaRepository.lockProximasPendentes(
                StatusFila.PENDENTE, PageRequest.of(0, 1));
        if (lote.isEmpty()) {
            return null;
        }
        FilaMovimento item = lote.get(0);
        MovimentoRequest request = new MovimentoRequest();
        request.setValor(item.getValor());
        try {
            if (item.getTipo() == TipoMovimento.DEPOSITO) {
                contaService.depositar(item.getContaId(), request, item.getIdempotencyKey());
            } else {
                contaService.sacar(item.getContaId(), request, item.getIdempotencyKey());
            }
            item.setStatus(StatusFila.PROCESSADO);
            item.setErro(null);
        } catch (RuntimeException ex) {
            item.setStatus(StatusFila.ERRO);
            item.setErro(ex.getMessage());
        }
        return filaRepository.save(item);
    }

    private FilaMovimentoResponse paraResponse(FilaMovimento item) {
        return FilaMovimentoResponse.from(item);
    }
}
