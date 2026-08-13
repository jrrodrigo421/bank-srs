import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Tarefa, StatusTarefa } from './models/tarefa.model';
import { TarefaService } from './services/tarefa.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {

  tarefas: Tarefa[] = [];
  tarefaForm: Tarefa = this.novaTarefa();
  editandoId: number | null = null;
  filtroStatus: StatusTarefa | '' = '';
  erro = '';
  carregando = false;

  readonly statusOptions: StatusTarefa[] = ['PENDENTE', 'EM_ANDAMENTO', 'CONCLUIDA'];

  constructor(private tarefaService: TarefaService) {}

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando = true;
    this.erro = '';
    const status = this.filtroStatus || undefined;
    this.tarefaService.listar(status).subscribe({
      next: (dados) => {
        this.tarefas = dados;
        this.carregando = false;
      },
      error: () => {
        this.erro = 'Falha ao carregar tarefas. Verifique se a API está no ar.';
        this.carregando = false;
      }
    });
  }

  salvar(): void {
    if (!this.tarefaForm.titulo.trim()) {
      this.erro = 'Título é obrigatório.';
      return;
    }
    this.erro = '';

    if (this.editandoId != null) {
      this.tarefaService.atualizar(this.editandoId, this.tarefaForm).subscribe({
        next: () => {
          this.limparForm();
          this.carregar();
        },
        error: () => this.erro = 'Erro ao atualizar tarefa.'
      });
    } else {
      this.tarefaService.criar(this.tarefaForm).subscribe({
        next: () => {
          this.limparForm();
          this.carregar();
        },
        error: () => this.erro = 'Erro ao criar tarefa.'
      });
    }
  }

  editar(tarefa: Tarefa): void {
    this.editandoId = tarefa.id ?? null;
    this.tarefaForm = {
      titulo: tarefa.titulo,
      descricao: tarefa.descricao || '',
      status: tarefa.status
    };
  }

  excluir(id: number | undefined): void {
    if (id == null || !confirm('Excluir esta tarefa?')) {
      return;
    }
    this.tarefaService.excluir(id).subscribe({
      next: () => this.carregar(),
      error: () => this.erro = 'Erro ao excluir tarefa.'
    });
  }

  limparForm(): void {
    this.editandoId = null;
    this.tarefaForm = this.novaTarefa();
  }

  labelStatus(status: StatusTarefa): string {
    const map: Record<StatusTarefa, string> = {
      PENDENTE: 'Pendente',
      EM_ANDAMENTO: 'Em andamento',
      CONCLUIDA: 'Concluída'
    };
    return map[status];
  }

  private novaTarefa(): Tarefa {
    return { titulo: '', descricao: '', status: 'PENDENTE' };
  }
}
