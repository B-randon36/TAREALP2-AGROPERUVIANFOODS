import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from './auth-service';

@Component({
  selector: 'app-auth-callback',
  standalone: true,
  template: `
    <div style="display:flex; justify-content:center; align-items:center; height:100vh; font-family:sans-serif; flex-direction:column; gap:16px;">
      <div style="border:4px solid #f3f3f3; border-top:4px solid #2e7d32; border-radius:50%; width:40px; height:40px; animation:spin 1s linear infinite;"></div>
      <p style="color:#333; font-weight:500;">Autenticando con Keycloak en AgroPeruvianFoods...</p>
      <style>
        @keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
      </style>
    </div>
  `,
})
export class AuthCallback {
  constructor() {
    const route = inject(ActivatedRoute);
    const router = inject(Router);
    const auth = inject(AuthService);

    const codigo = route.snapshot.queryParamMap.get('code');
    const state = route.snapshot.queryParamMap.get('state');
    const error = route.snapshot.queryParamMap.get('error');

    if (error || !codigo || !state) {
      router.navigateByUrl('/');
      return;
    }

    auth
      .completarLogin(codigo, state)
      .then(() => router.navigateByUrl(auth.destinoPendiente()))
      .catch(() => router.navigateByUrl('/'));
  }
}
