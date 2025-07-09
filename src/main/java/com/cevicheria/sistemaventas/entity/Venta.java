package com.cevicheria.sistemaventas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventas")
public class Venta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "El cliente es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @Column(name = "fecha_venta", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaVenta;
    
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;
    
    @Column(name = "igv", nullable = false, precision = 10, scale = 2)
    private BigDecimal igv = BigDecimal.ZERO;
    
    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoVenta estado = EstadoVenta.PENDIENTE;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", length = 20)
    private MetodoPago metodoPago;
    
    @Column(length = 255)
    private String observaciones;
    
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleVenta> detalles = new ArrayList<>();
    
    // Constructores
    public Venta() {
        this.fechaVenta = LocalDateTime.now();
    }
    
    public Venta(Cliente cliente, MetodoPago metodoPago) {
        this();
        this.cliente = cliente;
        this.metodoPago = metodoPago;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }
    
    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public BigDecimal getIgv() {
        return igv;
    }
    
    public void setIgv(BigDecimal igv) {
        this.igv = igv;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public EstadoVenta getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoVenta estado) {
        this.estado = estado;
    }
    
    public MetodoPago getMetodoPago() {
        return metodoPago;
    }
    
    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public List<DetalleVenta> getDetalles() {
        return detalles;
    }
    
    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }
    
    // Métodos de negocio
    public void agregarDetalle(DetalleVenta detalle) {
        this.detalles.add(detalle);
        detalle.setVenta(this);
        calcularTotales();
    }
    
    public void removerDetalle(DetalleVenta detalle) {
        this.detalles.remove(detalle);
        calcularTotales();
    }
    
    public void calcularTotales() {
        this.subtotal = this.detalles.stream()
                .map(DetalleVenta::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // IGV del 18%
        this.igv = this.subtotal.multiply(new BigDecimal("0.18"));
        this.total = this.subtotal.add(this.igv);
    }
    
    public enum EstadoVenta {
        PENDIENTE, CONFIRMADA, ENTREGADA, CANCELADA
    }
    
    public enum MetodoPago {
        EFECTIVO, TARJETA, TRANSFERENCIA, YAPE, PLIN
    }
}