import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { SesionCajaService } from './sesion-caja-service';
import { CategoriaFinancieraService } from '../categoria-financiera/categoria-financiera-service';
import { CategoriaFinanciera } from '../categoria-financiera/categoria-financiera.model';
import {
  ArqueoDenominacion,
  MetodoPago,
  MovimientoCaja,
  SesionCaja,
  SesionCajaDetalle,
  TipoFinanciero,
} from './sesion-caja.model';

@Component({
  selector: 'app-sesion-caja-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, CurrencyPipe, DatePipe],
  templateUrl: './sesion-caja-form.html',
  styleUrls: ['./sesion-caja-form.css'],
})
export class SesionCajaForm implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly sesionService = inject(SesionCajaService);
  private readonly categoriaService = inject(CategoriaFinancieraService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  // Estados
  protected readonly modoApertura = signal(false);
  protected readonly sesionId = signal<number | null>(null);
  protected readonly sesion = signal<SesionCajaDetalle | null>(null);
  protected readonly categorias = signal<CategoriaFinanciera[]>([]);
  protected readonly loading = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly exitoMensaje = signal<string | null>(null);

  // Denominaciones de Arqueo Físico (Billetes y Monedas del Perú)
  protected readonly billetes = signal<{ valor: number; cantidad: number }[]>([
    { valor: 200, cantidad: 0 },
    { valor: 100, cantidad: 0 },
    { valor: 50, cantidad: 0 },
    { valor: 20, cantidad: 0 },
    { valor: 10, cantidad: 0 },
  ]);

  protected readonly monedas = signal<{ valor: number; cantidad: number }[]>([
    { valor: 5, cantidad: 0 },
    { valor: 2, cantidad: 0 },
    { valor: 1, cantidad: 0 },
    { valor: 0.5, cantidad: 0 },
    { valor: 0.2, cantidad: 0 },
    { valor: 0.1, cantidad: 0 },
  ]);

  // Formulario Apertura
  protected readonly formApertura = this.fb.nonNullable.group({
    usuarioId: [1, [Validators.required]],
    cajero: ['Brandon Ccalla', [Validators.required, Validators.maxLength(120)]],
    saldoInicial: [300, [Validators.required, Validators.min(0)]],
    codigoSesion: [''],
  });

  // Formulario Registro Rápido de Movimientos
  protected readonly formMovimiento = this.fb.nonNullable.group({
    tipo: ['INGRESO' as TipoFinanciero, [Validators.required]],
    metodoPago: ['EFECTIVO' as MetodoPago, [Validators.required]],
    categoriaFinancieraId: [null as number | null, [Validators.required]],
    monto: [null as number | null, [Validators.required, Validators.min(0.01)]],
    descripcion: ['', [Validators.required, Validators.maxLength(255)]],
    referenciaExterna: ['', [Validators.maxLength(100)]],
  });

  // Formulario Cierre
  protected readonly formCierre = this.fb.nonNullable.group({
    observaciones: ['', [Validators.maxLength(500)]],
  });

  // Cálculos reactivos de Arqueo Físico
  protected readonly totalBilletes = computed(() =>
    this.billetes().reduce((acc, b) => acc + b.valor * b.cantidad, 0)
  );

  protected readonly totalMonedas = computed(() =>
    this.monedas().reduce((acc, m) => acc + m.valor * m.cantidad, 0)
  );

  protected readonly saldoRealArqueado = computed(() =>
    Number((this.totalBilletes() + this.totalMonedas()).toFixed(2))
  );

  protected readonly diferenciaArqueo = computed(() => {
    const esperado = this.sesion()?.saldoEsperadoEfectivo ?? 0;
    return Number((this.saldoRealArqueado() - esperado).toFixed(2));
  });

  // =========================================================================
  // REGLA RN-FIN-01 EN FRONTEND: Alerta visual reactiva de falta de liquidez
  // =========================================================================
  protected readonly alertaRnFin01 = computed(() => {
    const s = this.sesion();
    if (!s || s.estado !== 'ABIERTA') return null;

    const tipo = this.formMovimiento.controls.tipo.value;
    const metodo = this.formMovimiento.controls.metodoPago.value;
    const monto = this.formMovimiento.controls.monto.value ?? 0;

    if (tipo === 'EGRESO' && metodo === 'EFECTIVO' && monto > s.saldoEsperadoEfectivo) {
      return `RN-FIN-01: El monto a retirar (S/ ${monto.toFixed(2)}) supera el saldo disponible en gaveta (S/ ${s.saldoEsperadoEfectivo.toFixed(2)}). La operación será bloqueada.`;
    }
    return null;
  });

  ngOnInit(): void {
    this.cargarCategorias();
    const idParam = this.route.snapshot.paramMap.get('id');

    if (idParam && idParam !== 'nueva') {
      const id = Number(idParam);
      this.sesionId.set(id);
      this.cargarDetalle(id);
    } else {
      // Verificar si hay una activa; si no, mostrar apertura
      this.sesionService.obtenerActiva().subscribe({
        next: (activa) => {
          this.sesionId.set(activa.id);
          this.cargarDetalle(activa.id);
        },
        error: () => {
          this.modoApertura.set(true);
        },
      });
    }
  }

  cargarCategorias(): void {
    this.categoriaService.listar().subscribe({
      next: (cats) => {
        this.categorias.set(cats);
        if (cats.length > 0) {
          this.formMovimiento.patchValue({ categoriaFinancieraId: cats[0].id });
        }
      },
    });
  }

  cargarDetalle(id: number): void {
    this.loading.set(true);
    this.sesionService.obtenerDetalle(id).subscribe({
      next: (data) => {
        this.sesion.set(data);
        this.modoApertura.set(false);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar la información del turno de caja.');
        this.loading.set(false);
      },
    });
  }

  abrirTurno(): void {
    if (this.formApertura.invalid) {
      this.formApertura.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.error.set(null);
    const val = this.formApertura.getRawValue();

    this.sesionService.abrir(val).subscribe({
      next: (s) => {
        this.exitoMensaje.set(`Turno de caja ${s.codigoSesion} abierto con éxito.`);
        this.sesionId.set(s.id);
        this.cargarDetalle(s.id);
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'No se pudo abrir el turno de caja.');
        this.loading.set(false);
      },
    });
  }

  registrarMovimiento(): void {
    if (this.alertaRnFin01()) return;

    if (this.formMovimiento.invalid) {
      this.formMovimiento.markAllAsTouched();
      return;
    }

    const id = this.sesionId();
    if (!id) return;

    const val = this.formMovimiento.getRawValue();
    if (!val.categoriaFinancieraId || !val.monto) return;

    this.loading.set(true);
    this.error.set(null);

    this.sesionService
      .registrarMovimiento(id, {
        tipo: val.tipo,
        metodoPago: val.metodoPago,
        categoriaFinancieraId: val.categoriaFinancieraId,
        monto: val.monto,
        descripcion: val.descripcion,
        referenciaExterna: val.referenciaExterna,
      })
      .subscribe({
        next: () => {
          this.exitoMensaje.set('Movimiento registrado correctamente.');
          this.formMovimiento.reset({
            tipo: 'INGRESO',
            metodoPago: 'EFECTIVO',
            categoriaFinancieraId: this.categorias()[0]?.id ?? null,
            monto: null,
            descripcion: '',
            referenciaExterna: '',
          });
          this.cargarDetalle(id);
        },
        error: (err) => {
          this.error.set(err?.error?.message || 'Error al registrar el movimiento.');
          this.loading.set(false);
        },
      });
  }

  actualizarCantidadBillete(index: number, event: Event): void {
    const input = event.target as HTMLInputElement;
    const valor = Math.max(0, parseInt(input.value || '0', 10));
    const lista = [...this.billetes()];
    lista[index] = { ...lista[index], cantidad: valor };
    this.billetes.set(lista);
  }

  actualizarCantidadMoneda(index: number, event: Event): void {
    const input = event.target as HTMLInputElement;
    const valor = Math.max(0, parseInt(input.value || '0', 10));
    const lista = [...this.monedas()];
    lista[index] = { ...lista[index], cantidad: valor };
    this.monedas.set(lista);
  }

  cerrarTurnoCaja(): void {
    const id = this.sesionId();
    if (!id) return;

    const saldoReal = this.saldoRealArqueado();
    const obs = this.formCierre.controls.observaciones.value;

    if (!confirm(`¿Confirmas el cierre del turno con un arqueo físico de S/ ${saldoReal.toFixed(2)}?`)) {
      return;
    }

    this.loading.set(true);
    this.error.set(null);

    this.sesionService.cerrar(id, { saldoRealEfectivo: saldoReal, observaciones: obs }).subscribe({
      next: (cerrada) => {
        this.exitoMensaje.set(`Turno ${cerrada.codigoSesion} cerrado exitosamente.`);
        this.cargarDetalle(id);
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'No se pudo cerrar el turno de caja.');
        this.loading.set(false);
      },
    });
  }

  imprimirTicket(): void {
    window.print();
  }

  volver(): void {
    this.router.navigate(['/finanzas/sesiones']);
  }
}
