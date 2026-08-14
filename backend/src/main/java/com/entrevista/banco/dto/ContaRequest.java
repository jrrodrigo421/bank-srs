package com.entrevista.banco.dto;

import com.entrevista.banco.domain.StatusConta;
import com.entrevista.banco.domain.TipoConta;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class ContaRequest {

    @NotBlank(message = "Agência é obrigatória")
    @Size(min = 4, max = 4, message = "Agência deve ter 4 dígitos")
    @Pattern(regexp = "\\d{4}", message = "Agência deve conter apenas números")
    private String agencia;

    @NotBlank(message = "Número da conta é obrigatório")
    @Size(max = 10, message = "Número deve ter no máximo 10 dígitos")
    @Pattern(regexp = "\\d+", message = "Número deve conter apenas dígitos")
    private String numero;

    @NotBlank(message = "Titular é obrigatório")
    @Size(max = 120)
    private String titular;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 dígitos, só números")
    private String cpf;

    @NotNull(message = "Tipo é obrigatório")
    private TipoConta tipo;

    @NotNull(message = "Status é obrigatório")
    private StatusConta status;

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
}
