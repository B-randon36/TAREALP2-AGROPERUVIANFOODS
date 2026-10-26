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

        <!-- Panel de Acceso SSO Keycloak (Proveedor de Identidad Externo) -->
        <div class="sso-primary-card">
          <div class="sso-badge">
            <span class="material-symbols-outlined sso-shield-icon">verified_user</span>
            <span>Autenticación OIDC + PKCE</span>
          </div>
          <p class="sso-desc">
            Acceso unificado mediante el Proveedor de Identidad corporativo (Keycloak). 
            Todas las credenciales y roles son validados criptográficamente de forma externa.
          </p>

          <button
            type="button"
            class="sso-btn-primary"
            (click)="iniciarKeycloak()"
            [disabled]="cargando()"
          >
            <span class="material-symbols-outlined">login</span>
            <span>Iniciar Sesión con Keycloak SSO</span>
          </button>
        </div>

        <!-- Cuentas registradas en el Realm de Keycloak -->
        <div class="section-divider">
          <span>Usuarios registrados en Keycloak (Realm: agroperuvianfoods)</span>
        </div>

        <div class="profiles-group">
          <div class="profile-item" (click)="iniciarKeycloak()">
            <div class="profile-meta">
              <span class="profile-name">Administrador General</span>
              <span class="profile-email">admin&#64;agroperuvian.pe &bull; Clave: Agro2026!</span>
            </div>
            <span class="tag tag-admin">ADMIN</span>
          </div>

          <div class="profile-item" (click)="iniciarKeycloak()">
            <div class="profile-meta">
              <span class="profile-name">Supervisora de Finanzas</span>
              <span class="profile-email">finanzas&#64;agroperuvian.pe &bull; Clave: Agro2026!</span>
            </div>
            <span class="tag tag-admin">FINANZAS</span>
          </div>

          <div class="profile-item" (click)="iniciarKeycloak()">
            <div class="profile-meta">
              <span class="profile-name">Cajero Operativo</span>
              <span class="profile-email">cajero&#64;agroperuvian.pe &bull; Clave: Agro2026!</span>
            </div>
            <span class="tag tag-cajero">CAJERO</span>
          </div>
        </div>

        <!-- Acceso de Contingencia / Modo Desarrollo Local -->
        <details class="dev-bypass-accordion">
          <summary class="dev-bypass-summary">Acceso directo de contingencia (Modo desarrollo)</summary>
          <form (ngSubmit)="iniciarSesion()" class="credentials-form" style="margin-top: 1rem;">
            <div class="field-group">
              <label for="email" class="field-label">Correo local</label>
              <input id="email" type="email" [(ngModel)]="email" name="email" class="form-control" placeholder="usuario@agroperu.com" />
            </div>
            <div class="field-group">
              <label for="password" class="field-label">Contraseña local</label>
              <input id="password" type="password" [(ngModel)]="password" name="password" class="form-control" placeholder="••••••••••••" />
            </div>
            <button type="submit" class="submit-btn" [disabled]="cargando()" style="margin-top: 0.5rem;">
              {{ cargando() ? 'Validando...' : 'Ingreso Local' }}
            </button>
          </form>
        </details>

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

    .sso-primary-card {
      background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 100%);
      border: 1px solid #a7f3d0;
      border-radius: 8px;
      padding: 18px 16px;
      margin-bottom: 20px;
      text-align: center;
    }

    .sso-badge {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      background-color: #d1fae5;
      color: #065f46;
      font-size: 0.6875rem;
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 0.05em;
      padding: 4px 10px;
      border-radius: 9999px;
      margin-bottom: 10px;
    }

    .sso-shield-icon {
      font-size: 15px;
    }

    .sso-desc {
      font-size: 0.8125rem;
      color: #334155;
      line-height: 1.45;
      margin: 0 0 16px;
    }

    .sso-btn-primary {
      width: 100%;
      height: 44px;
      background: linear-gradient(135deg, #059669 0%, #047857 100%);
      color: #ffffff;
      border: none;
      border-radius: 8px;
      font-size: 0.9375rem;
      font-weight: 600;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10px;
      box-shadow: 0 4px 12px rgba(5, 150, 105, 0.25);
      transition: all 0.2s ease;
    }

    .sso-btn-primary:hover:not(:disabled) {
      background: linear-gradient(135deg, #047857 0%, #065f46 100%);
      box-shadow: 0 6px 16px rgba(5, 150, 105, 0.35);
      transform: translateY(-1px);
    }

    .sso-btn-primary:disabled {
      opacity: 0.6;
      cursor: not-allowed;
      transform: none;
    }

    .dev-bypass-accordion {
      margin-top: 18px;
      border-top: 1px dashed #cbd5e1;
      padding-top: 12px;
    }

    .dev-bypass-summary {
      font-size: 0.75rem;
      color: #64748b;
      cursor: pointer;
      user-select: none;
      font-weight: 500;
    }

    .dev-bypass-summary:hover {
      color: #334155;
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
