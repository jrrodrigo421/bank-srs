package com.entrevista.banco.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "IDEMPOTENCIA")
public class Idempotencia {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idem_seq")
    @SequenceGenerator(name = "idem_seq", sequenceName = "SEQ_IDEMPOTENCIA", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String chave;

    @Column(name = "RESPOSTA_JSON", nullable = false, length = 2000)
    private String respostaJson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getRespostaJson() {
        return respostaJson;
    }

    public void setRespostaJson(String respostaJson) {
        this.respostaJson = respostaJson;
    }
}
