# CRUD de Ventas - Guía Completa

Esta guía explica cómo usar el CRUD completo de ventas desarrollado en Spring Boot.

## Funcionamiento del Sistema

### 1. Flujo de una Venta

1. **Cliente** realiza un pedido
2. Se crea una **Venta** en estado `PENDIENTE`
3. Se agregan **DetalleVenta** con productos y cantidades
4. El sistema calcula automáticamente:
   - Subtotal por producto
   - IGV (18%)
   - Total de la venta
5. Se reduce el **stock** de los productos
6. La venta cambia de estado según el proceso

### 2. Estados de Venta y Transiciones

```
PENDIENTE → CONFIRMADA → ENTREGADA
    ↓           ↓
CANCELADA   CANCELADA
```

- **PENDIENTE**: Venta inicial, se puede modificar/eliminar
- **CONFIRMADA**: Venta confirmada, no se puede modificar
- **ENTREGADA**: Venta completada
- **CANCELADA**: Venta cancelada, restaura stock

## Operaciones CRUD

### CREATE (Crear Venta)

```bash
curl -X POST http://localhost:8080/api/ventas \
  -H "Content-Type: application/json" \
  -d '{
    "cliente": {
      "id": 1
    },
    "metodoPago": "EFECTIVO",
    "observaciones": "Mesa 5 - Sin cebolla",
    "detalles": [
      {
        "producto": {
          "id": 1
        },
        "cantidad": 2
      },
      {
        "producto": {
          "id": 5
        },
        "cantidad": 3
      }
    ]
  }'
```

**Respuesta:**
```json
{
  "id": 1,
  "cliente": {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez"
  },
  "fechaVenta": "2024-01-15T14:30:00",
  "subtotal": 74.00,
  "igv": 13.32,
  "total": 87.32,
  "estado": "PENDIENTE",
  "metodoPago": "EFECTIVO",
  "observaciones": "Mesa 5 - Sin cebolla",
  "detalles": [...]
}
```

### READ (Leer Ventas)

#### Obtener todas las ventas
```bash
curl http://localhost:8080/api/ventas
```

#### Obtener venta específica
```bash
curl http://localhost:8080/api/ventas/1
```

#### Obtener ventas paginadas
```bash
curl http://localhost:8080/api/ventas/paginadas?page=0&size=5
```

#### Obtener ventas por cliente
```bash
curl http://localhost:8080/api/ventas/cliente/1
```

#### Obtener ventas por estado
```bash
curl http://localhost:8080/api/ventas/estado/PENDIENTE
```

#### Obtener ventas del día
```bash
curl http://localhost:8080/api/ventas/hoy
```

#### Obtener ventas por rango de fechas
```bash
curl "http://localhost:8080/api/ventas/rango?fechaInicio=2024-01-01T00:00:00&fechaFin=2024-01-31T23:59:59"
```

### UPDATE (Actualizar Venta)

⚠️ **Solo se pueden actualizar ventas en estado PENDIENTE**

```bash
curl -X PUT http://localhost:8080/api/ventas/1 \
  -H "Content-Type: application/json" \
  -d '{
    "metodoPago": "TARJETA",
    "observaciones": "Mesa 5 - Sin cebolla - Tarjeta Visa",
    "detalles": [
      {
        "producto": {
          "id": 1
        },
        "cantidad": 3
      }
    ]
  }'
```

#### Cambiar estado de venta
```bash
curl -X PATCH http://localhost:8080/api/ventas/1/estado \
  -H "Content-Type: application/json" \
  -d '{"estado": "CONFIRMADA"}'
```

### DELETE (Eliminar Venta)

⚠️ **Solo se pueden eliminar ventas en estado PENDIENTE**

```bash
curl -X DELETE http://localhost:8080/api/ventas/1
```

## Validaciones Automáticas

### 1. Control de Stock
```bash
# Si intentas vender más cantidad de la disponible:
curl -X POST http://localhost:8080/api/ventas \
  -H "Content-Type: application/json" \
  -d '{
    "cliente": {"id": 1},
    "detalles": [
      {
        "producto": {"id": 1},
        "cantidad": 1000
      }
    ]
  }'

# Respuesta de error:
{
  "error": "Stock insuficiente para el producto: Ceviche de Pescado"
}
```

### 2. Cliente Activo
```bash
# Si el cliente no está activo:
{
  "error": "El cliente no está activo"
}
```

### 3. Transiciones de Estado Inválidas
```bash
# Si intentas cambiar de ENTREGADA a PENDIENTE:
{
  "error": "Transición de estado no válida"
}
```

## Casos de Uso Comunes

### Caso 1: Venta Completa (Flujo Normal)

