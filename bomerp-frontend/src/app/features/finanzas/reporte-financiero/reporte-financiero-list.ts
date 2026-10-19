import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ReporteFinancieroService } from './reporte-financiero-service';
import { DashboardFinanciero } from './reporte-financiero.model';

@Component({
  selector: 'app-reporte-financiero-list',
  standalone: true,
  imports: [CommonModule, RouterLink, CurrencyPipe, DecimalPipe],
  templateUrl: './reporte-financiero-list.html',
  styleUrls: ['./reporte-financiero-list.css'],
})
export class ReporteFinancieroList implements OnInit {
  private readonly reporteService = inject(ReporteFinancieroService);

  protected readonly dashboard = signal<DashboardFinanciero | null>(null);
  protected readonly loading = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly exportando = signal(false);

  ngOnInit(): void {
    this.cargarDashboard();
  }

  cargarDashboard(): void {
    this.loading.set(true);
    this.error.set(null);

    this.reporteService.obtenerDashboard().subscribe({
      next: (data) => {
        this.dashboard.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar el tablero analítico financiero.');
        this.loading.set(false);
      },
    });
  }

  calcularPorcentaje(monto: number): number {
    const dash = this.dashboard();
    if (!dash || dash.totalIngresos === 0) return 0;
    return Math.min(100, Math.round((monto / dash.totalIngresos) * 100));
  }

  descargarReporteActiva(formato: 'PDF' | 'EXCEL'): void {
    const dash = this.dashboard();
    if (!dash || !dash.sesionActivaId) {
      alert('No hay un turno de caja activo para exportar.');
      return;
    }

    this.exportando.set(true);
    this.reporteService.descargarCierreCaja(dash.sesionActivaId, formato).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        const ext = formato === 'PDF' ? 'pdf' : 'xlsx';
        a.href = url;
        a.download = `corte-caja-turno-activo.${ext}`;
        a.click();
        window.URL.revokeObjectURL(url);
        this.exportando.set(false);
      },
      error: () => {
        alert('Error al descargar el archivo de reporte.');
        this.exportando.set(false);
      },
    });
  }
}
