package com.entrevista.banco.dto;

import com.entrevista.banco.domain.FilaMovimento;
import com.entrevista.banco.domain.StatusFila;
import com.entrevista.banco.domain.TipoMovimento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FilaMovimentoResponse {

    private Long id;
    private Long contaId;
    private TipoMovimento tipo;
    private BigDecimal valor;
    private StatusFila status;
    private String idempotencyKey;
    private String erro;
    private LocalDateTime criadoEm;

    public FilaMovimentoResponse() {
    }

    public static FilaMovimentoResponse from(FilaMovimento item) {
        FilaMovimentoResponse response = new FilaMovimentoResponse();
        response.setId(item.getId());
        response.setContaId(item.getContaId());
        response.setTipo(item.getTipo());
        response.setValor(item.getValor());
        response.setStatus(item.getStatus());
        response.setIdempotencyKey(item.getIdempotencyKey());
        response.setErro(item.getErro());
        response.setCriadoEm(item.getCriadoEm());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContaId() {
        return contaId;
    }

    public void setContaId(Long contaId) {
        this.contaId = contaId;
    }

    public TipoMovimento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimento tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public StatusFila getStatus() {
        return status;
    }

    public void setStatus(StatusFila status) {
        this.status = status;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
