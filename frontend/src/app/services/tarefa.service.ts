import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Tarefa, StatusTarefa } from '../models/tarefa.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class TarefaService {

  private readonly apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  listar(status?: StatusTarefa): Observable<Tarefa[]> {
    const params = status ? `?status=${status}` : '';
    return this.http.get<Tarefa[]>(`${this.apiUrl}${params}`);
  }

  buscarPorId(id: number): Observable<Tarefa> {
    return this.http.get<Tarefa>(`${this.apiUrl}/${id}`);
  }

  criar(tarefa: Tarefa): Observable<Tarefa> {
    return this.http.post<Tarefa>(this.apiUrl, tarefa);
  }

  atualizar(id: number, tarefa: Tarefa): Observable<Tarefa> {
    return this.http.put<Tarefa>(`${this.apiUrl}/${id}`, tarefa);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
