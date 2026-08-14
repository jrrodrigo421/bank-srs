package com.entrevista.banco.repository;

import com.entrevista.banco.domain.Conta;
import com.entrevista.banco.domain.StatusConta;
import com.entrevista.banco.domain.TipoConta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    boolean existsByAgenciaAndNumero(String agencia, String numero);

    List<Conta> findByStatus(StatusConta status);

    List<Conta> findByTipo(TipoConta tipo);

    @Query("SELECT COALESCE(SUM(c.saldo), 0) FROM Conta c")
    BigDecimal somarSaldos();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Conta c WHERE c.id = :id")
    Optional<Conta> findByIdForUpdate(@Param("id") Long id);
}
