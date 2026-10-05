import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { RendicionCajaChicaService } from './rendicion-caja-chica-service';
import {
  CajaChica,
  EstadoRendicion,
  RendicionCajaChica,
  ReporteRendicionesAgregado,
} from './rendicion-caja-chica.model';

@Component({
  selector: 'app-rendicion-caja-chica-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './rendicion-caja-chica-list.html',
  styleUrls: ['./rendicion-caja-chica-list.css'],
})
export class RendicionCajaChicaList implements OnInit {
  private readonly rendicionService = inject(RendicionCajaChicaService);

  // Señales de Estado Principal
  protected readonly rendiciones = signal<RendicionCajaChica[]>([]);
  protected readonly cajasChicas = signal<CajaChica[]>([]);
  protected readonly reporteAgregado = signal<ReporteRendicionesAgregado | null>(null);
  protected readonly loading = signal(false);
  protected readonly loadingAnular = signal(false);

  // Mensajes de Alerta
  protected readonly mensajeExito = signal<string | null>(null);
  protected readonly mensajeError = signal<string | null>(null);

  // Filtros de Búsqueda
  protected filtroEstado = signal<string>('');
  protected filtroCajaChicaId = signal<number | null>(null);
  protected filtroFechaInicio = signal<string>('');
  protected filtroFechaFin = signal<string>('');

  // Control de Visualización Detallada (Modal de Detalle Profundo)
  protected rendicionSeleccionada = signal<RendicionCajaChica | null>(null);
  protected mostrarModalDetalle = signal(false);

  // Control de Transición de Estado (Modal de Anulación)
  protected rendicionParaAnular = signal<RendicionCajaChica | null>(null);
  protected mostrarModalAnular = signal(false);

  ngOnInit(): void {
    this.cargarCajasChicas();
    this.aplicarFiltros();
    this.cargarReporteAgregado();
  }

  cargarCajasChicas(): void {
    this.rendicionService.listarCajasChicas().subscribe({
      next: (data) => this.cajasChicas.set(data),
      error: () => console.warn('No se pudieron cargar las cajas chicas'),
    });
  }

  cargarReporteAgregado(): void {
    this.rendicionService.obtenerReporteAgregado(this.filtroCajaChicaId()).subscribe({
      next: (data) => this.reporteAgregado.set(data),
      error: () => console.warn('No se pudo cargar el reporte agregado'),
    });
  }

  aplicarFiltros(): void {
    this.loading.set(true);
    this.mensajeError.set(null);

    this.rendicionService
      .buscarConFiltros({
        cajaChicaId: this.filtroCajaChicaId() || null,
        estado: this.filtroEstado() || null,
        fechaInicio: this.filtroFechaInicio() || null,
        fechaFin: this.filtroFechaFin() || null,
        size: 50,
      })
      .subscribe({
        next: (page) => {
          this.rendiciones.set(page.content);
          this.loading.set(false);
          this.cargarReporteAgregado();
        },
        error: (err: HttpErrorResponse) => {
          this.loading.set(false);
          this.mensajeError.set(
            err.error?.message || 'Error al filtrar las rendiciones de caja chica.'
          );
        },
      });
  }

  limpiarFiltros(): void {
    this.filtroEstado.set('');
    this.filtroCajaChicaId.set(null);
    this.filtroFechaInicio.set('');
    this.filtroFechaFin.set('');
    this.aplicarFiltros();
  }

  onCajaChicaChange(event: Event): void {
    const val = (event.target as HTMLSelectElement).value;
    this.filtroCajaChicaId.set(val ? Number(val) : null);
  }

  // --- Modal de Consulta y Desglose de Detalle Completo ---

  verDetalle(rendicion: RendicionCajaChica): void {
    // Si la rendición viene sin detalles o para asegurar el detalle completo, podemos consultar
    this.rendicionService.obtenerRendicion(rendicion.id).subscribe({
      next: (r) => {
        this.rendicionSeleccionada.set(r);
        this.mostrarModalDetalle.set(true);
      },
      error: () => {
        this.rendicionSeleccionada.set(rendicion);
        this.mostrarModalDetalle.set(true);
      },
    });
  }

  cerrarModalDetalle(): void {
    this.mostrarModalDetalle.set(false);
    this.rendicionSeleccionada.set(null);
  }

  // --- Transición de Estado con Regla de Negocio (Anular) ---

  abrirModalAnular(rendicion: RendicionCajaChica): void {
    if (rendicion.estado === 'ANULADO') {
      this.mensajeError.set('La rendición ya se encuentra en estado ANULADO.');
      return;
    }
    this.rendicionParaAnular.set(rendicion);
    this.mostrarModalAnular.set(true);
  }

  cerrarModalAnular(): void {
    this.mostrarModalAnular.set(false);
    this.rendicionParaAnular.set(null);
  }

  confirmarAnulacion(): void {
    const item = this.rendicionParaAnular();
    if (!item) return;

    this.loadingAnular.set(true);
    this.mensajeError.set(null);
    this.mensajeExito.set(null);

    this.rendicionService.anularRendicion(item.id).subscribe({
      next: (anulada) => {
        this.loadingAnular.set(false);
        this.cerrarModalAnular();
        this.mensajeExito.set(
          `¡Rendición ${anulada.codigo} ANULADA con éxito! Se restituyeron S/ ${anulada.montoTotal.toFixed(2)} al saldo de la Caja Chica.`
        );
        this.aplicarFiltros();
        this.cargarCajasChicas();
      },
      error: (err: HttpErrorResponse) => {
        this.loadingAnular.set(false);
        this.cerrarModalAnular();
        const status = err.status;
        const msg = err.error?.message || '';

        // Manejo específico del rechazo de reintento / idempotencia
        if (status === 409 || msg.includes('ANULADO') || err.error?.reglaNegocio === 'RN-FIN-02') {
          this.mensajeError.set(
            `REGLA DE NEGOCIO RN-FIN-02: ${msg || 'La transacción ya fue anulada previamente y no puede repetirse.'}`
          );
        } else {
          this.mensajeError.set(msg || 'No se pudo anular la rendición seleccionada.');
        }
      },
    });
  }

  getCajaNombre(cajaChicaId: number): string {
    const caja = this.cajasChicas().find((c) => c.id === cajaChicaId);
    return caja ? `${caja.codigo} (${caja.responsable})` : `Caja #${cajaChicaId}`;
  }

  getEstadoClass(estado: EstadoRendicion): string {
    switch (estado) {
      case 'APROBADO':
        return 'badge-success';
      case 'PENDIENTE':
        return 'badge-warning';
      case 'ANULADO':
        return 'badge-danger';
      case 'RECHAZADO':
        return 'badge-secondary';
      default:
        return 'badge-secondary';
    }
  }
}
