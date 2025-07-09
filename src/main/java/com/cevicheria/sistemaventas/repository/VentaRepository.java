package com.cevicheria.sistemaventas.repository;

import com.cevicheria.sistemaventas.entity.Venta;
import com.cevicheria.sistemaventas.entity.Venta.EstadoVenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    
    // Buscar ventas por cliente
    List<Venta> findByClienteId(Long clienteId);
    
    // Buscar ventas por estado
    List<Venta> findByEstado(EstadoVenta estado);
    
    // Buscar ventas por rango de fechas
    @Query("SELECT v FROM Venta v WHERE v.fechaVenta BETWEEN :fechaInicio AND :fechaFin ORDER BY v.fechaVenta DESC")
    List<Venta> findByFechaVentaBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                       @Param("fechaFin") LocalDateTime fechaFin);
    
    // Buscar ventas del día actual
    @Query("SELECT v FROM Venta v WHERE DATE(v.fechaVenta) = DATE(CURRENT_DATE) ORDER BY v.fechaVenta DESC")
    List<Venta> findVentasDelDia();
    
    // Buscar ventas por cliente y estado
    List<Venta> findByClienteIdAndEstado(Long clienteId, EstadoVenta estado);
    
    // Obtener ventas con paginación
    Page<Venta> findByOrderByFechaVentaDesc(Pageable pageable);
    
    // Buscar ventas que contengan un producto específico
    @Query("SELECT DISTINCT v FROM Venta v JOIN v.detalles d WHERE d.producto.id = :productoId")
    List<Venta> findVentasConProducto(@Param("productoId") Long productoId);
    
    // Obtener total de ventas por mes
    @Query("SELECT SUM(v.total) FROM Venta v WHERE MONTH(v.fechaVenta) = :mes AND YEAR(v.fechaVenta) = :año AND v.estado = 'CONFIRMADA'")
    Double getTotalVentasPorMes(@Param("mes") int mes, @Param("año") int año);
    
    // Contar ventas por estado
    long countByEstado(EstadoVenta estado);
}