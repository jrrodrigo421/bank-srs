package com.entrevista.banco.repository;

import com.entrevista.banco.domain.Conta;
import com.entrevista.banco.domain.StatusConta;
import com.entrevista.banco.domain.TipoConta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    boolean existsByAgenciaAndNumero(String agencia, String numero);

    List<Conta> findByStatus(StatusConta status);

    List<Conta> findByTipo(TipoConta tipo);
}
