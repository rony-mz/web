package com.cevicheria.sistemaventas.controller;

import com.cevicheria.sistemaventas.entity.Cliente;
import com.cevicheria.sistemaventas.repository.ClienteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    // Crear nuevo cliente - POST /api/clientes
    @PostMapping
    public ResponseEntity<?> crearCliente(@Valid @RequestBody Cliente cliente) {
        try {
            // Validar email único
            if (cliente.getEmail() != null && clienteRepository.existsByEmail(cliente.getEmail())) {
                return ResponseEntity.badRequest().body(Map.of("error", "El email ya está registrado"));
            }
            
            // Validar teléfono único
            if (cliente.getTelefono() != null && clienteRepository.existsByTelefono(cliente.getTelefono())) {
                return ResponseEntity.badRequest().body(Map.of("error", "El teléfono ya está registrado"));
            }
            
            Cliente nuevoCliente = clienteRepository.save(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    // Obtener todos los clientes - GET /api/clientes
    @GetMapping
    public ResponseEntity<List<Cliente>> obtenerTodosLosClientes() {
        List<Cliente> clientes = clienteRepository.findAll();
        return ResponseEntity.ok(clientes);
    }
    
    // Obtener clientes activos - GET /api/clientes/activos
    @GetMapping("/activos")
    public ResponseEntity<List<Cliente>> obtenerClientesActivos() {
        List<Cliente> clientes = clienteRepository.findByActivoTrue();
        return ResponseEntity.ok(clientes);
    }
    
    // Obtener cliente por ID - GET /api/clientes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerClientePorId(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        return cliente.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    // Buscar clientes por nombre - GET /api/clientes/buscar?q=nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<Cliente>> buscarClientes(@RequestParam String q) {
        List<Cliente> clientes = clienteRepository.findByNombreOrApellidoContainingIgnoreCase(q);
        return ResponseEntity.ok(clientes);
    }
    
    // Actualizar cliente - PUT /api/clientes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCliente(@PathVariable Long id, @Valid @RequestBody Cliente clienteActualizado) {
        Optional<Cliente> clienteExistente = clienteRepository.findById(id);
        
        if (clienteExistente.isPresent()) {
            Cliente cliente = clienteExistente.get();
            
            // Validar email único (si es diferente al actual)
            if (clienteActualizado.getEmail() != null && 
                !clienteActualizado.getEmail().equals(cliente.getEmail()) &&
                clienteRepository.existsByEmail(clienteActualizado.getEmail())) {
                return ResponseEntity.badRequest().body(Map.of("error", "El email ya está registrado"));
            }
            
            // Validar teléfono único (si es diferente al actual)
            if (clienteActualizado.getTelefono() != null && 
                !clienteActualizado.getTelefono().equals(cliente.getTelefono()) &&
                clienteRepository.existsByTelefono(clienteActualizado.getTelefono())) {
                return ResponseEntity.badRequest().body(Map.of("error", "El teléfono ya está registrado"));
            }
            
            cliente.setNombre(clienteActualizado.getNombre());
            cliente.setApellido(clienteActualizado.getApellido());
            cliente.setTelefono(clienteActualizado.getTelefono());
            cliente.setEmail(clienteActualizado.getEmail());
            cliente.setDireccion(clienteActualizado.getDireccion());
            
            Cliente clienteGuardado = clienteRepository.save(cliente);
            return ResponseEntity.ok(clienteGuardado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Desactivar cliente - PATCH /api/clientes/{id}/desactivar
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Cliente> desactivarCliente(@PathVariable Long id) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(id);
        
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            cliente.setActivo(false);
            Cliente clienteGuardado = clienteRepository.save(cliente);
            return ResponseEntity.ok(clienteGuardado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Activar cliente - PATCH /api/clientes/{id}/activar
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Cliente> activarCliente(@PathVariable Long id) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(id);
        
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            cliente.setActivo(true);
            Cliente clienteGuardado = clienteRepository.save(cliente);
            return ResponseEntity.ok(clienteGuardado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}