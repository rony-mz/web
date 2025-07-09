package com.cevicheria.sistemaventas.config;

import com.cevicheria.sistemaventas.entity.Cliente;
import com.cevicheria.sistemaventas.entity.Producto;
import com.cevicheria.sistemaventas.repository.ClienteRepository;
import com.cevicheria.sistemaventas.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Crear clientes de ejemplo
        if (clienteRepository.count() == 0) {
            crearClientesEjemplo();
        }
        
        // Crear productos de ejemplo
        if (productoRepository.count() == 0) {
            crearProductosEjemplo();
        }
    }
    
    private void crearClientesEjemplo() {
        Cliente cliente1 = new Cliente("Juan", "Pérez", "987654321", "juan.perez@email.com", "Av. Lima 123");
        Cliente cliente2 = new Cliente("María", "García", "987654322", "maria.garcia@email.com", "Jr. Cusco 456");
        Cliente cliente3 = new Cliente("Carlos", "López", "987654323", "carlos.lopez@email.com", "Av. Arequipa 789");
        Cliente cliente4 = new Cliente("Ana", "Martínez", "987654324", "ana.martinez@email.com", "Jr. Tacna 321");
        
        clienteRepository.save(cliente1);
        clienteRepository.save(cliente2);
        clienteRepository.save(cliente3);
        clienteRepository.save(cliente4);
        
        System.out.println("✓ Clientes de ejemplo creados");
    }
    
    private void crearProductosEjemplo() {
        // Productos de cevichería
        Producto ceviche = new Producto("Ceviche de Pescado", "Ceviche fresco con pescado del día", 
                new BigDecimal("25.00"), 50, "Porción");
        
        Producto tiradito = new Producto("Tiradito de Pescado", "Tiradito con ají amarillo", 
                new BigDecimal("22.00"), 30, "Porción");
        
        Producto arroz = new Producto("Arroz con Mariscos", "Arroz con mariscos variados", 
                new BigDecimal("28.00"), 25, "Porción");
        
        Producto sudado = new Producto("Sudado de Pescado", "Sudado de pescado con yuca", 
                new BigDecimal("24.00"), 20, "Porción");
        
        Producto leche = new Producto("Leche de Tigre", "Leche de tigre pura", 
                new BigDecimal("8.00"), 40, "Vaso");
        
        Producto chicha = new Producto("Chicha Morada", "Chicha morada tradicional", 
                new BigDecimal("5.00"), 100, "Vaso");
        
        Producto inca = new Producto("Inca Kola", "Gaseosa Inca Kola", 
                new BigDecimal("4.00"), 80, "Botella");
        
        Producto agua = new Producto("Agua Mineral", "Agua mineral 500ml", 
                new BigDecimal("2.50"), 120, "Botella");
        
        productoRepository.save(ceviche);
        productoRepository.save(tiradito);
        productoRepository.save(arroz);
        productoRepository.save(sudado);
        productoRepository.save(leche);
        productoRepository.save(chicha);
        productoRepository.save(inca);
        productoRepository.save(agua);
        
        System.out.println("✓ Productos de ejemplo creados");
    }
}