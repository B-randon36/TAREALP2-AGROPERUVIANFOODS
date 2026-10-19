export type EstadoCaja = 'ABIERTA' | 'CERRADA';
export type MetodoPago = 'EFECTIVO' | 'YAPE' | 'PLIN' | 'TRANSFERENCIA' | 'TARJETA';
export type TipoFinanciero = 'INGRESO' | 'EGRESO';

export interface SesionCaja {
  id: number;
  codigoSesion: string;
  usuarioId: number;
  cajero: string;
  fechaApertura: string;
  fechaCierre?: string | null;
  saldoInicial: number;
  totalIngresosEfectivo: number;
  totalEgresosEfectivo: number;
  saldoEsperadoEfectivo: number;
  saldoRealEfectivo?: number | null;
  diferencia: number;
  estado: EstadoCaja;
  observaciones?: string | null;
  cantidadMovimientos: number;
}

export interface MovimientoCaja {
  id: number;
  fechaHora: string;
  tipo: TipoFinanciero;
  metodoPago: MetodoPago;
  monto: number;
  descripcion: string;
  referenciaExterna?: string | null;
  categoriaFinancieraId: number;
  categoriaFinancieraNombre?: string | null;
  sesionCajaId: number;
}

export interface SesionCajaDetalle extends SesionCaja {
  movimientos: MovimientoCaja[];
}

export interface AperturaSesionRequest {
  usuarioId: number;
  cajero: string;
  saldoInicial: number;
  codigoSesion?: string | null;
}

export interface CierreSesionRequest {
  saldoRealEfectivo: number;
  observaciones?: string | null;
}

export interface MovimientoCajaRequest {
  tipo: TipoFinanciero;
  metodoPago: MetodoPago;
  monto: number;
  descripcion: string;
  referenciaExterna?: string | null;
  categoriaFinancieraId: number;
}

export interface MetodoPagoResumen {
  metodoPago: MetodoPago;
  totalIngresos: number;
  totalEgresos: number;
  neto: number;
}

export interface CierreCajaReporte {
  empresaNombre: string;
  ruc: string;
  direccion: string;
  fechaGeneracion: string;
  sesion: SesionCaja;
  movimientos: MovimientoCaja[];
  resumenPorMetodo: MetodoPagoResumen[];
}

export interface ArqueoDenominacion {
  denominacion: number;
  cantidad: number;
  tipo: 'BILLETE' | 'MONEDA';
}
