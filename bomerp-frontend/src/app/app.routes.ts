import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./core/layout/layout').then((m) => m.Layout),
    children: [
      {
        path: '',
        loadComponent: () => import('./core/inicio/inicio').then((m) => m.Inicio),
      },
      // --- Módulo Finanzas y Reportes (Módulo Asignado) ---
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
      },
      {
        path: 'finanzas/transacciones/:id/editar',
        loadComponent: () =>
          import('./features/finanzas/transaccion-financiera/transaccion-financiera-form').then(
            (m) => m.TransaccionFinancieraForm
          ),
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
      },
      {
        path: 'finanzas/categorias/:id/editar',
        loadComponent: () =>
          import('./features/finanzas/categoria-financiera/categoria-financiera-form').then(
            (m) => m.CategoriaFinancieraForm
          ),
      },
      // --- Operación Transaccional Cabecera-Detalle: Rendiciones de Caja Chica ---
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
      },
      {
        path: 'catalogo/categorias/:id/editar',
        loadComponent: () =>
          import('./features/catalogo/categoria/categoria-form').then((m) => m.CategoriaForm),
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
      },
      {
        path: 'catalogo/productos/:id/editar',
        loadComponent: () =>
          import('./features/catalogo/producto/producto-form').then((m) => m.ProductoForm),
      },
    ],
  },
];
