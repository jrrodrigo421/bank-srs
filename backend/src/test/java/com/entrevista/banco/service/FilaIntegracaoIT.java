package com.entrevista.banco.service;

import com.entrevista.banco.domain.FilaMovimento;
import com.entrevista.banco.domain.StatusConta;
import com.entrevista.banco.domain.StatusFila;
import com.entrevista.banco.domain.TipoConta;
import com.entrevista.banco.domain.TipoMovimento;
import com.entrevista.banco.dto.ContaRequest;
import com.entrevista.banco.dto.ContaResponse;
import com.entrevista.banco.dto.MovimentoRequest;
import com.entrevista.banco.repository.ContaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class FilaIntegracaoIT {

    @Autowired
    private ContaService contaService;

    @Autowired
    private FilaService filaService;

    @Autowired
    private ContaRepository contaRepository;

    @Test
    void deveProcessarDepositoDaFila() {
        System.out.println("[fila IT] abre conta, enfileira deposito 25, processa item -> saldo 25 e PROCESSADO");
        ContaRequest cadastro = new ContaRequest();
        cadastro.setAgencia("0088");
        cadastro.setNumero(String.valueOf(System.nanoTime()).substring(0, 10));
        cadastro.setTitular("Fila");
        cadastro.setCpf("55566677788");
        cadastro.setTipo(TipoConta.CORRENTE);
        cadastro.setStatus(StatusConta.ATIVA);
        ContaResponse conta = contaService.abrir(cadastro);

        MovimentoRequest movimento = new MovimentoRequest();
        movimento.setValor(new BigDecimal("25.00"));
        filaService.enfileirar(conta.getId(), TipoMovimento.DEPOSITO, movimento, "fila-dep-1");

        FilaMovimento processado = filaService.processarProxima();

        BigDecimal saldo = contaRepository.findById(conta.getId()).get().getSaldo();
        System.out.println("[fila IT] status=" + processado.getStatus() + " saldo=" + saldo);
        assertNotNull(processado);
        assertEquals(StatusFila.PROCESSADO, processado.getStatus());
        assertEquals(0, new BigDecimal("25.00").compareTo(saldo));
        System.out.println("[fila IT] OK");
    }
}
