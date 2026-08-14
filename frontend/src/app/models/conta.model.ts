export type TipoConta = 'CORRENTE' | 'POUPANCA';
export type StatusConta = 'ATIVA' | 'BLOQUEADA' | 'ENCERRADA';

export interface Conta {
  id?: number;
  agencia: string;
  numero: string;
  titular: string;
  cpf: string;
  tipo: TipoConta;
  status: StatusConta;
  saldo?: number;
  abertaEm?: string;
}
