import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProduccionService } from './produccion-service';
import { RecetaBOM } from './produccion.model';

@Component({
  selector: 'app-receta-bom-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './receta-bom-list.html',
  styleUrls: ['./receta-bom-list.css'],
})
export class RecetaBomList implements OnInit {
  private readonly produccionService = inject(ProduccionService);

  readonly recetas = signal<RecetaBOM[]>([]);
  readonly cargando = signal<boolean>(true);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.cargarRecetas();
  }

  cargarRecetas(): void {
    this.cargando.set(true);
    this.produccionService.listarRecetas().subscribe({
      next: (data) => {
        this.recetas.set(data);
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set('Error al cargar recetas: ' + (err.error?.message || err.message));
        this.cargando.set(false);
      },
    });
  }
}
