package com.entrevista.banco.service;

import com.entrevista.banco.domain.StatusConta;
import com.entrevista.banco.domain.TipoConta;
import com.entrevista.banco.dto.ContaRequest;
import com.entrevista.banco.dto.ContaResponse;
import com.entrevista.banco.dto.MovimentoRequest;
import com.entrevista.banco.repository.ContaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RaceConditionIT {

    @Autowired
    private ContaService contaService;

    @Autowired
    private ContaRepository contaRepository;

    @Test
    void doisSaquesConcorrentesNaoDeixamSaldoNegativo() throws Exception {
        ContaRequest cadastro = new ContaRequest();
        cadastro.setAgencia("0099");
        cadastro.setNumero(String.valueOf(System.nanoTime()).substring(0, 10));
        cadastro.setTitular("Corrida");
        cadastro.setCpf("11122233344");
        cadastro.setTipo(TipoConta.CORRENTE);
        cadastro.setStatus(StatusConta.ATIVA);
        ContaResponse conta = contaService.abrir(cadastro);

        MovimentoRequest deposito = new MovimentoRequest();
        deposito.setValor(new BigDecimal("100.00"));
        contaService.depositar(conta.getId(), deposito, UUID.randomUUID().toString());

        MovimentoRequest saque = new MovimentoRequest();
        saque.setValor(new BigDecimal("100.00"));

        int threads = 2;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        List<Throwable> erros = Collections.synchronizedList(new ArrayList<Throwable>());

        for (int i = 0; i < threads; i++) {
            final String chave = "saque-" + i;
            pool.submit(() -> {
                try {
                    start.await();
                    contaService.sacar(conta.getId(), saque, chave);
                } catch (Exception e) {
                    erros.add(e);
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        assertTrue(done.await(15, TimeUnit.SECONDS));
        pool.shutdown();

        BigDecimal saldo = contaRepository.findById(conta.getId()).get().getSaldo();
        assertTrue(saldo.compareTo(BigDecimal.ZERO) >= 0);
        assertEquals(0, saldo.compareTo(BigDecimal.ZERO));
        assertEquals(1, erros.size());
    }
}
