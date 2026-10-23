import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { VentasService } from './ventas-service';
import { ProductoService } from '../catalogo/producto/producto-service';
import { Producto } from '../catalogo/producto/producto.model';
import { SesionCajaService } from '../finanzas/sesion-caja/sesion-caja-service';
import { SesionCaja } from '../finanzas/sesion-caja/sesion-caja.model';
import {
  CanalVenta,
  MetodoPagoVenta,
  VentaCreateRequest,
  VentaResponse,
} from './ventas.model';

export interface CartItem {
  productoId: number;
  nombre: string;
  precio: number;
  cantidad: number;
  imagenUrl?: string | null;
}

@Component({
  selector: 'app-pos-checkout',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, DatePipe, DecimalPipe],
  templateUrl: './pos-checkout.html',
  styleUrls: ['./pos-checkout.css'],
})
export class PosCheckout implements OnInit {
  private readonly ventasService = inject(VentasService);
  private readonly productoService = inject(ProductoService);
  private readonly sesionService = inject(SesionCajaService);
  private readonly router = inject(Router);

  // Estados
  protected readonly productos = signal<Producto[]>([]);
  protected readonly turnoActivo = signal<SesionCaja | null>(null);
  protected readonly loading = signal(false);
  protected readonly error = signal<string | null>(null);

  // Carrito de compras
  protected readonly carrito = signal<CartItem[]>([]);

  // Datos de Facturación / Venta
  protected readonly canal = signal<CanalVenta>('TIENDA_MOSTRADOR');
  protected readonly clienteTipo = signal<string>('CONSUMIDOR_FINAL');
  protected readonly clienteNombre = signal<string>('Público General');
  protected readonly clienteNumeroDoc = signal<string>('');
  protected readonly metodoPago = signal<MetodoPagoVenta>('EFECTIVO');
  protected readonly montoRecibido = signal<number>(0);

  // Venta confirmada para vista e impresión de ticket
  protected readonly ventaConfirmada = signal<VentaResponse | null>(null);
  protected readonly loadingCobro = signal(false);

  // Computados
  protected readonly totalVenta = computed(() => {
    const sum = this.carrito().reduce((acc, it) => acc + it.precio * it.cantidad, 0);
    return Number(sum.toFixed(2));
  });

  protected readonly subtotalVenta = computed(() => {
    return Number((this.totalVenta() / 1.18).toFixed(2));
  });

  protected readonly igvVenta = computed(() => {
    return Number((this.totalVenta() - this.subtotalVenta()).toFixed(2));
  });

  protected readonly vuelto = computed(() => {
    if (this.metodoPago() !== 'EFECTIVO') return 0;
    const v = this.montoRecibido() - this.totalVenta();
    return v > 0 ? Number(v.toFixed(2)) : 0;
  });

  protected readonly puedeCobrar = computed(() => {
    if (this.carrito().length === 0) return false;
    if (this.metodoPago() === 'EFECTIVO') {
      if (!this.turnoActivo()) return false;
      return this.montoRecibido() >= this.totalVenta();
    }
    return true;
  });

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.loading.set(true);
    // 1. Cargar productos
    this.productoService.listar().subscribe({
      next: (prods) => {
        this.productos.set(prods);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar los productos del catálogo.');
        this.loading.set(false);
      },
    });

    // 2. Verificar turno de caja activo
    this.sesionService.obtenerActiva().subscribe({
      next: (activa) => {
        this.turnoActivo.set(activa);
      },
      error: () => {
        this.turnoActivo.set(null);
      },
    });
  }

  agregarAlCarrito(prod: Producto): void {
    this.error.set(null);
    const actual = this.carrito();
    const idx = actual.findIndex((i) => i.productoId === prod.id);

    if (idx >= 0) {
      const nuevo = [...actual];
      nuevo[idx] = { ...nuevo[idx], cantidad: nuevo[idx].cantidad + 1 };
      this.carrito.set(nuevo);
    } else {
      this.carrito.set([
        ...actual,
        {
          productoId: prod.id,
          nombre: prod.nombre,
          precio: Number(prod.precio),
          cantidad: 1,
          imagenUrl: prod.imagenUrl,
        },
      ]);
    }

    if (this.metodoPago() === 'EFECTIVO' && this.montoRecibido() < this.totalVenta()) {
      this.montoRecibido.set(this.totalVenta());
    }
  }

  modificarCantidad(productoId: number, delta: number): void {
    const actual = this.carrito();
    const idx = actual.findIndex((i) => i.productoId === productoId);
    if (idx < 0) return;

    const nuevaCant = actual[idx].cantidad + delta;
    if (nuevaCant <= 0) {
      this.eliminarDelCarrito(productoId);
    } else {
      const nuevo = [...actual];
      nuevo[idx] = { ...nuevo[idx], cantidad: nuevaCant };
      this.carrito.set(nuevo);
    }
  }

  eliminarDelCarrito(productoId: number): void {
    this.carrito.set(this.carrito().filter((i) => i.productoId !== productoId));
  }

  limpiarCarrito(): void {
    this.carrito.set([]);
    this.montoRecibido.set(0);
  }

  fijarMontoRecibido(monto: number): void {
    this.montoRecibido.set(monto);
  }

  procesarCobro(): void {
    if (!this.puedeCobrar()) return;

    this.loadingCobro.set(true);
    this.error.set(null);

    const req: VentaCreateRequest = {
      canal: this.canal(),
      clienteTipo: this.clienteTipo(),
      clienteNombre: this.clienteNombre(),
      clienteNumeroDoc: this.clienteNumeroDoc() || undefined,
      metodoPago: this.metodoPago(),
      montoPagado: this.metodoPago() === 'EFECTIVO' ? Number(this.montoRecibido()) : Number(this.totalVenta()),
      cajeroNombre: this.turnoActivo()?.cajero || 'Cajero POS',
      items: this.carrito().map((it) => ({
        productoId: it.productoId,
        cantidad: it.cantidad,
        precioUnitario: it.precio,
      })),
    };

    this.ventasService.registrarVenta(req).subscribe({
      next: (venta) => {
        this.ventaConfirmada.set(venta);
        this.loadingCobro.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'Error al procesar la venta.');
        this.loadingCobro.set(false);
      },
    });
  }

  nuevaVenta(): void {
    this.ventaConfirmada.set(null);
    this.limpiarCarrito();
    this.cargarDatos();
  }

  imprimirTicket(): void {
    window.print();
  }
}
