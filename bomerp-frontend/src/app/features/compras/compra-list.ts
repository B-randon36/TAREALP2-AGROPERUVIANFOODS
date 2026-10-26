import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ComprasService } from './compras-service';
import { Compra } from './compras.model';

@Component({
  selector: 'app-compra-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './compra-list.html',
  styleUrls: ['./compra-list.css'],
})
export class CompraList implements OnInit {
  private readonly comprasService = inject(ComprasService);

  readonly compras = signal<Compra[]>([]);
  readonly cargando = signal<boolean>(true);
  readonly error = signal<string | null>(null);

  // Modal detalle
  readonly compraSeleccionada = signal<Compra | null>(null);

  ngOnInit(): void {
    this.cargarCompras();
  }

  cargarCompras(): void {
    this.cargando.set(true);
    this.comprasService.listar().subscribe({
      next: (data) => {
        this.compras.set(data);
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set('Error al cargar órdenes de compra: ' + (err.error?.message || err.message));
        this.cargando.set(false);
      },
    });
  }

  verDetalle(compra: Compra): void {
    this.compraSeleccionada.set(compra);
  }

  cerrarModal(): void {
    this.compraSeleccionada.set(null);
  }
}
