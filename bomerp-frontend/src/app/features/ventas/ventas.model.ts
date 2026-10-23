export type CanalVenta = 'TIENDA_MOSTRADOR' | 'FERIA_COMERCIAL' | 'DELIVERY';
export type MetodoPagoVenta = 'EFECTIVO' | 'TARJETA' | 'TRANSFERENCIA_DIGITAL';
export type EstadoVenta = 'EMITIDA' | 'ANULADA';

export interface VentaItemRequest {
  productoId: number;
  cantidad: number;
  precioUnitario: number;
}

export interface VentaCreateRequest {
  canal: CanalVenta;
  clienteTipo?: string;
  clienteNombre?: string;
  clienteNumeroDoc?: string;
  metodoPago: MetodoPagoVenta;
  montoPagado?: number;
  cajeroNombre?: string;
  items: VentaItemRequest[];
}

export interface DetalleVentaResponse {
  id: number;
  productoId: number;
  productoNombre: string;
  productoImagenUrl?: string | null;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
  lotesConsumidosFefo?: string;
}

export interface VentaResponse {
  id: number;
  codigoVenta: string;
  fechaHora: string;
  canal: CanalVenta;
  clienteTipo: string;
  clienteNombre: string;
  clienteNumeroDoc?: string;
  metodoPago: MetodoPagoVenta;
  subtotal: number;
  igv: number;
  total: number;
  montoPagado: number;
  vuelto: number;
  sesionCajaId?: number;
  cajeroNombre?: string;
  estado: EstadoVenta;
  detalles: DetalleVentaResponse[];
}
