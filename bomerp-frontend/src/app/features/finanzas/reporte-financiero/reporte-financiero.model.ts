import { MetodoPago } from '../sesion-caja/sesion-caja.model';

export interface MetodoPagoResumen {
  metodoPago: MetodoPago;
  totalIngresos: number;
  totalEgresos: number;
  neto: number;
}

export interface FlujoCajaResumen {
  totalIngresos: number;
  totalEgresos: number;
  saldoNeto: number;
  cantidadTransacciones: number;
}

export interface ReporteRendicionesAgregado {
  totalMonto: number;
  promedioMonto: number;
  montoMaximo: number;
  montoMinimo: number;
  cantidadRendiciones: number;
}

export interface DashboardFinanciero {
  totalIngresos: number;
  totalEgresos: number;
  balanceNeto: number;
  saldoCajaDiariaActiva: number;
  sesionActivaId?: number | null;
  codigoSesionActiva?: string | null;
  cajeroActivo?: string | null;
  resumenPorMetodo: MetodoPagoResumen[];
  flujoCaja: FlujoCajaResumen;
  reporteRendiciones: ReporteRendicionesAgregado;
}
