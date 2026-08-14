package com.entrevista.banco.dto;

import com.entrevista.banco.domain.StatusConta;
import com.entrevista.banco.domain.TipoConta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ContaResponse {

    private Long id;
    private String agencia;
    private String numero;
    private String titular;
    private String cpf;
    private TipoConta tipo;
    private StatusConta status;
    private BigDecimal saldo;
    private LocalDateTime abertaEm;

    public ContaResponse() {
    }

    public ContaResponse(Long id, String agencia, String numero, String titular, String cpf,
                         TipoConta tipo, StatusConta status, BigDecimal saldo, LocalDateTime abertaEm) {
        this.id = id;
        this.agencia = agencia;
        this.numero = numero;
        this.titular = titular;
        this.cpf = cpf;
        this.tipo = tipo;
        this.status = status;
        this.saldo = saldo;
        this.abertaEm = abertaEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public TipoConta getTipo() {
        return tipo;
    }

    public void setTipo(TipoConta tipo) {
        this.tipo = tipo;
    }

    public StatusConta getStatus() {
        return status;
    }

    public void setStatus(StatusConta status) {
        this.status = status;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public LocalDateTime getAbertaEm() {
        return abertaEm;
    }

    public void setAbertaEm(LocalDateTime abertaEm) {
        this.abertaEm = abertaEm;
    }
}
