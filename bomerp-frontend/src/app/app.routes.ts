import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth-guard';
import { rolGuard } from './core/guards/rol-guard';

export const routes: Routes = [
  {
    path: 'auth/callback',
    loadComponent: () => import('./core/auth/auth-callback').then((m) => m.AuthCallback),
  },
  {
    path: '',
    loadComponent: () => import('./core/layout/layout').then((m) => m.Layout),
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./core/inicio/inicio').then((m) => m.Inicio),
      },

      // --- Módulo Finanzas y Reportes ---
      {
        path: 'finanzas/transacciones',
        loadComponent: () =>
          import('./features/finanzas/transaccion-financiera/transaccion-financiera-list').then(
            (m) => m.TransaccionFinancieraList
          ),
      },
      {
        path: 'finanzas/transacciones/nueva',
        loadComponent: () =>
          import('./features/finanzas/transaccion-financiera/transaccion-financiera-form').then(
            (m) => m.TransaccionFinancieraForm
          ),
        canActivate: [rolGuard('ADMIN', 'ADMIN_FINANZAS')],
      },
      {
        path: 'finanzas/transacciones/:id/editar',
        loadComponent: () =>
          import('./features/finanzas/transaccion-financiera/transaccion-financiera-form').then(
            (m) => m.TransaccionFinancieraForm
          ),
        canActivate: [rolGuard('ADMIN', 'ADMIN_FINANZAS')],
      },
      {
        path: 'finanzas/categorias',
        loadComponent: () =>
          import('./features/finanzas/categoria-financiera/categoria-financiera-list').then(
            (m) => m.CategoriaFinancieraList
          ),
      },
      {
        path: 'finanzas/categorias/nueva',
        loadComponent: () =>
          import('./features/finanzas/categoria-financiera/categoria-financiera-form').then(
            (m) => m.CategoriaFinancieraForm
          ),
        canActivate: [rolGuard('ADMIN', 'ADMIN_FINANZAS')],
      },
      {
        path: 'finanzas/categorias/:id/editar',
        loadComponent: () =>
          import('./features/finanzas/categoria-financiera/categoria-financiera-form').then(
            (m) => m.CategoriaFinancieraForm
          ),
        canActivate: [rolGuard('ADMIN', 'ADMIN_FINANZAS')],
      },

      // --- Operación Transaccional: Rendiciones de Caja Chica ---
      {
        path: 'finanzas/rendiciones',
        loadComponent: () =>
          import('./features/finanzas/rendicion-caja-chica/rendicion-caja-chica-list').then(
            (m) => m.RendicionCajaChicaList
          ),
      },
      {
        path: 'finanzas/rendiciones/nueva',
        loadComponent: () =>
          import('./features/finanzas/rendicion-caja-chica/rendicion-caja-chica-form').then(
            (m) => m.RendicionCajaChicaForm
          ),
        canActivate: [rolGuard('ADMIN', 'ADMIN_FINANZAS')],
      },

      // --- Submódulo Transaccional: Sesiones y Turnos de Caja Diaria ---
      {
        path: 'finanzas/sesiones',
        loadComponent: () =>
          import('./features/finanzas/sesion-caja/sesion-caja-list').then((m) => m.SesionCajaList),
      },
      {
        path: 'finanzas/sesiones/nueva',
        loadComponent: () =>
          import('./features/finanzas/sesion-caja/sesion-caja-form').then((m) => m.SesionCajaForm),
        canActivate: [rolGuard('ADMIN', 'ADMIN_FINANZAS')],
      },
      {
        path: 'finanzas/sesiones/:id',
        loadComponent: () =>
          import('./features/finanzas/sesion-caja/sesion-caja-form').then((m) => m.SesionCajaForm),
        canActivate: [rolGuard('ADMIN', 'ADMIN_FINANZAS')],
      },

      // --- Submódulo Analítico: Dashboard y Reportes Financieros ---
      {
        path: 'finanzas/reportes',
        loadComponent: () =>
          import('./features/finanzas/reporte-financiero/reporte-financiero-list').then(
            (m) => m.ReporteFinancieroList
          ),
      },

      // --- Módulo Catálogo ---
      {
        path: 'catalogo/categorias',
        loadComponent: () =>
          import('./features/catalogo/categoria/categoria-list').then((m) => m.CategoriaList),
      },
      {
        path: 'catalogo/categorias/nueva',
        loadComponent: () =>
          import('./features/catalogo/categoria/categoria-form').then((m) => m.CategoriaForm),
        canActivate: [rolGuard('ADMIN')],
      },
      {
        path: 'catalogo/categorias/:id/editar',
        loadComponent: () =>
          import('./features/catalogo/categoria/categoria-form').then((m) => m.CategoriaForm),
        canActivate: [rolGuard('ADMIN')],
      },
      {
        path: 'catalogo/productos',
        loadComponent: () =>
          import('./features/catalogo/producto/producto-list').then((m) => m.ProductoList),
      },
      {
        path: 'catalogo/productos/nuevo',
        loadComponent: () =>
          import('./features/catalogo/producto/producto-form').then((m) => m.ProductoForm),
        canActivate: [rolGuard('ADMIN')],
      },
      {
        path: 'catalogo/productos/:id/editar',
        loadComponent: () =>
          import('./features/catalogo/producto/producto-form').then((m) => m.ProductoForm),
        canActivate: [rolGuard('ADMIN')],
      },

      // --- Ruta de Acceso Denegado ---
      {
        path: 'sin-permiso',
        loadComponent: () => import('./core/sin-permiso/sin-permiso').then((m) => m.SinPermiso),
      },
      {
        path: 'acceso-denegado',
        loadComponent: () => import('./core/sin-permiso/sin-permiso').then((m) => m.SinPermiso),
      },
    ],
  },
];
