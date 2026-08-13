export type StatusTarefa = 'PENDENTE' | 'EM_ANDAMENTO' | 'CONCLUIDA';

export interface Tarefa {
  id?: number;
  titulo: string;
  descricao?: string;
  status: StatusTarefa;
  criadoEm?: string;
  atualizadoEm?: string;
}
