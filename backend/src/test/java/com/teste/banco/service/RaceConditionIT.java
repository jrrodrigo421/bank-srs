package com.teste.banco.service;

import com.teste.banco.domain.StatusConta;
import com.teste.banco.domain.TipoConta;
import com.teste.banco.dto.ContaRequest;
import com.teste.banco.dto.ContaResponse;
import com.teste.banco.dto.MovimentoRequest;
import com.teste.banco.repository.ContaRepository;
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
        System.out.println("[race] saldo 100, 2 threads sacam 100 ao mesmo tempo (SELECT FOR UPDATE)");
        System.out.println("[race] esperado: 1 saque ok, 1 falha, saldo final 0 (nunca negativo)");
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
        System.out.println("[race] saldo final=" + saldo + " | saques que falharam=" + erros.size());
        if (!erros.isEmpty()) {
            System.out.println("[race] erro do perdedor=" + erros.get(0).getMessage());
        }
        assertTrue(saldo.compareTo(BigDecimal.ZERO) >= 0);
        assertEquals(0, saldo.compareTo(BigDecimal.ZERO));
        assertEquals(1, erros.size());
        System.out.println("[race] OK: lock pessimista impediu saldo negativo");
    }
}
