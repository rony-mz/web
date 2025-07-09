package com.cevicheria.sistemaventas.controller;

import com.cevicheria.sistemaventas.entity.Producto;
import com.cevicheria.sistemaventas.repository.ProductoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    // Crear nuevo producto - POST /api/productos
    @PostMapping
    public ResponseEntity<?> crearProducto(@Valid @RequestBody Producto producto) {
        try {
            // Validar nombre único
            if (productoRepository.existsByNombreAndActivoTrue(producto.getNombre())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Ya existe un producto con ese nombre"));
            }
            
            Producto nuevoProducto = productoRepository.save(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    // Obtener todos los productos - GET /api/productos
    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodosLosProductos() {
        List<Producto> productos = productoRepository.findAll();
        return ResponseEntity.ok(productos);
    }
    
    // Obtener productos activos - GET /api/productos/activos
    @GetMapping("/activos")
    public ResponseEntity<List<Producto>> obtenerProductosActivos() {
        List<Producto> productos = productoRepository.findByActivoTrue();
        return ResponseEntity.ok(productos);
    }
    
    // Obtener producto por ID - GET /api/productos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        Optional<Producto> producto = productoRepository.findById(id);
        return producto.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    // Buscar productos por nombre - GET /api/productos/buscar?q=nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarProductos(@RequestParam String q) {
        List<Producto> productos = productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(q);
        return ResponseEntity.ok(productos);
    }
    
    // Obtener productos con stock bajo - GET /api/productos/stock-bajo?minimo=5
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<Producto>> obtenerProductosConStockBajo(@RequestParam(defaultValue = "5") Integer minimo) {
        List<Producto> productos = productoRepository.findProductosConStockBajo(minimo);
        return ResponseEntity.ok(productos);
    }
    
    // Actualizar producto - PUT /api/productos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @Valid @RequestBody Producto productoActualizado) {
        Optional<Producto> productoExistente = productoRepository.findById(id);
        
        if (productoExistente.isPresent()) {
            Producto producto = productoExistente.get();
            
            // Validar nombre único (si es diferente al actual)
            if (!productoActualizado.getNombre().equals(producto.getNombre()) &&
                productoRepository.existsByNombreAndActivoTrue(productoActualizado.getNombre())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Ya existe un producto con ese nombre"));
            }
            
            producto.setNombre(productoActualizado.getNombre());
            producto.setDescripcion(productoActualizado.getDescripcion());
            producto.setPrecio(productoActualizado.getPrecio());
            producto.setStock(productoActualizado.getStock());
            producto.setUnidadMedida(productoActualizado.getUnidadMedida());
            
            Producto productoGuardado = productoRepository.save(producto);
            return ResponseEntity.ok(productoGuardado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Actualizar stock - PATCH /api/productos/{id}/stock
    @PatchMapping("/{id}/stock")
    public ResponseEntity<?> actualizarStock(@PathVariable Long id, @RequestBody Map<String, Integer> stockMap) {
        Optional<Producto> productoOpt = productoRepository.findById(id);
        
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            Integer nuevoStock = stockMap.get("stock");
            
            if (nuevoStock == null || nuevoStock < 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Stock inválido"));
            }
            
            producto.setStock(nuevoStock);
            Producto productoGuardado = productoRepository.save(producto);
            return ResponseEntity.ok(productoGuardado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Desactivar producto - PATCH /api/productos/{id}/desactivar
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Producto> desactivarProducto(@PathVariable Long id) {
        Optional<Producto> productoOpt = productoRepository.findById(id);
        
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            producto.setActivo(false);
            Producto productoGuardado = productoRepository.save(producto);
            return ResponseEntity.ok(productoGuardado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Activar producto - PATCH /api/productos/{id}/activar
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Producto> activarProducto(@PathVariable Long id) {
        Optional<Producto> productoOpt = productoRepository.findById(id);
        
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            producto.setActivo(true);
            Producto productoGuardado = productoRepository.save(producto);
            return ResponseEntity.ok(productoGuardado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}