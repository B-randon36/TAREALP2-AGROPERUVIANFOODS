import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProduccionService } from './produccion-service';
import { OrdenProduccion, EstadoOrdenProduccion } from './produccion.model';

@Component({
  selector: 'app-orden-produccion-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './orden-produccion-list.html',
  styleUrls: ['./orden-produccion-list.css'],
})
export class OrdenProduccionList implements OnInit {
  private readonly produccionService = inject(ProduccionService);

  readonly ordenes = signal<OrdenProduccion[]>([]);
  readonly cargando = signal<boolean>(true);
  readonly error = signal<string | null>(null);
  readonly exito = signal<string | null>(null);

  // Modal para completar orden
  readonly ordenSeleccionada = signal<OrdenProduccion | null>(null);
  readonly completando = signal<boolean>(false);
  observacionCierre = '';
  fechaVencimientoPersonalizada = '';

  ngOnInit(): void {
    this.cargarOrdenes();
  }

  cargarOrdenes(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.produccionService.listarOrdenes().subscribe({
      next: (data) => {
        this.ordenes.set(data);
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set('No se pudieron cargar las órdenes de producción: ' + (err.error?.message || err.message));
        this.cargando.set(false);
      },
    });
  }

  abrirModalCompletar(orden: OrdenProduccion): void {
    this.ordenSeleccionada.set(orden);
    this.observacionCierre = 'Control de calidad conforme. Envasado y sellado hermético verificado.';
    this.fechaVencimientoPersonalizada = '';
  }

  cerrarModal(): void {
    this.ordenSeleccionada.set(null);
  }

  confirmarCompletar(): void {
    const orden = this.ordenSeleccionada();
    if (!orden) return;

    this.completando.set(true);
    this.produccionService.completarOrden(orden.id, {
      observaciones: this.observacionCierre,
      fechaVencimientoPersonalizada: this.fechaVencimientoPersonalizada || undefined,
    }).subscribe({
      next: (resp) => {
        this.exito.set(`¡Orden ${resp.codigoOrden} completada exitosamente! Lote generado: ${resp.codigoLoteGenerado}`);
        this.completando.set(false);
        this.cerrarModal();
        this.cargarOrdenes();
        setTimeout(() => this.exito.set(null), 5000);
      },
      error: (err) => {
        this.error.set('Error al completar orden: ' + (err.error?.message || err.message));
        this.completando.set(false);
      },
    });
  }

  cancelarOrden(orden: OrdenProduccion): void {
    if (!confirm(`¿Estás seguro de cancelar la orden ${orden.codigoOrden}?`)) return;

    this.produccionService.cancelarOrden(orden.id, 'Cancelada por el supervisor desde panel').subscribe({
      next: () => {
        this.cargarOrdenes();
      },
      error: (err) => {
        alert('Error al cancelar: ' + (err.error?.message || err.message));
      },
    });
  }
}
