import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Conta, StatusConta, TipoConta } from './models/conta.model';
import { ContaService } from './services/conta.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {

  contas: Conta[] = [];
  totalSaldo = 0;
  form: Conta = this.novaConta();
  editandoId: number | null = null;
  filtroStatus: StatusConta | '' = '';
  filtroTipo: TipoConta | '' = '';
  erro = '';
  carregando = false;

  movimentoId: number | null = null;
  movimentoTipo: 'depositar' | 'sacar' = 'depositar';
  movimentoValor: number | null = null;

  readonly tipos: TipoConta[] = ['CORRENTE', 'POUPANCA'];
  readonly statusList: StatusConta[] = ['ATIVA', 'BLOQUEADA', 'ENCERRADA'];

  constructor(private contaService: ContaService) {}

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando = true;
    this.erro = '';
    this.contaService.listar(this.filtroStatus || undefined, this.filtroTipo || undefined).subscribe({
      next: (dados) => {
        this.contas = dados;
        this.carregando = false;
      },
      error: () => {
        this.erro = 'Falha ao carregar contas. Verifique se a API está no ar.';
        this.carregando = false;
      }
    });
    this.contaService.saldoTotal().subscribe({
      next: (total) => this.totalSaldo = Number(total) || 0
    });
  }

  salvar(): void {
    this.erro = '';
    if (this.editandoId != null) {
      this.contaService.atualizar(this.editandoId, this.form).subscribe({
        next: () => {
          this.limparForm();
          this.carregar();
        },
        error: (e) => this.erro = this.msg(e, 'Erro ao atualizar conta.')
      });
    } else {
      this.contaService.abrir(this.form).subscribe({
        next: () => {
          this.limparForm();
          this.carregar();
        },
        error: (e) => this.erro = this.msg(e, 'Erro ao abrir conta.')
      });
    }
  }

  editar(conta: Conta): void {
    this.editandoId = conta.id ?? null;
    this.form = {
      agencia: conta.agencia,
      numero: conta.numero,
      titular: conta.titular,
      cpf: conta.cpf,
      tipo: conta.tipo,
      status: conta.status
    };
  }

  encerrar(id: number | undefined): void {
    if (id == null || !confirm('Encerrar esta conta? Só é permitido com saldo zero.')) {
      return;
    }
    this.contaService.encerrar(id).subscribe({
      next: () => this.carregar(),
      error: (e) => this.erro = this.msg(e, 'Erro ao encerrar conta.')
    });
  }

  abrirMovimento(conta: Conta, tipo: 'depositar' | 'sacar'): void {
    this.movimentoId = conta.id ?? null;
    this.movimentoTipo = tipo;
    this.movimentoValor = null;
  }

  confirmarMovimento(): void {
    if (this.movimentoId == null || !this.movimentoValor) {
      return;
    }
    const op = this.movimentoTipo === 'depositar'
      ? this.contaService.depositar(this.movimentoId, this.movimentoValor)
      : this.contaService.sacar(this.movimentoId, this.movimentoValor);

    op.subscribe({
      next: () => {
        this.movimentoId = null;
        this.carregar();
      },
      error: (e) => this.erro = this.msg(e, 'Operação recusada.')
    });
  }

  limparForm(): void {
    this.editandoId = null;
    this.form = this.novaConta();
  }

  labelTipo(tipo: TipoConta): string {
    return tipo === 'CORRENTE' ? 'Corrente' : 'Poupança';
  }

  labelStatus(status: StatusConta): string {
    const map: Record<StatusConta, string> = {
      ATIVA: 'Ativa',
      BLOQUEADA: 'Bloqueada',
      ENCERRADA: 'Encerrada'
    };
    return map[status];
  }

  mascararCpf(cpf: string): string {
    if (!cpf || cpf.length !== 11) {
      return cpf;
    }
    return `${cpf.slice(0, 3)}.${cpf.slice(3, 6)}.${cpf.slice(6, 9)}-${cpf.slice(9)}`;
  }

  private novaConta(): Conta {
    return {
      agencia: '0001',
      numero: '',
      titular: '',
      cpf: '',
      tipo: 'CORRENTE',
      status: 'ATIVA'
    };
  }

  private msg(e: { error?: { mensagem?: string } }, fallback: string): string {
    return e.error && e.error.mensagem ? e.error.mensagem : fallback;
  }
}
