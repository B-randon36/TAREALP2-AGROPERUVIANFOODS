         

# 

## 

Esta sesión no enseña contenido nuevo: cierra la Unidad II de LP2. El sílabo (sesión 12) define dos actividades:

- 

- 

Esta sesión coincide con el "Segundo corte integrado" de los tres cursos, según el cronograma del Proyecto Integrador: ADS evalúa su catálogo UML esta misma semana, en su propia sesión 12; BD2 evalúa su base de datos administrada esta misma semana, en su propia sesión 12. La sustentación de hoy es individual de LP2 — tu SPA, tu código, tu evidencia —, pero la sección 4 exige además evidencia de que esa SPA funciona sobre el backend real y la base administrada de BD2, no aislada.
Sobre la seguridad evaluada: Keycloak, no JWT propio. S10 construyó un módulo seguridad con JWT propio, deliberadamente temporal (ADR-005). S10b — de ejecución obligatoria, no opcional — reemplazó ese módulo por Keycloak antes de S11. Lo que esta sesión evalúa es ese estado real: bomerp-backend validando tokens de Keycloak, y la SPA iniciando sesión con Authorization Code + PKCE — no el JWT hecho a mano que S10 construyó como primer paso pedagógico.

## 

Del sílabo, el producto de la Unidad II es:

Frontend SPA empresarial seguro, conectado al backend, con navegación modular, CRUD de tablas independientes y dependientes, formularios transaccionales, consultas, reportes y control de acceso.

El producto completo — plantilla-ejemplo con el contenido de BomERP — vive en lp2-demo.md: organización funcional, evidencias exigidas, casos de prueba, integración esperada con Keycloak, rúbrica de evaluación y trazabilidad de esa rúbrica contra la malla curricular. La estructura es exigible a todos; el contenido de BomERP se reemplaza por el del propio proyecto de cada equipo.

### 

Este producto no se construye en S12: se ensambla con lo que cada sesión anterior ya te pidió sobre tu propia SPA.
Tabla 1. De la sesión a la SPA evaluada

Lo que sustentas en S12 es tu SPA: los módulos que tú construiste, con los datos y reglas de tu propio dominio, autenticados contra tu propio realm de Keycloak — no el de BomERP. lp2-demo.md muestra cómo se ve esa SPA terminada usando el ejemplo del docente; tu entregable real tiene la misma estructura, con el contenido que tú construiste en S7-S11.

## 

Cubre los cinco temas dictados antes de esta sesión. El docente puede tomarla escrita, oral o mixta.
Tabla 2. Temario de la evaluación teórico-práctica

Preguntas de referencia (el docente puede formular equivalentes):

- 

- 

- 

- 

- 

- 

- 

## 

Tabla 3. Distribución de tiempo por integrante

Tabla 4. Entregables obligatorios

Secuencia sugerida de presentación (referencias a secciones de lp2-demo.md):

- 

- 

- 

- 

- 

- 

- 

Criterios mínimos de aceptación:

- 

- 

- 

- 

- 

- 

- 

## 

La rúbrica (6 criterios: 5 cita literal de los criterios de evaluación del producto de la Unidad II + sustentación) vive en lp2-demo.md, junto con la plantilla del producto y su trazabilidad con la malla curricular (CE023 Nivel 2, parcial). Úsala directamente desde ahí para calificar la sustentación de esta sesión — no se duplica aquí.

var target=document.getElementById(location.hash.slice(1));target&&target.name&&(target.checked=target.name.startsWith("__tabbed_"))

  Volver al principio

        