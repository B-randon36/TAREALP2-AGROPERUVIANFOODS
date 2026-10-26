import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiService } from '../../core/services/api-service';
import { Compra, RegistrarCompraRequest } from './compras.model';

@Injectable({ providedIn: 'root' })
export class ComprasService {
  private readonly http = inject(HttpClient);
  private readonly api = inject(ApiService);
  private readonly basePath = '/api/v1/compras';

  listar(): Observable<Compra[]> {
    return this.http.get<Compra[]>(this.api.buildUrl(this.basePath));
  }

  obtenerPorId(id: number): Observable<Compra> {
    return this.http.get<Compra>(this.api.buildUrl(`${this.basePath}/${id}`));
  }

  registrar(req: RegistrarCompraRequest): Observable<Compra> {
    return this.http.post<Compra>(this.api.buildUrl(this.basePath), req);
  }

  recibir(id: number): Observable<Compra> {
    return this.http.patch<Compra>(this.api.buildUrl(`${this.basePath}/${id}/recibir`), {});
  }

  anular(id: number, motivo: string): Observable<Compra> {
    return this.http.patch<Compra>(this.api.buildUrl(`${this.basePath}/${id}/anular?motivo=${encodeURIComponent(motivo)}`), {});
  }
}
