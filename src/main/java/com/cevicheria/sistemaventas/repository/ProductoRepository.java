package com.cevicheria.sistemaventas.repository;

import com.cevicheria.sistemaventas.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    // Buscar productos activos
    List<Producto> findByActivoTrue();
    
    // Buscar por nombre
    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND p.activo = true")
    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(@Param("nombre") String nombre);
    
    // Buscar productos con stock bajo
    @Query("SELECT p FROM Producto p WHERE p.stock <= :stockMinimo AND p.activo = true")
    List<Producto> findProductosConStockBajo(@Param("stockMinimo") Integer stockMinimo);
    
    // Buscar productos sin stock
    List<Producto> findByStockAndActivoTrue(Integer stock);
    
    // Verificar si existe producto por nombre
    boolean existsByNombreAndActivoTrue(String nombre);
}