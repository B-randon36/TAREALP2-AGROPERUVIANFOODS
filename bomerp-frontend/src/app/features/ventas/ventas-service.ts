import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { VentaCreateRequest, VentaResponse } from './ventas.model';

@Injectable({
  providedIn: 'root',
})
export class VentasService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/v1/ventas';

  registrarVenta(request: VentaCreateRequest): Observable<VentaResponse> {
    return this.http.post<VentaResponse>(this.apiUrl, request);
  }

  listarVentas(): Observable<VentaResponse[]> {
    return this.http.get<VentaResponse[]>(this.apiUrl);
  }

  obtenerVentaPorId(id: number): Observable<VentaResponse> {
    return this.http.get<VentaResponse>(`${this.apiUrl}/${id}`);
  }

  obtenerVentaPorCodigo(codigo: string): Observable<VentaResponse> {
    return this.http.get<VentaResponse>(`${this.apiUrl}/codigo/${codigo}`);
  }
}
