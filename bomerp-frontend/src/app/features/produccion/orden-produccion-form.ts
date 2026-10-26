import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProduccionService } from './produccion-service';
import { RecetaBOM } from './produccion.model';

@Component({
  selector: 'app-orden-produccion-form',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './orden-produccion-form.html',
  styleUrls: ['./orden-produccion-form.css'],
})
export class OrdenProduccionForm implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly produccionService = inject(ProduccionService);

  readonly recetas = signal<RecetaBOM[]>([]);
  readonly recetaSeleccionada = signal<RecetaBOM | null>(null);
  readonly guardando = signal<boolean>(false);
  readonly error = signal<string | null>(null);

  form: FormGroup = this.fb.group({
    codigoOrden: ['', [Validators.required, Validators.maxLength(40)]],
    recetaBomId: ['', [Validators.required]],
    cantidadAProducir: [100, [Validators.required, Validators.min(0.01)]],
    observaciones: [''],
  });

  ngOnInit(): void {
    // Autogenerar código de orden sugerido
    const randomSuffix = Math.floor(100 + Math.random() * 900);
    this.form.patchValue({
      codigoOrden: `OP-2026-${randomSuffix}`,
    });

    this.produccionService.listarRecetas().subscribe({
      next: (data) => {
        const vigentes = data.filter((r) => r.vigente);
        this.recetas.set(vigentes);
        if (vigentes.length > 0) {
          this.form.patchValue({ recetaBomId: vigentes[0].id });
          this.onRecetaChange(vigentes[0].id);
        }
      },
      error: (err) => this.error.set('Error al cargar recetas: ' + err.message),
    });

    this.form.get('recetaBomId')?.valueChanges.subscribe((id) => {
      if (id) {
        this.onRecetaChange(+id);
      }
    });
  }

  onRecetaChange(recetaId: number): void {
    const receta = this.recetas().find((r) => r.id === recetaId);
    this.recetaSeleccionada.set(receta || null);
  }

  get cantidad(): number {
    return this.form.get('cantidadAProducir')?.value || 0;
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.guardando.set(true);
    this.error.set(null);

    const val = this.form.value;
    this.produccionService.crearOrden({
      codigoOrden: val.codigoOrden,
      recetaBomId: +val.recetaBomId,
      cantidadAProducir: +val.cantidadAProducir,
      observaciones: val.observaciones,
    }).subscribe({
      next: () => {
        this.guardando.set(false);
        this.router.navigate(['/produccion/ordenes']);
      },
      error: (err) => {
        this.error.set(err.error?.message || err.message || 'Error al crear la orden de producción');
        this.guardando.set(false);
      },
    });
  }
}
