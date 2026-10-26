import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from './auth-service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="login-wrapper">
      <div class="login-panel">
        
        <!-- Encabezado Corporativo -->
        <header class="panel-header">
          <div class="brand-badge">
            <svg class="brand-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/>
            </svg>
          </div>
          <h1 class="brand-title">AgroPeruvian Foods</h1>
          <p class="brand-subtitle">Plataforma Empresarial BomERP</p>
        </header>

        @if (errorMensaje()) {
          <div class="alert-box alert-error" role="alert">
            <svg class="alert-svg" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/>
            </svg>
            <span>{{ errorMensaje() }}</span>
          </div>
        }

        <!-- Formulario Principal -->
        <form (ngSubmit)="iniciarSesion()" class="credentials-form">
          <div class="field-group">
            <label for="email" class="field-label">Correo electrónico</label>
            <div class="input-container">
              <svg class="field-icon" viewBox="0 0 20 20" fill="currentColor">
                <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z"/>
                <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z"/>
              </svg>
              <input
                id="email"
                type="email"
                [(ngModel)]="email"
                name="email"
                required
                placeholder="usuario@agroperu.com"
                autocomplete="username"
                class="form-control"
              />
            </div>
          </div>

          <div class="field-group">
            <label for="password" class="field-label">Contraseña</label>
            <div class="input-container">
              <svg class="field-icon" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clip-rule="evenodd"/>
              </svg>
              <input
                id="password"
                type="password"
                [(ngModel)]="password"
                name="password"
                required
                placeholder="••••••••••••"
                autocomplete="current-password"
                class="form-control"
              />
            </div>
          </div>

          <button type="submit" class="submit-btn" [disabled]="cargando()">
            @if (cargando()) {
              <span class="loading-spinner"></span>
              <span>Validando credenciales...</span>
            } @else {
              <span>Ingresar al panel</span>
              <svg class="action-icon" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M10.293 3.293a1 1 0 011.414 0l6 6a1 1 0 010 1.414l-6 6a1 1 0 01-1.414-1.414L14.586 11H3a1 1 0 110-2h11.586l-4.293-4.293a1 1 0 010-1.414z" clip-rule="evenodd"/>
              </svg>
            }
          </button>
        </form>

        <!-- Selector de Perfil de Prueba -->
        <div class="section-divider">
          <span>Perfiles preconfigurados</span>
        </div>

        <div class="profiles-group">
          <button
            type="button"
            class="profile-item"
            (click)="iniciarConCredenciales('admin@agroperu.com', 'AgroPeru2026!')"
            [disabled]="cargando()"
          >
            <div class="profile-meta">
              <span class="profile-name">Administrador</span>
              <span class="profile-email">admin&#64;agroperu.com</span>
            </div>
            <span class="tag tag-admin">ADMIN</span>
          </button>

          <button
            type="button"
            class="profile-item"
            (click)="iniciarConCredenciales('cajero@agroperu.com', 'AgroPeru2026!')"
            [disabled]="cargando()"
          >
            <div class="profile-meta">
              <span class="profile-name">Cajero / Finanzas</span>
              <span class="profile-email">cajero&#64;agroperu.com</span>
            </div>
            <span class="tag tag-cajero">CAJERO</span>
          </button>

          <button
            type="button"
            class="profile-item"
            (click)="iniciarConCredenciales('vendedor@agroperu.com', 'AgroPeru2026!')"
            [disabled]="cargando()"
          >
            <div class="profile-meta">
              <span class="profile-name">Vendedor POS</span>
              <span class="profile-email">vendedor&#64;agroperu.com</span>
            </div>
            <span class="tag tag-vendedor">VENTAS</span>
          </button>
        </div>

        <!-- Opción SSO Keycloak -->
        <div class="sso-container">
          <button
            type="button"
            class="sso-btn"
            (click)="iniciarKeycloak()"
            [disabled]="cargando()"
          >
            <svg class="sso-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"/>
            </svg>
            <span>Autenticación SSO (Keycloak)</span>
          </button>
        </div>

        <footer class="panel-footer">
          <span>Agro Peruvian Foods S.A.C. &bull; Sistema ERP &bull; 2026</span>
        </footer>

      </div>
    </div>
  `,
  styles: [`
    .login-wrapper {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: #0f172a;
      background-image: 
        radial-gradient(at 0% 0%, rgba(16, 185, 129, 0.12) 0px, transparent 50%),
        radial-gradient(at 100% 100%, rgba(30, 64, 175, 0.15) 0px, transparent 50%);
      padding: 24px 16px;
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
    }

    .login-panel {
      background: #ffffff;
      border: 1px solid #e2e8f0;
      border-radius: 12px;
      box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
      width: 100%;
      max-width: 440px;
      padding: 32px 28px;
    }

    .panel-header {
      text-align: center;
      margin-bottom: 24px;
    }

    .brand-badge {
      width: 44px;
      height: 44px;
      background-color: #065f46;
      border-radius: 10px;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 12px;
    }

    .brand-icon {
      width: 24px;
      height: 24px;
      color: #ffffff;
    }

    .brand-title {
      font-size: 1.25rem;
      font-weight: 700;
      color: #0f172a;
      margin: 0;
      letter-spacing: -0.02em;
    }

    .brand-subtitle {
      font-size: 0.8125rem;
      color: #64748b;
      margin: 4px 0 0;
    }

    .alert-box {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 10px 14px;
      border-radius: 6px;
      font-size: 0.8125rem;
      margin-bottom: 18px;
    }

    .alert-error {
      background-color: #fef2f2;
      border: 1px solid #fee2e2;
      color: #991b1b;
    }

    .alert-svg {
      width: 16px;
      height: 16px;
      flex-shrink: 0;
    }

    .credentials-form {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }

    .field-group {
      display: flex;
      flex-direction: column;
      gap: 6px;
    }

    .field-label {
      font-size: 0.75rem;
      font-weight: 600;
      color: #475569;
      text-transform: uppercase;
      letter-spacing: 0.04em;
    }

    .input-container {
      position: relative;
      display: flex;
      align-items: center;
    }

    .field-icon {
      position: absolute;
      left: 12px;
      width: 16px;
      height: 16px;
      color: #94a3b8;
      pointer-events: none;
    }

    .form-control {
      width: 100%;
      height: 40px;
      padding: 0 12px 0 36px;
      border: 1px solid #cbd5e1;
      border-radius: 6px;
      font-size: 0.875rem;
      color: #0f172a;
      background-color: #ffffff;
      transition: border-color 0.15s ease, box-shadow 0.15s ease;
      box-sizing: border-box;
    }

    .form-control:focus {
      outline: none;
      border-color: #059669;
      box-shadow: 0 0 0 3px rgba(5, 150, 105, 0.12);
    }

    .submit-btn {
      height: 42px;
      background-color: #065f46;
      color: #ffffff;
      border: none;
      border-radius: 6px;
      font-size: 0.875rem;
      font-weight: 600;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      transition: background-color 0.15s ease;
      margin-top: 4px;
    }

    .submit-btn:hover:not(:disabled) {
      background-color: #047857;
    }

    .submit-btn:disabled {
      opacity: 0.65;
      cursor: not-allowed;
    }

    .action-icon {
      width: 16px;
      height: 16px;
    }

    .section-divider {
      display: flex;
      align-items: center;
      margin: 20px 0 14px;
      text-align: center;
    }

    .section-divider::before,
    .section-divider::after {
      content: '';
      flex: 1;
      border-bottom: 1px solid #e2e8f0;
    }

    .section-divider span {
      padding: 0 10px;
      font-size: 0.6875rem;
      font-weight: 600;
      color: #94a3b8;
      text-transform: uppercase;
      letter-spacing: 0.05em;
    }

    .profiles-group {
      display: flex;
      flex-direction: column;
      gap: 6px;
    }

    .profile-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 8px 12px;
      background-color: #f8fafc;
      border: 1px solid #e2e8f0;
      border-radius: 6px;
      cursor: pointer;
      text-align: left;
      transition: background-color 0.15s ease, border-color 0.15s ease;
    }

    .profile-item:hover:not(:disabled) {
      background-color: #f1f5f9;
      border-color: #cbd5e1;
    }

    .profile-meta {
      display: flex;
      flex-direction: column;
    }

    .profile-name {
      font-size: 0.8125rem;
      font-weight: 600;
      color: #1e293b;
    }

    .profile-email {
      font-size: 0.6875rem;
      color: #64748b;
    }

    .tag {
      font-size: 0.625rem;
      font-weight: 700;
      letter-spacing: 0.05em;
      padding: 2px 6px;
      border-radius: 4px;
    }

    .tag-admin {
      background-color: #fee2e2;
      color: #991b1b;
    }

    .tag-cajero {
      background-color: #fef3c7;
      color: #92400e;
    }

    .tag-vendedor {
      background-color: #dcfce7;
      color: #166534;
    }

    .sso-container {
      margin-top: 14px;
    }

    .sso-btn {
      width: 100%;
      height: 38px;
      background-color: #ffffff;
      border: 1px solid #cbd5e1;
      border-radius: 6px;
      color: #334155;
      font-size: 0.8125rem;
      font-weight: 500;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      transition: background-color 0.15s ease, border-color 0.15s ease;
    }

    .sso-btn:hover:not(:disabled) {
      background-color: #f8fafc;
      border-color: #94a3b8;
    }

    .sso-icon {
      width: 15px;
      height: 15px;
      color: #475569;
    }

    .panel-footer {
      margin-top: 22px;
      text-align: center;
      font-size: 0.6875rem;
      color: #94a3b8;
    }

    .loading-spinner {
      width: 14px;
      height: 14px;
      border: 2px solid rgba(255, 255, 255, 0.3);
      border-top-color: #ffffff;
      border-radius: 50%;
      animation: spin 0.6s linear infinite;
    }

    @keyframes spin {
      to { transform: rotate(360deg); }
    }
  `],
})
export class LoginComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  email = 'admin@agroperu.com';
  password = 'AgroPeru2026!';
  cargando = signal(false);
  errorMensaje = signal<string | null>(null);

  async iniciarSesion(): Promise<void> {
    if (!this.email || !this.password) {
      this.errorMensaje.set('Debe ingresar correo y contraseña.');
      return;
    }
    this.cargando.set(true);
    this.errorMensaje.set(null);

    const ok = await this.auth.iniciarSesionDirecta(this.email, this.password);
    this.cargando.set(false);

    if (ok) {
      const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
      this.router.navigateByUrl(returnUrl);
    } else {
      this.errorMensaje.set('Credenciales inválidas o servidor inalcanzable.');
    }
  }

  iniciarConCredenciales(email: string, pass: string): void {
    this.email = email;
    this.password = pass;
    this.iniciarSesion();
  }

  iniciarKeycloak(): void {
    const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
    this.auth.iniciarLogin(returnUrl);
  }
}
