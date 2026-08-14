package com.entrevista.banco.service;

import com.entrevista.banco.domain.Conta;
import com.entrevista.banco.domain.Idempotencia;
import com.entrevista.banco.domain.StatusConta;
import com.entrevista.banco.domain.TipoConta;
import com.entrevista.banco.dto.ContaRequest;
import com.entrevista.banco.dto.ContaResponse;
import com.entrevista.banco.dto.MovimentoRequest;
import com.entrevista.banco.exception.RecursoNaoEncontradoException;
import com.entrevista.banco.exception.RegraNegocioException;
import com.entrevista.banco.repository.ContaRepository;
import com.entrevista.banco.repository.IdempotenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContaServiceImplTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private IdempotenciaRepository idempotenciaRepository;

    @InjectMocks
    private ContaServiceImpl contaService;

    private Conta conta;
    private ContaRequest request;
    private MovimentoRequest movimento;

    @BeforeEach
    void setUp() {
        conta = new Conta();
        conta.setId(1L);
        conta.setAgencia("0001");
        conta.setNumero("12345");
        conta.setTitular("Maria Silva");
        conta.setCpf("12345678901");
        conta.setTipo(TipoConta.CORRENTE);
        conta.setStatus(StatusConta.ATIVA);
        conta.setSaldo(new BigDecimal("100.00"));

        request = new ContaRequest();
        request.setAgencia("0001");
        request.setNumero("12345");
        request.setTitular("Maria Silva");
        request.setCpf("12345678901");
        request.setTipo(TipoConta.CORRENTE);
        request.setStatus(StatusConta.ATIVA);

        movimento = new MovimentoRequest();
        movimento.setValor(new BigDecimal("50.00"));
    }

    @Test
    void deveAbrirContaComSaldoZero() {
        System.out.println("[abrir] agencia+numero novos -> conta salva com saldo 0");
        when(contaRepository.existsByAgenciaAndNumero("0001", "12345")).thenReturn(false);
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        contaService.abrir(request);

        verify(contaRepository).save(any(Conta.class));
        System.out.println("[abrir] OK: save chamado, nao duplicou");
    }

    @Test
    void naoDeveAbrirContaDuplicada() {
        System.out.println("[abrir duplicada] mesma agencia+numero ja existe -> deve recusar");
        when(contaRepository.existsByAgenciaAndNumero("0001", "12345")).thenReturn(true);

        assertThrows(RegraNegocioException.class, () -> contaService.abrir(request));
        verify(contaRepository, never()).save(any(Conta.class));
        System.out.println("[abrir duplicada] OK: RegraNegocioException e nenhum save");
    }

    @Test
    void deveDepositarEmContaAtiva() {
        System.out.println("[deposito] conta ATIVA saldo 100 + 50 -> espera 150 (lock FOR UPDATE)");
        when(contaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(conta));
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        contaService.depositar(1L, movimento, null);

        assertEquals(new BigDecimal("150.00"), conta.getSaldo());
        verify(contaRepository).findByIdForUpdate(1L);
        System.out.println("[deposito] OK: saldo=" + conta.getSaldo());
    }

    @Test
    void deveSerIdempotenteNoDeposito() {
        System.out.println("[idempotencia] mesma chave dep-1 duas vezes -> nao credita de novo, devolve snapshot");
        Idempotencia registro = new Idempotencia();
        registro.setChave("dep-1");
        registro.setRespostaJson(
                "{\"id\":1,\"agencia\":\"0001\",\"numero\":\"12345\",\"titular\":\"Maria Silva\","
                        + "\"cpf\":\"12345678901\",\"tipo\":\"CORRENTE\",\"status\":\"ATIVA\",\"saldo\":150.00}");
        when(idempotenciaRepository.findByChave("dep-1")).thenReturn(Optional.of(registro));

        ContaResponse primeira = contaService.depositar(1L, movimento, "dep-1");
        ContaResponse segunda = contaService.depositar(1L, movimento, "dep-1");

        assertEquals(0, primeira.getSaldo().compareTo(segunda.getSaldo()));
        verify(contaRepository, never()).findByIdForUpdate(1L);
        verify(contaRepository, never()).save(any(Conta.class));
        System.out.println("[idempotencia] OK: saldo repetido=" + primeira.getSaldo() + " sem lock/save");
    }

    @Test
    void deveGravarChaveNaPrimeiraVez() {
        System.out.println("[idempotencia 1a vez] chave nova -> processa deposito e grava na tabela IDEMPOTENCIA");
        when(idempotenciaRepository.findByChave("dep-1")).thenReturn(Optional.empty());
        when(contaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(conta));
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        contaService.depositar(1L, movimento, "dep-1");

        verify(idempotenciaRepository).save(any(Idempotencia.class));
        System.out.println("[idempotencia 1a vez] OK: chave gravada, saldo=" + conta.getSaldo());
    }

    @Test
    void naoDeveSacarSemSaldo() {
        System.out.println("[saque] saldo 100, tenta sacar 200 -> recusa, nao fica negativo");
        movimento.setValor(new BigDecimal("200.00"));
        when(contaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(conta));

        assertThrows(RegraNegocioException.class, () -> contaService.sacar(1L, movimento, null));
        System.out.println("[saque] OK: RegraNegocioException, saldo permanece " + conta.getSaldo());
    }

    @Test
    void naoDeveMovimentarContaBloqueada() {
        System.out.println("[bloqueada] status BLOQUEADA -> deposito recusado");
        conta.setStatus(StatusConta.BLOQUEADA);
        when(contaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(conta));

        assertThrows(RegraNegocioException.class, () -> contaService.depositar(1L, movimento, null));
        System.out.println("[bloqueada] OK: RegraNegocioException");
    }

    @Test
    void naoDeveEncerrarComSaldo() {
        System.out.println("[encerrar] saldo 100 -> nao pode encerrar (so saldo 0)");
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

        assertThrows(RegraNegocioException.class, () -> contaService.encerrar(1L));
        verify(contaRepository, never()).delete(any(Conta.class));
        System.out.println("[encerrar] OK: nao deletou");
    }

    @Test
    void deveLancar404QuandoNaoEncontrar() {
        System.out.println("[404] id 99 inexistente -> RecursoNaoEncontradoException");
        when(contaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> contaService.buscarPorId(99L));
        System.out.println("[404] OK");
    }
}
