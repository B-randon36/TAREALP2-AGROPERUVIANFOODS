import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';
import { AuthService } from '../auth/auth-service';

export const authGuard: CanActivateFn = (_route, state) => {
  const auth = inject(AuthService);

  if (auth.autenticado()) {
    return true;
  }
  // Rúbrica U2 / S12: Acceso no autenticado redirige directamente al IdP Keycloak con PKCE
  auth.iniciarLogin(state.url);
  return false;
};
