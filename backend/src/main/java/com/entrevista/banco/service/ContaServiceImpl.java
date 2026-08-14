package com.entrevista.banco.service;

import com.entrevista.banco.domain.Conta;
import com.entrevista.banco.domain.Idempotencia;
import com.entrevista.banco.domain.StatusConta;
import com.entrevista.banco.domain.TipoConta;
import com.entrevista.banco.dto.ContaRequest;
import com.entrevista.banco.dto.ContaResponse;
import com.entrevista.banco.dto.MovimentoRequest;
import com.entrevista.banco.exception.RecursoNaoEncontradoException;
import com.entrevista.banco.exception.RegraNegocioException;
import com.entrevista.banco.repository.ContaRepository;
import com.entrevista.banco.repository.IdempotenciaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContaServiceImpl implements ContaService {

    private final ContaRepository contaRepository;
    private final IdempotenciaRepository idempotenciaRepository;
    private final ObjectMapper objectMapper;

    public ContaServiceImpl(ContaRepository contaRepository, IdempotenciaRepository idempotenciaRepository) {
        this.contaRepository = contaRepository;
        this.idempotenciaRepository = idempotenciaRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public ContaResponse abrir(ContaRequest request) {
        if (contaRepository.existsByAgenciaAndNumero(request.getAgencia(), request.getNumero())) {
            throw new RegraNegocioException("Já existe conta nesta agência e número");
        }
        Conta conta = new Conta();
        mapearCadastro(request, conta);
        conta.setSaldo(BigDecimal.ZERO);
        return paraResponse(contaRepository.save(conta));
    }

    @Override
    @Transactional(readOnly = true)
    public ContaResponse buscarPorId(Long id) {
        return paraResponse(buscarEntidade(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContaResponse> listar(StatusConta status, TipoConta tipo) {
        List<Conta> contas;
        if (status != null) {
            contas = contaRepository.findByStatus(status);
        } else if (tipo != null) {
            contas = contaRepository.findByTipo(tipo);
        } else {
            contas = contaRepository.findAll();
        }
        return contas.stream().map(this::paraResponse).collect(Collectors.toList());
    }

    @Override
    public ContaResponse atualizar(Long id, ContaRequest request) {
        Conta conta = buscarEntidade(id);
        if (!request.getAgencia().equals(conta.getAgencia())
                || !request.getNumero().equals(conta.getNumero())) {
            if (contaRepository.existsByAgenciaAndNumero(request.getAgencia(), request.getNumero())) {
                throw new RegraNegocioException("Já existe conta nesta agência e número");
            }
        }
        mapearCadastro(request, conta);
        return paraResponse(contaRepository.save(conta));
    }

    @Override
    public void encerrar(Long id) {
        Conta conta = buscarEntidade(id);
        if (conta.getSaldo().compareTo(BigDecimal.ZERO) != 0) {
            throw new RegraNegocioException("Não é possível encerrar conta com saldo diferente de zero");
        }
        contaRepository.delete(conta);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ContaResponse depositar(Long id, MovimentoRequest request, String idempotencyKey) {
        ContaResponse cached = respostaIdempotente(idempotencyKey);
        if (cached != null) {
            return cached;
        }
        Conta conta = contaAtivaComLock(id);
        conta.setSaldo(conta.getSaldo().add(request.getValor()));
        ContaResponse response = paraResponse(contaRepository.save(conta));
        gravarIdempotencia(idempotencyKey, response);
        return response;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ContaResponse sacar(Long id, MovimentoRequest request, String idempotencyKey) {
        ContaResponse cached = respostaIdempotente(idempotencyKey);
        if (cached != null) {
            return cached;
        }
        Conta conta = contaAtivaComLock(id);
        if (conta.getSaldo().compareTo(request.getValor()) < 0) {
            throw new RegraNegocioException("Saldo insuficiente");
        }
        conta.setSaldo(conta.getSaldo().subtract(request.getValor()));
        ContaResponse response = paraResponse(contaRepository.save(conta));
        gravarIdempotencia(idempotencyKey, response);
        return response;
    }

    private Conta contaAtivaComLock(Long id) {
        Conta conta = contaRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada: " + id));
        if (conta.getStatus() != StatusConta.ATIVA) {
            throw new RegraNegocioException("Operação permitida apenas em conta ATIVA");
        }
        return conta;
    }

    private Conta buscarEntidade(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada: " + id));
    }

    private ContaResponse respostaIdempotente(String chave) {
        if (chave == null || chave.trim().isEmpty()) {
            return null;
        }
        java.util.Optional<Idempotencia> existente = idempotenciaRepository.findByChave(chave);
        if (!existente.isPresent()) {
            return null;
        }
        try {
            return objectMapper.readValue(existente.get().getRespostaJson(), ContaResponse.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Falha ao ler resposta idempotente", e);
        }
    }

    private void gravarIdempotencia(String chave, ContaResponse response) {
        if (chave == null || chave.trim().isEmpty()) {
            return;
        }
        try {
            Idempotencia registro = new Idempotencia();
            registro.setChave(chave);
            registro.setRespostaJson(objectMapper.writeValueAsString(response));
            idempotenciaRepository.save(registro);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Falha ao gravar idempotência", e);
        }
    }

    private void mapearCadastro(ContaRequest request, Conta conta) {
        conta.setAgencia(request.getAgencia());
        conta.setNumero(request.getNumero());
        conta.setTitular(request.getTitular());
        conta.setCpf(request.getCpf());
        conta.setTipo(request.getTipo());
        conta.setStatus(request.getStatus());
    }

    private ContaResponse paraResponse(Conta conta) {
        return new ContaResponse(
                conta.getId(),
                conta.getAgencia(),
                conta.getNumero(),
                conta.getTitular(),
                conta.getCpf(),
                conta.getTipo(),
                conta.getStatus(),
                conta.getSaldo(),
                conta.getAbertaEm()
        );
    }
}
