package com.teste.banco.repository;

import com.teste.banco.domain.Idempotencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdempotenciaRepository extends JpaRepository<Idempotencia, Long> {

    Optional<Idempotencia> findByChave(String chave);
}
