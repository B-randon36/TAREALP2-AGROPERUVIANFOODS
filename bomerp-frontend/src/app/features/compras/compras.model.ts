export type EstadoCompra = 'REGISTRADA' | 'RECIBIDA' | 'ANULADA';

export interface DetalleCompra {
  id?: number;
  insumoId: number;
  insumoNombre?: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

export interface Compra {
  id: number;
  codigoCompra: string;
  proveedorId: number;
  proveedorNombre: string;
  numeroComprobante: string;
  fechaCompra: string;
  subtotal: number;
  igv: number;
  total: number;
  estado: EstadoCompra;
  metodoPago: string;
  observaciones?: string;
  detalles: DetalleCompra[];
}

export interface ItemCompraRequest {
  insumoId: number;
  cantidad: number;
  precioUnitario: number;
}

export interface RegistrarCompraRequest {
  codigoCompra?: string;
  proveedorId: number;
  numeroComprobante: string;
  fechaCompra: string;
  metodoPago: string;
  observaciones?: string;
  items: ItemCompraRequest[];
}