```bash
# 1. Crear venta
curl -X POST http://localhost:8080/api/ventas \
  -H "Content-Type: application/json" \
  -d '{
    "cliente": {"id": 1},
    "metodoPago": "EFECTIVO",
    "detalles": [
      {"producto": {"id": 1}, "cantidad": 1},
      {"producto": {"id": 6}, "cantidad": 2}
    ]
  }'

# 2. Confirmar venta
curl -X PATCH http://localhost:8080/api/ventas/1/estado \
  -H "Content-Type: application/json" \
  -d '{"estado": "CONFIRMADA"}'

# 3. Marcar como entregada
curl -X PATCH http://localhost:8080/api/ventas/1/estado \
  -H "Content-Type: application/json" \
  -d '{"estado": "ENTREGADA"}'
```

### Caso 2: Venta Cancelada

```bash
# 1. Crear venta
curl -X POST http://localhost:8080/api/ventas \
  -H "Content-Type: application/json" \
  -d '{
    "cliente": {"id": 2},
    "metodoPago": "YAPE",
    "detalles": [
      {"producto": {"id": 3}, "cantidad": 1}
    ]
  }'

# 2. Cancelar venta (restaura stock automáticamente)
curl -X PATCH http://localhost:8080/api/ventas/2/estado \
  -H "Content-Type: application/json" \
  -d '{"estado": "CANCELADA"}'
```

### Caso 3: Modificar Venta Pendiente

```bash
# 1. Crear venta
curl -X POST http://localhost:8080/api/ventas \
  -H "Content-Type: application/json" \
  -d '{
    "cliente": {"id": 1},
    "metodoPago": "EFECTIVO",
    "detalles": [
      {"producto": {"id": 1}, "cantidad": 1}
    ]
  }'

# 2. Modificar venta (agregar más productos)
curl -X PUT http://localhost:8080/api/ventas/3 \
  -H "Content-Type: application/json" \
  -d '{
    "metodoPago": "TARJETA",
    "observaciones": "Cliente cambió a tarjeta",
    "detalles": [
      {"producto": {"id": 1}, "cantidad": 1},
      {"producto": {"id": 2}, "cantidad": 1},
      {"producto": {"id": 7}, "cantidad": 2}
    ]
  }'
```

## Endpoints de Apoyo

### Obtener Estados Disponibles
```bash
curl http://localhost:8080/api/ventas/estados
# Respuesta: ["PENDIENTE", "CONFIRMADA", "ENTREGADA", "CANCELADA"]
```

### Obtener Métodos de Pago
```bash
curl http://localhost:8080/api/ventas/metodos-pago
# Respuesta: ["EFECTIVO", "TARJETA", "TRANSFERENCIA", "YAPE", "PLIN"]
```

### Obtener Clientes Activos
```bash
curl http://localhost:8080/api/clientes/activos
```

### Obtener Productos Activos
```bash
curl http://localhost:8080/api/productos/activos
```

## Respuestas de Error Comunes

### Error de Validación
```json
{
  "error": "Errores de validación",
  "fields": {
    "cliente": "El cliente es obligatorio",
    "detalles[0].cantidad": "La cantidad debe ser mayor a 0"
  }
}
```

### Error de Negocio
```json
{
  "error": "Stock insuficiente para el producto: Ceviche de Pescado"
}
```

### Error de Estado
```json
{
  "error": "Solo se pueden modificar ventas en estado PENDIENTE"
}
```

## Cálculos Automáticos

El sistema calcula automáticamente:

1. **Precio Unitario**: Se toma del producto actual
2. **Subtotal por Detalle**: precio × cantidad
3. **Subtotal General**: suma de todos los subtotales
4. **IGV (18%)**: subtotal × 0.18
5. **Total**: subtotal + IGV

### Ejemplo de Cálculo:
- Ceviche de Pescado × 2 = S/. 25.00 × 2 = S/. 50.00
- Chicha Morada × 3 = S/. 5.00 × 3 = S/. 15.00
- **Subtotal**: S/. 65.00
- **IGV (18%)**: S/. 11.70
- **Total**: S/. 76.70

## Consideraciones Importantes

1. **Stock**: Se actualiza automáticamente al crear/cancelar ventas
2. **Validaciones**: El sistema valida stock, clientes activos, productos activos
3. **Estados**: Solo ciertas transiciones están permitidas
4. **Modificaciones**: Solo ventas PENDIENTES pueden modificarse/eliminarse
5. **Concurrencia**: El sistema maneja transacciones para evitar inconsistencias

Este CRUD está diseñado para manejar el flujo completo de ventas en una cevichería, desde la creación hasta la entrega, con todas las validaciones necesarias para mantener la integridad de los datos.