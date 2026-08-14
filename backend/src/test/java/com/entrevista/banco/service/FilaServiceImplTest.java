package com.entrevista.banco.service;

import com.entrevista.banco.domain.FilaMovimento;
import com.entrevista.banco.domain.StatusFila;
import com.entrevista.banco.domain.TipoMovimento;
import com.entrevista.banco.dto.ContaResponse;
import com.entrevista.banco.dto.FilaMovimentoResponse;
import com.entrevista.banco.dto.MovimentoRequest;
import com.entrevista.banco.repository.FilaMovimentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilaServiceImplTest {

    @Mock
    private FilaMovimentoRepository filaRepository;

    @Mock
    private ContaService contaService;

    @InjectMocks
    private FilaServiceImpl filaService;

    private MovimentoRequest movimento;
    private FilaMovimento item;

    @BeforeEach
    void setUp() {
        movimento = new MovimentoRequest();
        movimento.setValor(new BigDecimal("40.00"));

        item = new FilaMovimento();
        item.setId(1L);
        item.setContaId(10L);
        item.setTipo(TipoMovimento.DEPOSITO);
        item.setValor(new BigDecimal("40.00"));
        item.setStatus(StatusFila.PENDENTE);
        item.setIdempotencyKey("fila-1");
    }

    @Test
    void deveEnfileirarComoPendente() {
        System.out.println("[fila] enfileira deposito 40 na conta 10 -> status PENDENTE (ainda nao mexe no saldo)");
        when(contaService.buscarPorId(10L)).thenReturn(new ContaResponse());
        when(filaRepository.save(any(FilaMovimento.class))).thenAnswer(inv -> {
            FilaMovimento f = inv.getArgument(0);
            f.setId(1L);
            return f;
        });

        FilaMovimentoResponse response = filaService.enfileirar(10L, TipoMovimento.DEPOSITO, movimento, "fila-1");

        assertEquals(StatusFila.PENDENTE, response.getStatus());
        assertEquals(TipoMovimento.DEPOSITO, response.getTipo());
        verify(filaRepository).save(any(FilaMovimento.class));
        System.out.println("[fila] OK: status=" + response.getStatus());
    }

    @Test
    void deveProcessarProximaComLock() {
        System.out.println("[fila worker] pega PENDENTE com lock, chama depositar, marca PROCESSADO");
        when(filaRepository.lockProximasPendentes(eq(StatusFila.PENDENTE), any(Pageable.class)))
                .thenReturn(Collections.singletonList(item));
        when(contaService.depositar(eq(10L), any(MovimentoRequest.class), eq("fila-1")))
                .thenReturn(new ContaResponse());
        when(filaRepository.save(any(FilaMovimento.class))).thenAnswer(inv -> inv.getArgument(0));

        FilaMovimento processado = filaService.processarProxima();

        assertEquals(StatusFila.PROCESSADO, processado.getStatus());
        verify(contaService).depositar(eq(10L), any(MovimentoRequest.class), eq("fila-1"));
        System.out.println("[fila worker] OK: status=" + processado.getStatus());
    }

    @Test
    void deveMarcarErroQuandoRegraFalhar() {
        System.out.println("[fila erro] saque na fila falha (saldo insuficiente) -> item vira ERRO, nao some");
        item.setTipo(TipoMovimento.SAQUE);
        when(filaRepository.lockProximasPendentes(eq(StatusFila.PENDENTE), any(Pageable.class)))
                .thenReturn(Collections.singletonList(item));
        when(contaService.sacar(eq(10L), any(MovimentoRequest.class), eq("fila-1")))
                .thenThrow(new com.entrevista.banco.exception.RegraNegocioException("Saldo insuficiente"));
        when(filaRepository.save(any(FilaMovimento.class))).thenAnswer(inv -> inv.getArgument(0));

        FilaMovimento processado = filaService.processarProxima();

        assertEquals(StatusFila.ERRO, processado.getStatus());
        assertEquals("Saldo insuficiente", processado.getErro());
        System.out.println("[fila erro] OK: status=" + processado.getStatus() + " msg=" + processado.getErro());
    }

    @Test
    void deveRetornarNullQuandoFilaVazia() {
        System.out.println("[fila vazia] nenhum PENDENTE -> processarProxima retorna null");
        when(filaRepository.lockProximasPendentes(eq(StatusFila.PENDENTE), any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        assertNull(filaService.processarProxima());
        System.out.println("[fila vazia] OK: null");
    }
}
