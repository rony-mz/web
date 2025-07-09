package com.cevicheria.sistemaventas.repository;

import com.cevicheria.sistemaventas.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    // Buscar clientes activos
    List<Cliente> findByActivoTrue();
    
    // Buscar por nombre o apellido
    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR LOWER(c.apellido) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    List<Cliente> findByNombreOrApellidoContainingIgnoreCase(@Param("busqueda") String busqueda);
    
    // Buscar por email
    Optional<Cliente> findByEmail(String email);
    
    // Buscar por teléfono
    Optional<Cliente> findByTelefono(String telefono);
    
    // Verificar si existe email
    boolean existsByEmail(String email);
    
    // Verificar si existe teléfono
    boolean existsByTelefono(String telefono);
}