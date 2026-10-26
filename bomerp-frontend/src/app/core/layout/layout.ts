import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../auth/auth-service';
import { SunatApiService, TipoCambioResponse } from '../services/sunat-api.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './layout.html',
  styleUrl: './layout.css',
})
export class Layout implements OnInit {
  protected readonly auth = inject(AuthService);
  private readonly sunatService = inject(SunatApiService);

  readonly tipoCambio = signal<TipoCambioResponse | null>(null);

  ngOnInit(): void {
    this.cargarTipoCambio();
  }

  private cargarTipoCambio(): void {
    this.sunatService.obtenerTipoCambio().subscribe({
      next: (tc) => this.tipoCambio.set(tc),
      error: () => {},
    });
  }
}
