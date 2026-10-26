import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiService } from '../../core/services/api-service';
import {
  Insumo,
  InsumoCreateRequest,
  RecetaBOM,
  RecetaBomCreateRequest,
  OrdenProduccion,
  CrearOrdenRequest,
  CompletarOrdenRequest,
} from './produccion.model';

@Injectable({ providedIn: 'root' })
export class ProduccionService {
  private readonly http = inject(HttpClient);
  private readonly api = inject(ApiService);

  private readonly insumosUrl = '/api/v1/produccion/insumos';
  private readonly recetasUrl = '/api/v1/produccion/recetas';
  private readonly ordenesUrl = '/api/v1/produccion/ordenes';

  // Insumos
  listarInsumos(): Observable<Insumo[]> {
    return this.http.get<Insumo[]>(this.api.buildUrl(this.insumosUrl));
  }

  crearInsumo(req: InsumoCreateRequest): Observable<Insumo> {
    return this.http.post<Insumo>(this.api.buildUrl(this.insumosUrl), req);
  }

  // Recetas BOM
  listarRecetas(): Observable<RecetaBOM[]> {
    return this.http.get<RecetaBOM[]>(this.api.buildUrl(this.recetasUrl));
  }

  obtenerReceta(id: number): Observable<RecetaBOM> {
    return this.http.get<RecetaBOM>(this.api.buildUrl(`${this.recetasUrl}/${id}`));
  }

  crearReceta(req: RecetaBomCreateRequest): Observable<RecetaBOM> {
    return this.http.post<RecetaBOM>(this.api.buildUrl(this.recetasUrl), req);
  }

  // Órdenes de Producción
  listarOrdenes(): Observable<OrdenProduccion[]> {
    return this.http.get<OrdenProduccion[]>(this.api.buildUrl(this.ordenesUrl));
  }

  obtenerOrden(id: number): Observable<OrdenProduccion> {
    return this.http.get<OrdenProduccion>(this.api.buildUrl(`${this.ordenesUrl}/${id}`));
  }

  crearOrden(req: CrearOrdenRequest): Observable<OrdenProduccion> {
    return this.http.post<OrdenProduccion>(this.api.buildUrl(this.ordenesUrl), req);
  }

  completarOrden(id: number, req?: CompletarOrdenRequest): Observable<OrdenProduccion> {
    return this.http.patch<OrdenProduccion>(this.api.buildUrl(`${this.ordenesUrl}/${id}/completar`), req || {});
  }

  cancelarOrden(id: number, motivo: string): Observable<OrdenProduccion> {
    return this.http.patch<OrdenProduccion>(this.api.buildUrl(`${this.ordenesUrl}/${id}/cancelar?motivo=${encodeURIComponent(motivo)}`), {});
  }
}
