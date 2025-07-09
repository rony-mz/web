# Sistema de Ventas - Cevichería

Sistema CRUD completo para gestión de ventas desarrollado en Spring Boot para una cevichería.

## Características

- ✅ CRUD completo de Ventas
- ✅ Gestión de Clientes y Productos
- ✅ Control de inventario automático
- ✅ Estados de venta (Pendiente, Confirmada, Entregada, Cancelada)
- ✅ Múltiples métodos de pago
- ✅ Cálculo automático de IGV (18%)
- ✅ API REST con validaciones
- ✅ Base de datos H2 para desarrollo
- ✅ Datos de ejemplo incluidos

## Tecnologías

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Web**
- **H2 Database** (desarrollo)
- **MySQL** (producción)
- **Maven**

## Instalación y Ejecución

### Prerrequisitos

- Java 17 o superior
- Maven 3.6 o superior

### Pasos

1. **Clonar o descargar el proyecto**

2. **Ejecutar la aplicación**
   ```bash
   mvn spring-boot:run
   ```

3. **La aplicación estará disponible en:**
   - API: http://localhost:8080
   - Consola H2: http://localhost:8080/h2-console
     - URL: `jdbc:h2:mem:testdb`
     - Usuario: `sa`
     - Contraseña: `password`

## API Endpoints

### Ventas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/ventas` | Obtener todas las ventas |
| `GET` | `/api/ventas/paginadas?page=0&size=10` | Obtener ventas paginadas |
| `GET` | `/api/ventas/{id}` | Obtener venta por ID |
| `POST` | `/api/ventas` | Crear nueva venta |
| `PUT` | `/api/ventas/{id}` | Actualizar venta |
| `DELETE` | `/api/ventas/{id}` | Eliminar venta |
| `PATCH` | `/api/ventas/{id}/estado` | Cambiar estado de venta |
| `GET` | `/api/ventas/cliente/{clienteId}` | Ventas por cliente |
| `GET` | `/api/ventas/estado/{estado}` | Ventas por estado |
| `GET` | `/api/ventas/hoy` | Ventas del día |
| `GET` | `/api/ventas/rango` | Ventas por rango de fechas |
| `GET` | `/api/ventas/estados` | Obtener estados disponibles |
| `GET` | `/api/ventas/metodos-pago` | Obtener métodos de pago |

### Clientes

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/clientes` | Obtener todos los clientes |
| `GET` | `/api/clientes/activos` | Obtener clientes activos |
| `GET` | `/api/clientes/{id}` | Obtener cliente por ID |
| `POST` | `/api/clientes` | Crear nuevo cliente |
| `PUT` | `/api/clientes/{id}` | Actualizar cliente |
| `PATCH` | `/api/clientes/{id}/activar` | Activar cliente |
| `PATCH` | `/api/clientes/{id}/desactivar` | Desactivar cliente |
| `GET` | `/api/clientes/buscar?q=nombre` | Buscar clientes |

### Productos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/productos` | Obtener todos los productos |
| `GET` | `/api/productos/activos` | Obtener productos activos |
| `GET` | `/api/productos/{id}` | Obtener producto por ID |
| `POST` | `/api/productos` | Crear nuevo producto |
| `PUT` | `/api/productos/{id}` | Actualizar producto |
| `PATCH` | `/api/productos/{id}/stock` | Actualizar stock |
| `PATCH` | `/api/productos/{id}/activar` | Activar producto |
| `PATCH` | `/api/productos/{id}/desactivar` | Desactivar producto |
| `GET` | `/api/productos/buscar?q=nombre` | Buscar productos |
| `GET` | `/api/productos/stock-bajo?minimo=5` | Productos con stock bajo |

## Ejemplos de Uso

### 1. Crear una nueva venta

```bash
curl -X POST http://localhost:8080/api/ventas \
  -H "Content-Type: application/json" \
  -d '{
    "cliente": {
      "id": 1
    },
    "metodoPago": "EFECTIVO",
    "observaciones": "Venta de prueba",
    "detalles": [
      {
        "producto": {
          "id": 1
        },
        "cantidad": 2
      },
      {
        "producto": {
          "id": 2
        },
        "cantidad": 1
      }
    ]
  }'
```

### 2. Obtener todas las ventas

```bash
curl http://localhost:8080/api/ventas
```

### 3. Cambiar estado de una venta

```bash
curl -X PATCH http://localhost:8080/api/ventas/1/estado \
  -H "Content-Type: application/json" \
  -d '{"estado": "CONFIRMADA"}'
```

### 4. Crear un nuevo cliente

```bash
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Pedro",
    "apellido": "Gonzales",
    "telefono": "987654325",
    "email": "pedro.gonzales@email.com",
    "direccion": "Av. Brasil 456"
  }'
```

### 5. Crear un nuevo producto

```bash
curl -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Ceviche Mixto",
    "descripcion": "Ceviche con pescado y mariscos",
    "precio": 30.00,
    "stock": 20,
    "unidadMedida": "Porción"
  }'
```

## Estados de Venta

- **PENDIENTE**: Venta recién creada
- **CONFIRMADA**: Venta confirmada por el cliente
- **ENTREGADA**: Venta entregada al cliente
- **CANCELADA**: Venta cancelada

## Métodos de Pago

- **EFECTIVO**
- **TARJETA**
- **TRANSFERENCIA**
- **YAPE**
- **PLIN**

## Funcionalidades Especiales

### Control de Inventario
- Al crear una venta, se reduce automáticamente el stock de los productos
- Al cancelar una venta, se restaura el stock de los productos
- Validación de stock disponible antes de crear la venta

### Validaciones
- Email y teléfono únicos para clientes
- Nombres únicos para productos
- Stock suficiente para ventas
- Estados válidos para transiciones de venta

### Cálculo Automático
- Subtotal por cada detalle de venta
- IGV del 18% sobre el subtotal
- Total de la venta (subtotal + IGV)

## Configuración para Producción

Para usar MySQL en producción, modifica `application.properties`:

```properties
# Comentar configuración H2 y descomentar MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/cevicheria_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
```

## Datos de Ejemplo

El sistema incluye datos de ejemplo que se cargan automáticamente:

### Clientes
- Juan Pérez
- María García  
- Carlos López
- Ana Martínez

### Productos
- Ceviche de Pescado - S/. 25.00
- Tiradito de Pescado - S/. 22.00
- Arroz con Mariscos - S/. 28.00
- Sudado de Pescado - S/. 24.00
- Leche de Tigre - S/. 8.00
- Chicha Morada - S/. 5.00
- Inca Kola - S/. 4.00
- Agua Mineral - S/. 2.50

## Estructura del Proyecto

```
src/main/java/com/cevicheria/sistemaventas/
├── entity/           # Entidades JPA
├── repository/       # Repositorios de datos
├── service/          # Lógica de negocio
├── controller/       # Controladores REST
└── config/           # Configuración y datos de ejemplo
```

## Autor

Sistema desarrollado para gestión de ventas en cevicherías con Spring Boot.
