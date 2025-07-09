package com.cevicheria.sistemaventas.controller;

import com.cevicheria.sistemaventas.entity.Venta;
import com.cevicheria.sistemaventas.entity.Venta.EstadoVenta;
import com.cevicheria.sistemaventas.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*") // Para permitir llamadas desde el frontend
public class VentaController {
    
    @Autowired
    private VentaService ventaService;
    
    // Crear nueva venta - POST /api/ventas
    @PostMapping
    public ResponseEntity<?> crearVenta(@Valid @RequestBody Venta venta) {
        try {
            Venta nuevaVenta = ventaService.crearVenta(venta);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaVenta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    // Obtener todas las ventas - GET /api/ventas
    @GetMapping
    public ResponseEntity<List<Venta>> obtenerTodasLasVentas() {
        List<Venta> ventas = ventaService.obtenerTodasLasVentas();
        return ResponseEntity.ok(ventas);
    }
    
    // Obtener ventas con paginación - GET /api/ventas/paginadas?page=0&size=10
    @GetMapping("/paginadas")
    public ResponseEntity<Page<Venta>> obtenerVentasPaginadas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Venta> ventas = ventaService.obtenerVentasPaginadas(pageable);
        return ResponseEntity.ok(ventas);
    }
    
    // Obtener venta por ID - GET /api/ventas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerVentaPorId(@PathVariable Long id) {
        Optional<Venta> venta = ventaService.obtenerVentaPorId(id);
        if (venta.isPresent()) {
            return ResponseEntity.ok(venta.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Actualizar venta - PUT /api/ventas/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarVenta(@PathVariable Long id, @Valid @RequestBody Venta venta) {
        try {
            Venta ventaActualizada = ventaService.actualizarVenta(id, venta);
            return ResponseEntity.ok(ventaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    // Cambiar estado de venta - PATCH /api/ventas/{id}/estado
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstadoVenta(@PathVariable Long id, @RequestBody Map<String, String> estadoMap) {
        try {
            String estadoStr = estadoMap.get("estado");
            EstadoVenta nuevoEstado = EstadoVenta.valueOf(estadoStr.toUpperCase());
            Venta ventaActualizada = ventaService.cambiarEstadoVenta(id, nuevoEstado);
            return ResponseEntity.ok(ventaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Estado inválido o venta no encontrada"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    // Eliminar venta - DELETE /api/ventas/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarVenta(@PathVariable Long id) {
        try {
            ventaService.eliminarVenta(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    // Obtener ventas por cliente - GET /api/ventas/cliente/{clienteId}
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Venta>> obtenerVentasPorCliente(@PathVariable Long clienteId) {
        List<Venta> ventas = ventaService.obtenerVentasPorCliente(clienteId);
        return ResponseEntity.ok(ventas);
    }
    
    // Obtener ventas por estado - GET /api/ventas/estado/{estado}
    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> obtenerVentasPorEstado(@PathVariable String estado) {
        try {
            EstadoVenta estadoVenta = EstadoVenta.valueOf(estado.toUpperCase());
            List<Venta> ventas = ventaService.obtenerVentasPorEstado(estadoVenta);
            return ResponseEntity.ok(ventas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Estado inválido"));
        }
    }
    
    // Obtener ventas del día - GET /api/ventas/hoy
    @GetMapping("/hoy")
    public ResponseEntity<List<Venta>> obtenerVentasDelDia() {
        List<Venta> ventas = ventaService.obtenerVentasDelDia();
        return ResponseEntity.ok(ventas);
    }
    
    // Obtener ventas por rango de fechas - GET /api/ventas/rango?fechaInicio=2024-01-01T00:00:00&fechaFin=2024-01-31T23:59:59
    @GetMapping("/rango")
    public ResponseEntity<List<Venta>> obtenerVentasPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        List<Venta> ventas = ventaService.obtenerVentasPorRangoFechas(fechaInicio, fechaFin);
        return ResponseEntity.ok(ventas);
    }
    
    // Endpoint para obtener los estados disponibles - GET /api/ventas/estados
    @GetMapping("/estados")
    public ResponseEntity<EstadoVenta[]> obtenerEstados() {
        return ResponseEntity.ok(EstadoVenta.values());
    }
    
    // Endpoint para obtener los métodos de pago disponibles - GET /api/ventas/metodos-pago
    @GetMapping("/metodos-pago")
    public ResponseEntity<Venta.MetodoPago[]> obtenerMetodosPago() {
        return ResponseEntity.ok(Venta.MetodoPago.values());
    }
}