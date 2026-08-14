import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Conta, StatusConta, TipoConta } from '../models/conta.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ContaService {

  private readonly apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  listar(status?: StatusConta, tipo?: TipoConta): Observable<Conta[]> {
    const params: string[] = [];
    if (status) {
      params.push(`status=${status}`);
    }
    if (tipo) {
      params.push(`tipo=${tipo}`);
    }
    const query = params.length ? `?${params.join('&')}` : '';
    return this.http.get<Conta[]>(`${this.apiUrl}${query}`);
  }

  saldoTotal(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/saldo-total`);
  }

  abrir(conta: Conta): Observable<Conta> {
    return this.http.post<Conta>(this.apiUrl, conta);
  }

  atualizar(id: number, conta: Conta): Observable<Conta> {
    return this.http.put<Conta>(`${this.apiUrl}/${id}`, conta);
  }

  encerrar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  depositar(id: number, valor: number): Observable<Conta> {
    return this.http.post<Conta>(`${this.apiUrl}/${id}/depositar`, { valor });
  }

  sacar(id: number, valor: number): Observable<Conta> {
    return this.http.post<Conta>(`${this.apiUrl}/${id}/sacar`, { valor });
  }
}
