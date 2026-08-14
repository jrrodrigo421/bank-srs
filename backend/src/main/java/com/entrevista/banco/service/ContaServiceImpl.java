package com.entrevista.banco.service;

import com.entrevista.banco.domain.Conta;
import com.entrevista.banco.domain.StatusConta;
import com.entrevista.banco.domain.TipoConta;
import com.entrevista.banco.dto.ContaRequest;
import com.entrevista.banco.dto.ContaResponse;
import com.entrevista.banco.dto.MovimentoRequest;
import com.entrevista.banco.exception.RecursoNaoEncontradoException;
import com.entrevista.banco.exception.RegraNegocioException;
import com.entrevista.banco.repository.ContaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContaServiceImpl implements ContaService {

    private final ContaRepository contaRepository;

    public ContaServiceImpl(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
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
    public ContaResponse depositar(Long id, MovimentoRequest request) {
        Conta conta = contaAtiva(id);
        conta.setSaldo(conta.getSaldo().add(request.getValor()));
        return paraResponse(contaRepository.save(conta));
    }

    @Override
    public ContaResponse sacar(Long id, MovimentoRequest request) {
        Conta conta = contaAtiva(id);
        if (conta.getSaldo().compareTo(request.getValor()) < 0) {
            throw new RegraNegocioException("Saldo insuficiente");
        }
        conta.setSaldo(conta.getSaldo().subtract(request.getValor()));
        return paraResponse(contaRepository.save(conta));
    }

    private Conta contaAtiva(Long id) {
        Conta conta = buscarEntidade(id);
        if (conta.getStatus() != StatusConta.ATIVA) {
            throw new RegraNegocioException("Operação permitida apenas em conta ATIVA");
        }
        return conta;
    }

    private Conta buscarEntidade(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada: " + id));
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
