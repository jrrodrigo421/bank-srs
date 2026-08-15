package com.teste.banco.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "CONTAS", uniqueConstraints = @UniqueConstraint(columnNames = {"AGENCIA", "NUMERO"}))
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "conta_seq")
    @SequenceGenerator(name = "conta_seq", sequenceName = "SEQ_CONTA", allocationSize = 1)
    private Long id;

    @Column(name = "AGENCIA", nullable = false, length = 4)
    private String agencia;

    @Column(name = "NUMERO", nullable = false, length = 10)
    private String numero;

    @Column(nullable = false, length = 120)
    private String titular;

    @Column(nullable = false, length = 11)
    private String cpf;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoConta tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusConta status;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    @Column(name = "ABERTA_EM", nullable = false)
    private LocalDateTime abertaEm;

    @PrePersist
    public void prePersist() {
        this.abertaEm = LocalDateTime.now();
        if (this.saldo == null) {
            this.saldo = BigDecimal.ZERO;
        }
        if (this.status == null) {
            this.status = StatusConta.ATIVA;
        }
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
