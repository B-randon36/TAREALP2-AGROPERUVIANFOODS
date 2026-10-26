export type UnidadMedida = 'UNIDAD' | 'KG' | 'LITRO';
export type EstadoOrdenProduccion = 'PLANIFICADA' | 'EN_PROCESO' | 'COMPLETADA' | 'CANCELADA';

export interface Insumo {
  id: number;
  codigo: string;
  nombre: string;
  unidadMedida: UnidadMedida;
  costoUnitario: number;
  stockActual: number;
  stockMinimo: number;
  activo: boolean;
}

export interface InsumoCreateRequest {
  codigo: string;
  nombre: string;
  unidadMedida: UnidadMedida;
  costoUnitario: number;
  stockActual: number;
  stockMinimo: number;
}

export interface DetalleReceta {
  id?: number;
  insumoId: number;
  insumoCodigo?: string;
  insumoNombre?: string;
  unidadMedida?: UnidadMedida;
  cantidadRequerida: number;
  costoUnitarioInsumo?: number;
  costoSubtotal?: number;
}

export interface RecetaBOM {
  id: number;
  codigoReceta: string;
  productoId: number;
  productoNombre?: string;
  nombreVersion: string;
  costoManoObraEstimado: number;
  costoTotalProduccion: number;
  vigente: boolean;
  detalles: DetalleReceta[];
}

export interface ItemRecetaRequest {
  insumoId: number;
  cantidadRequerida: number;
}

export interface RecetaBomCreateRequest {
  codigoReceta: string;
  productoId: number;
  nombreVersion: string;
  costoManoObraEstimado: number;
  items: ItemRecetaRequest[];
}

export interface DetalleConsumo {
  id: number;
  insumoId: number;
  insumoCodigo?: string;
  insumoNombre: string;
  unidadMedida: UnidadMedida;
  cantidadConsumida: number;
  costoUnitario: number;
}

export interface OrdenProduccion {
  id: number;
  codigoOrden: string;
  productoId: number;
  productoNombre?: string;
  recetaBomId: number;
  cantidadAProducir: number;
  loteGeneradoId?: number;
  codigoLoteGenerado?: string;
  fechaRegistro: string;
  fechaFinalizacion?: string;
  estado: EstadoOrdenProduccion;
  observaciones?: string;
  consumos?: DetalleConsumo[];
}

export interface CrearOrdenRequest {
  codigoOrden: string;
  recetaBomId: number;
  cantidadAProducir: number;
  observaciones?: string;
}

export interface CompletarOrdenRequest {
  observaciones?: string;
  fechaVencimientoPersonalizada?: string;
}
