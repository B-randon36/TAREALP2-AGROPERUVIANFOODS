import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ComprasService } from './compras-service';
import { TercerosService } from '../terceros/terceros-service';
import { ProduccionService } from '../produccion/produccion-service';
import { Tercero } from '../terceros/terceros.model';
import { Insumo } from '../produccion/produccion.model';

@Component({
  selector: 'app-compra-form',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './compra-form.html',
  styleUrls: ['./compra-form.css'],
})
export class CompraForm implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly comprasService = inject(ComprasService);
  private readonly tercerosService = inject(TercerosService);
  private readonly produccionService = inject(ProduccionService);

  readonly proveedores = signal<Tercero[]>([]);
  readonly insumos = signal<Insumo[]>([]);
  readonly guardando = signal<boolean>(false);
  readonly error = signal<string | null>(null);

  form: FormGroup = this.fb.group({
    proveedorId: ['', [Validators.required]],
    numeroComprobante: ['', [Validators.required]],
    fechaCompra: [new Date().toISOString().substring(0, 10), [Validators.required]],
    metodoPago: ['TRANSFERENCIA', [Validators.required]],
    observaciones: [''],
    items: this.fb.array([]),
  });

  get itemsArray(): FormArray {
    return this.form.get('items') as FormArray;
  }

  ngOnInit(): void {
    this.tercerosService.listar('PROVEEDOR').subscribe({
      next: (data) => this.proveedores.set(data),
      error: (err) => console.error(err),
    });

    this.produccionService.listarInsumos().subscribe({
      next: (data) => {
        this.insumos.set(data);
        if (data.length > 0 && this.itemsArray.length === 0) {
          this.agregarItem();
        }
      },
      error: (err) => console.error(err),
    });
  }

  agregarItem(): void {
    const primerInsumo = this.insumos().length > 0 ? this.insumos()[0] : null;
    const itemGroup = this.fb.group({
      insumoId: [primerInsumo ? primerInsumo.id : (null as number | null), [Validators.required]],
      cantidad: [100, [Validators.required, Validators.min(0.01)]],
      precioUnitario: [primerInsumo ? primerInsumo.costoUnitario : 1.50, [Validators.required, Validators.min(0.0001)]],
    });

    this.itemsArray.push(itemGroup);
  }

  onInsumoChange(index: number): void {
    const itemGroup = this.itemsArray.at(index);
    const insumoId = +itemGroup.get('insumoId')?.value;
    const insumo = this.insumos().find((i) => i.id === insumoId);
    if (insumo) {
      itemGroup.patchValue({ precioUnitario: insumo.costoUnitario });
    }
  }

  eliminarItem(index: number): void {
    if (this.itemsArray.length > 1) {
      this.itemsArray.removeAt(index);
    }
  }

  get subtotal(): number {
    return this.itemsArray.controls.reduce((acc, ctrl) => {
      const cant = ctrl.get('cantidad')?.value || 0;
      const precio = ctrl.get('precioUnitario')?.value || 0;
      return acc + cant * precio;
    }, 0);
  }

  get igv(): number {
    return this.subtotal * 0.18;
  }

  get total(): number {
    return this.subtotal + this.igv;
  }

  guardar(): void {
    if (this.form.invalid || this.itemsArray.length === 0) {
      this.form.markAllAsTouched();
      return;
    }

    this.guardando.set(true);
    this.error.set(null);

    const val = this.form.value;
    this.comprasService.registrar({
      proveedorId: +val.proveedorId,
      numeroComprobante: val.numeroComprobante,
      fechaCompra: val.fechaCompra,
      metodoPago: val.metodoPago,
      observaciones: val.observaciones,
      items: val.items.map((it: any) => ({
        insumoId: +it.insumoId,
        cantidad: +it.cantidad,
        precioUnitario: +it.precioUnitario,
      })),
    }).subscribe({
      next: () => {
        this.guardando.set(false);
        this.router.navigate(['/compras']);
      },
      error: (err) => {
        this.error.set(err.error?.message || err.message || 'Error al registrar orden de compra');
        this.guardando.set(false);
      },
    });
  }
}
