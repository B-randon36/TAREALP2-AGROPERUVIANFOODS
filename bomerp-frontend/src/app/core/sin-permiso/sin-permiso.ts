import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-sin-permiso',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div style="padding: 40px; text-align: center; max-width: 500px; margin: 40px auto; background: #fff; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.08);">
      <div style="font-size: 32px; font-weight: 700; color: #d32f2f; margin-bottom: 16px; letter-spacing: 1px;">ACCESO RESTRINGIDO</div>
      <h2 style="color: #2c3e50; margin-bottom: 8px;">Acceso Denegado (403)</h2>
      <p style="color: #666; font-size: 15px; line-height: 1.5; margin-bottom: 24px;">
        Tu usuario no cuenta con el rol ni los privilegios necesarios para acceder a esta pantalla o realizar esta acción en AgroPeruvianFoods.
      </p>
      <a routerLink="/" style="display: inline-block; padding: 10px 20px; background: #2e7d32; color: #fff; text-decoration: none; border-radius: 4px; font-weight: 500;">
        Volver al inicio
      </a>
    </div>
  `,
})
export class SinPermiso {}
