package com.entrevista.banco.repository;

import com.entrevista.banco.domain.FilaMovimento;
import com.entrevista.banco.domain.StatusFila;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface FilaMovimentoRepository extends JpaRepository<FilaMovimento, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "javax.persistence.lock.timeout", value = "-2"))
    @Query("SELECT f FROM FilaMovimento f WHERE f.status = :status ORDER BY f.id ASC")
    List<FilaMovimento> lockProximasPendentes(@Param("status") StatusFila status, Pageable pageable);
}
