import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { VentasService } from './ventas-service';
import { VentaResponse } from './ventas.model';

@Component({
  selector: 'app-venta-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, DatePipe, DecimalPipe],
  templateUrl: './venta-list.html',
  styleUrls: ['./venta-list.css'],
})
export class VentaList implements OnInit {
  private readonly ventasService = inject(VentasService);

  protected readonly ventas = signal<VentaResponse[]>([]);
  protected readonly loading = signal(false);
  protected readonly error = signal<string | null>(null);

  protected readonly filtroTexto = signal('');
  protected readonly filtroMetodo = signal('');

  protected readonly ventaSeleccionada = signal<VentaResponse | null>(null);

  protected readonly ventasFiltradas = computed(() => {
    let list = this.ventas();
    const txt = this.filtroTexto().toLowerCase().trim();
    const met = this.filtroMetodo();

    if (txt) {
      list = list.filter(
        (v) =>
          v.codigoVenta.toLowerCase().includes(txt) ||
          v.clienteNombre.toLowerCase().includes(txt) ||
          (v.clienteNumeroDoc && v.clienteNumeroDoc.includes(txt)) ||
          (v.cajeroNombre && v.cajeroNombre.toLowerCase().includes(txt))
      );
    }
    if (met) {
      list = list.filter((v) => v.metodoPago === met);
    }
    return list;
  });

  protected readonly totalVendido = computed(() =>
    this.ventas().reduce((acc, v) => acc + v.total, 0)
  );

  ngOnInit(): void {
    this.cargarVentas();
  }

  cargarVentas(): void {
    this.loading.set(true);
    this.error.set(null);
    this.ventasService.listarVentas().subscribe({
      next: (data) => {
        this.ventas.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar las ventas registradas.');
        this.loading.set(false);
      },
    });
  }

  verTicket(v: VentaResponse): void {
    this.ventaSeleccionada.set(v);
  }

  cerrarModalTicket(): void {
    this.ventaSeleccionada.set(null);
  }

  imprimir(): void {
    window.print();
  }
}
