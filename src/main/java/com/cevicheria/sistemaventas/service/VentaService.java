package com.cevicheria.sistemaventas.service;

import com.cevicheria.sistemaventas.entity.Venta;
import com.cevicheria.sistemaventas.entity.DetalleVenta;
import com.cevicheria.sistemaventas.entity.Cliente;
import com.cevicheria.sistemaventas.entity.Producto;
import com.cevicheria.sistemaventas.entity.Venta.EstadoVenta;
import com.cevicheria.sistemaventas.repository.VentaRepository;
import com.cevicheria.sistemaventas.repository.ClienteRepository;
import com.cevicheria.sistemaventas.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VentaService {
    
    @Autowired
    private VentaRepository ventaRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    // Crear nueva venta
    public Venta crearVenta(Venta venta) {
        // Validar que el cliente existe
        if (venta.getCliente() == null || venta.getCliente().getId() == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        
        Cliente cliente = clienteRepository.findById(venta.getCliente().getId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        
        if (!cliente.getActivo()) {
            throw new IllegalArgumentException("El cliente no está activo");
        }
        
        venta.setCliente(cliente);
        venta.setFechaVenta(LocalDateTime.now());
        venta.setEstado(EstadoVenta.PENDIENTE);
        
        // Procesar detalles de venta
        if (venta.getDetalles() != null && !venta.getDetalles().isEmpty()) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                procesarDetalleVenta(detalle, venta);
            }
        }
        
        venta.calcularTotales();
        return ventaRepository.save(venta);
    }
    
    // Obtener todas las ventas
    public List<Venta> obtenerTodasLasVentas() {
        return ventaRepository.findAll();
    }
    
    // Obtener ventas con paginación
    public Page<Venta> obtenerVentasPaginadas(Pageable pageable) {
        return ventaRepository.findByOrderByFechaVentaDesc(pageable);
    }
    
    // Obtener venta por ID
    public Optional<Venta> obtenerVentaPorId(Long id) {
        return ventaRepository.findById(id);
    }
    
    // Actualizar venta
    public Venta actualizarVenta(Long id, Venta ventaActualizada) {
        Venta ventaExistente = ventaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));
        
        // Solo permitir actualizar si está en estado PENDIENTE
        if (ventaExistente.getEstado() != EstadoVenta.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden modificar ventas en estado PENDIENTE");
        }
        
        // Restaurar stock de productos si existen detalles anteriores
        if (ventaExistente.getDetalles() != null) {
            for (DetalleVenta detalle : ventaExistente.getDetalles()) {
                Producto producto = detalle.getProducto();
                producto.aumentarStock(detalle.getCantidad());
                productoRepository.save(producto);
            }
        }
        
        // Actualizar campos permitidos
        ventaExistente.setMetodoPago(ventaActualizada.getMetodoPago());
        ventaExistente.setObservaciones(ventaActualizada.getObservaciones());
        
        // Procesar nuevos detalles
        ventaExistente.getDetalles().clear();
        if (ventaActualizada.getDetalles() != null) {
            for (DetalleVenta detalle : ventaActualizada.getDetalles()) {
                procesarDetalleVenta(detalle, ventaExistente);
                ventaExistente.agregarDetalle(detalle);
            }
        }
        
        ventaExistente.calcularTotales();
        return ventaRepository.save(ventaExistente);
    }
    
    // Cambiar estado de venta
    public Venta cambiarEstadoVenta(Long id, EstadoVenta nuevoEstado) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));
        
        EstadoVenta estadoAnterior = venta.getEstado();
        
        // Validar transiciones de estado
        if (!esTransicionValida(estadoAnterior, nuevoEstado)) {
            throw new IllegalStateException("Transición de estado no válida");
        }
        
        // Si se cancela la venta, restaurar stock
        if (nuevoEstado == EstadoVenta.CANCELADA && estadoAnterior != EstadoVenta.CANCELADA) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                Producto producto = detalle.getProducto();
                producto.aumentarStock(detalle.getCantidad());
                productoRepository.save(producto);
            }
        }
        
        venta.setEstado(nuevoEstado);
        return ventaRepository.save(venta);
    }
    
    // Eliminar venta (solo si está en estado PENDIENTE)
    public void eliminarVenta(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));
        
        if (venta.getEstado() != EstadoVenta.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden eliminar ventas en estado PENDIENTE");
        }
        
        // Restaurar stock de productos
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.aumentarStock(detalle.getCantidad());
            productoRepository.save(producto);
        }
        
        ventaRepository.delete(venta);
    }
    
    // Obtener ventas por cliente
    public List<Venta> obtenerVentasPorCliente(Long clienteId) {
        return ventaRepository.findByClienteId(clienteId);
    }
    
    // Obtener ventas por estado
    public List<Venta> obtenerVentasPorEstado(EstadoVenta estado) {
        return ventaRepository.findByEstado(estado);
    }
    
    // Obtener ventas del día
    public List<Venta> obtenerVentasDelDia() {
        return ventaRepository.findVentasDelDia();
    }
    
    // Obtener ventas por rango de fechas
    public List<Venta> obtenerVentasPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ventaRepository.findByFechaVentaBetween(fechaInicio, fechaFin);
    }
    
    // Métodos privados auxiliares
    private void procesarDetalleVenta(DetalleVenta detalle, Venta venta) {
        // Validar producto
        if (detalle.getProducto() == null || detalle.getProducto().getId() == null) {
            throw new IllegalArgumentException("El producto es obligatorio");
        }
        
        Producto producto = productoRepository.findById(detalle.getProducto().getId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        
        if (!producto.getActivo()) {
            throw new IllegalArgumentException("El producto no está activo");
        }
        
        // Verificar stock disponible
        if (producto.getStock() < detalle.getCantidad()) {
            throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre());
        }
        
        // Configurar detalle
        detalle.setProducto(producto);
        detalle.setVenta(venta);
        detalle.setPrecioUnitario(producto.getPrecio());
        
        // Reducir stock del producto
        producto.reducirStock(detalle.getCantidad());
        productoRepository.save(producto);
    }
    
    private boolean esTransicionValida(EstadoVenta estadoActual, EstadoVenta nuevoEstado) {
        switch (estadoActual) {
            case PENDIENTE:
                return nuevoEstado == EstadoVenta.CONFIRMADA || nuevoEstado == EstadoVenta.CANCELADA;
            case CONFIRMADA:
                return nuevoEstado == EstadoVenta.ENTREGADA || nuevoEstado == EstadoVenta.CANCELADA;
            case ENTREGADA:
                return false; // No se puede cambiar desde ENTREGADA
            case CANCELADA:
                return false; // No se puede cambiar desde CANCELADA
            default:
                return false;
        }
    }
}