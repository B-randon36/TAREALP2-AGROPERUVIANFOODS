# Directivas y Reglas del Agente — BOMERP

## Regla Obligatoria de Inicio de Contexto
- **CRÍTICO:** Antes de ejecutar comandos, inspeccionar directorios, proponer soluciones o modificar código, se DEBE consultar y respetar obligatoriamente el archivo [PROJECT_MEMORY.md](file:///e:/repoprofe/bomerp-backend/PROJECT_MEMORY.md) y la configuración en [.gemini/config.json](file:///e:/repoprofe/bomerp-backend/.gemini/config.json).

## Directivas Arquitecturales y de Negocio
1. **Separación de Módulos (Spring Modulith):**
   - Respetar la división en 4 frentes:
     - **Finanzas y Reportes (Brandon Ccalla):** `SesionCaja`, `MovimientoCaja`, `FinanzasApi` (`@NamedInterface("api")`), `CierreCajaExporter`.
     - **Ventas y Despacho (Zaggy Morales):** `Venta`, `DetalleVenta`, tickets ESC/POS, delivery.
     - **Producción e Inventario (Isai Armuto):** `OrdenProduccion`, consumo de insumos, catálogo, lotes y regla FEFO.
     - **Compras y Terceros (Nestor Huaylla):** `Compra`, `DetalleCompra`, CRM `Tercero`.
   - Prohibido acoplar repositorios o entidades directamente entre módulos distintos. Toda comunicación intermodular debe realizarse mediante puertos / APIs públicas (`@NamedInterface`).
2. **Reglas de Negocio Clave:**
   - **RN-FIN-01:** Bloquear egresos en efectivo si el monto supera `saldoEsperadoEfectivo` o la sesión no está en estado `ABIERTA`. Lanzar `ReglaNegocioFinanzasException` con rollback transaccional.
   - **FEFO:** Descuento atómico por fecha de vencimiento más próxima en salidas de inventario.
3. **Límites de Alcance:**
   - NO implementar integración directa con SUNAT.
   - NO implementar e-commerce o tienda virtual.
   - Si una instrucción contradice estas reglas o las de `PROJECT_MEMORY.md`, advertir al usuario antes de proceder.
