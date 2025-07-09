package com.cevicheria.sistemaventas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "productos")
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    @Column(length = 255)
    private String descripcion;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    @NotNull(message = "El stock es obligatorio")
    @Column(nullable = false)
    private Integer stock = 0;
    
    @Size(max = 50, message = "La unidad de medida no puede exceder 50 caracteres")
    @Column(name = "unidad_medida", length = 50)
    private String unidadMedida;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<DetalleVenta> detallesVenta;
    
    // Constructores
    public Producto() {}
    
    public Producto(String nombre, String descripcion, BigDecimal precio, Integer stock, String unidadMedida) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.unidadMedida = unidadMedida;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public BigDecimal getPrecio() {
        return precio;
    }
    
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    
    public Integer getStock() {
        return stock;
    }
    
    public void setStock(Integer stock) {
        this.stock = stock;
    }
    
    public String getUnidadMedida() {
        return unidadMedida;
    }
    
    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    public List<DetalleVenta> getDetallesVenta() {
        return detallesVenta;
    }
    
    public void setDetallesVenta(List<DetalleVenta> detallesVenta) {
        this.detallesVenta = detallesVenta;
    }
    
    public void reducirStock(Integer cantidad) {
        if (this.stock >= cantidad) {
            this.stock -= cantidad;
        } else {
            throw new IllegalArgumentException("Stock insuficiente");
        }
    }
    
    public void aumentarStock(Integer cantidad) {
        this.stock += cantidad;
    }
}