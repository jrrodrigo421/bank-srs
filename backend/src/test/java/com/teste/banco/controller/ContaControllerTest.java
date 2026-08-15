package com.teste.banco.controller;

import com.teste.banco.domain.StatusConta;
import com.teste.banco.domain.TipoConta;
import com.teste.banco.dto.ContaRequest;
import com.teste.banco.dto.ContaResponse;
import com.teste.banco.dto.MovimentoRequest;
import com.teste.banco.exception.RegraNegocioException;
import com.teste.banco.service.ContaService;
import com.teste.banco.service.FilaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContaController.class)
class ContaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ContaService contaService;

    @MockBean
    private FilaService filaService;

    @Test
    void deveAbrirConta() throws Exception {
        System.out.println("[API POST /contas] abrir conta -> 201 e saldo 0");
        ContaRequest request = requestValido();
        ContaResponse response = conta("0001", BigDecimal.ZERO);

        when(contaService.abrir(any(ContaRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.agencia").value("0001"))
                .andExpect(jsonPath("$.saldo").value(0));
        System.out.println("[API POST /contas] OK: 201 Created");
    }

    @Test
    void deveListarContas() throws Exception {
        System.out.println("[API GET /contas] listar -> 200 com titular");
        ContaResponse response = conta("0001", new BigDecimal("10.00"));
        when(contaService.listar(null, null)).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/contas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titular").value("Maria Silva"));
        System.out.println("[API GET /contas] OK");
    }

    @Test
    void deveDepositar() throws Exception {
        System.out.println("[API POST /contas/1/depositar] +50 -> 200 saldo 50");
        MovimentoRequest movimento = new MovimentoRequest();
        movimento.setValor(new BigDecimal("50.00"));
        ContaResponse response = conta("0001", new BigDecimal("50.00"));

        when(contaService.depositar(eq(1L), any(MovimentoRequest.class), any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/contas/1/depositar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movimento)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(50.00));
        System.out.println("[API depositar] OK");
    }

    @Test
    void deveRetornar400QuandoSaldoInsuficiente() throws Exception {
        System.out.println("[API POST /sacar] saldo insuficiente -> 400");
        MovimentoRequest movimento = new MovimentoRequest();
        movimento.setValor(new BigDecimal("50.00"));
        when(contaService.sacar(eq(1L), any(MovimentoRequest.class), any()))
                .thenThrow(new RegraNegocioException("Saldo insuficiente"));

        mockMvc.perform(post("/api/contas/1/sacar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movimento)))
                .andExpect(status().isBadRequest());
        System.out.println("[API sacar] OK: 400");
    }

    @Test
    void deveRetornar400QuandoCpfInvalido() throws Exception {
        System.out.println("[API POST /contas] CPF 123 (invalido) -> 400 Bean Validation");
        ContaRequest request = requestValido();
        request.setCpf("123");

        mockMvc.perform(post("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        System.out.println("[API CPF] OK: 400");
    }

    private ContaRequest requestValido() {
        ContaRequest request = new ContaRequest();
        request.setAgencia("0001");
        request.setNumero("12345");
        request.setTitular("Maria Silva");
        request.setCpf("12345678901");
        request.setTipo(TipoConta.CORRENTE);
        request.setStatus(StatusConta.ATIVA);
        return request;
    }

    private ContaResponse conta(String agencia, BigDecimal saldo) {
        ContaResponse response = new ContaResponse();
        response.setId(1L);
        response.setAgencia(agencia);
        response.setNumero("12345");
        response.setTitular("Maria Silva");
        response.setCpf("12345678901");
        response.setTipo(TipoConta.CORRENTE);
        response.setStatus(StatusConta.ATIVA);
        response.setSaldo(saldo);
        response.setAbertaEm(LocalDateTime.now());
        return response;
    }
}
