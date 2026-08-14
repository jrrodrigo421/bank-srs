package com.entrevista.banco.controller;

import com.entrevista.banco.domain.StatusConta;
import com.entrevista.banco.domain.TipoConta;
import com.entrevista.banco.dto.ContaRequest;
import com.entrevista.banco.dto.ContaResponse;
import com.entrevista.banco.dto.MovimentoRequest;
import com.entrevista.banco.exception.RegraNegocioException;
import com.entrevista.banco.service.ContaService;
import com.entrevista.banco.service.FilaService;
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
        ContaRequest request = requestValido();
        ContaResponse response = new ContaResponse(
                1L, "0001", "12345", "Maria Silva", "12345678901",
                TipoConta.CORRENTE, StatusConta.ATIVA, BigDecimal.ZERO, LocalDateTime.now());

        when(contaService.abrir(any(ContaRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.agencia").value("0001"))
                .andExpect(jsonPath("$.saldo").value(0));
    }

    @Test
    void deveListarContas() throws Exception {
        ContaResponse response = new ContaResponse(
                1L, "0001", "12345", "Maria Silva", "12345678901",
                TipoConta.CORRENTE, StatusConta.ATIVA, new BigDecimal("10.00"), LocalDateTime.now());
        when(contaService.listar(null, null)).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/contas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titular").value("Maria Silva"));
    }

    @Test
    void deveDepositar() throws Exception {
        MovimentoRequest movimento = new MovimentoRequest();
        movimento.setValor(new BigDecimal("50.00"));
        ContaResponse response = new ContaResponse(
                1L, "0001", "12345", "Maria Silva", "12345678901",
                TipoConta.CORRENTE, StatusConta.ATIVA, new BigDecimal("50.00"), LocalDateTime.now());

        when(contaService.depositar(eq(1L), any(MovimentoRequest.class), any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/contas/1/depositar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movimento)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(50.00));
    }

    @Test
    void deveRetornar400QuandoSaldoInsuficiente() throws Exception {
        MovimentoRequest movimento = new MovimentoRequest();
        movimento.setValor(new BigDecimal("50.00"));
        when(contaService.sacar(eq(1L), any(MovimentoRequest.class), any()))
                .thenThrow(new RegraNegocioException("Saldo insuficiente"));

        mockMvc.perform(post("/api/contas/1/sacar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movimento)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoCpfInvalido() throws Exception {
        ContaRequest request = requestValido();
        request.setCpf("123");

        mockMvc.perform(post("/api/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
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
}
