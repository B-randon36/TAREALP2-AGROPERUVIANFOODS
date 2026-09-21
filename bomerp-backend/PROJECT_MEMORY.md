# BOMERP — Memoria y Contexto del Proyecto

## 1. Información General del Proyecto
- **Sistema:** BOMERP (ERP Ligero + Punto de Venta [POS] Integral).
- **Cliente / Dominio:** Agro Peruvian Foods (elaboración y venta de derivados de cacao y aceite de coco prensado en frío).
- **Definición:** ERP ligero y Punto de Venta (POS) integral diseñado para Agro Peruvian Foods que centraliza y reemplaza la gestión manual que antes se llevaba en Excel y talonarios de notas físicas. Permite administrar de forma unificada las ventas en tienda física, ferias y despachos por delivery, controlando la trazabilidad de inventarios por lotes con fechas de producción y vencimiento (regla FEFO), el costeo unitario de producción por receta (BOM: insumos + mano de obra), las compras a proveedores, el arqueo de turnos de caja y la emisión de tickets mediante impresoras térmicas portátiles.
- **Objetivo General:** Implementar un sistema de gestión empresarial modular (BOMERP) con arquitectura full-stack que automatice y optimice los procesos de ventas omnicanal, costeo de producción, trazabilidad de lotes, abastecimiento y finanzas de Agro Peruvian Foods.
- **Objetivos Específicos:**
  1. *Automatizar las transacciones comerciales (POS):* Agilizar la venta en mostrador, salidas a feria y pedidos con delivery mediante el registro ágil de operaciones y la impresión directa de tickets térmicos (Bluetooth/USB).
  2. *Garantizar la trazabilidad del inventario (Lotes y FEFO):* Asegurar que las salidas de producto descuenten automáticamente el stock de los lotes más próximos a caducar (First Expired, First Out).
  3. *Estandarizar el costeo de producción (BOM):* Calcular el costo unitario exacto sumando insumos de empaque (envases, tapas, precintos, etiquetas) y mano de obra para determinar márgenes de rentabilidad reales.
  4. *Controlar el flujo financiero y arqueo de caja:* Registrar turnos de caja, entradas y salidas menores de efectivo, conciliación por métodos de pago (Efectivo, Yape/Plin, transferencias) y cuentas por cobrar/pagar.
  5. *Consolidar la toma de decisiones con analítica:* Proveer reportes estratégicos consolidados de ventas periódicas, productos con mayor rotación, valorización de stock y balance de caja.

## 2. Stack Tecnológico
- **Backend:** Java 17+, Spring Boot 3+, Spring Data JPA, Spring Modulith (Monolito Modular).
- **Frontend:** Angular 18 (SPA / PWA).
- **Base de Datos:** Oracle Database 23 Free.
- **Hardware Periférico:** Impresoras térmicas ESC/POS (58mm/80mm vía Bluetooth/USB).
- **Librerías Clave:** Jakarta Validation, Lombok, MapStruct, Apache POI / OpenPDF para reportes analíticos.

## 3. Arquitectura y Módulos de Trabajo (4 Frentes / 8 Módulos)
1. **Finanzas y Reportes (Módulo Principal Asignado - Brandon Ccalla):**
   - *Transaccional:* Sesiones de caja y movimientos (`SesionCaja` [Cabecera] / `MovimientoCaja` [Detalle]).
   - *Analítico:* Reportes y métricas de balance financiero, flujo de caja y rentabilidad (modelos de lectura desacoplados).
   - *Exportadores:* `CierreCajaExporter` polimórfico (PDF, Excel).
   - *API Modular Pública:* `FinanzasApi` expuesta con `@NamedInterface("api")` (puerto de cobro para otros dominios).
2. **Ventas y Despacho (Zaggy Morales):** `Venta` / `DetalleVenta`, pedidos de delivery y emisión de tickets térmicos.
3. **Producción e Inventario (Isai Armuto):** `OrdenProduccion` / `DetalleConsumoInsumo`, catálogo, recetas BOM e inventario de lotes con fechas de envasado y vencimiento.
4. **Compras y Terceros (Nestor Huaylla):** `Compra` / `DetalleCompra` y CRM de personas (`Tercero` con roles cliente/proveedor).

## 4. Reglas de Negocio Críticas
- **RN-FIN-01 (Control de Liquidez / Finanzas):** Bloquear cualquier egreso (`EGRESO`) en `EFECTIVO` si el monto supera el saldo disponible actual (`saldoEsperadoEfectivo`) o si la sesión no está en estado `ABIERTA`. Debe lanzar `ReglaNegocioFinanzasException` y gatillar un `@Transactional` rollback inmediato.
- **Regla FEFO (Inventario/Ventas):** Las salidas de inventario descuentan de manera atómica del lote con vencimiento más próximo (*First Expired, First Out*).
- **Estandarización BOM (Producción):** Costeo unitario integrando insumos de empaque (envases, tapas, etiquetas, precintos) + mano de obra.
- **Límites de Módulo:** Todo acoplamiento entre módulos debe respetar Spring Modulith (`@ApplicationModule` en `package-info.java` y pruebas en verde con `ApplicationModules.of(...).verify()`).
- **Alcance Excluido:** NO implementar integración directa con SUNAT (queda desacoplada como adaptador opcional futuro), NO implementar tienda virtual/e-commerce de autoservicio, NO implementar contabilidad por partida doble ni libros SIRE.
