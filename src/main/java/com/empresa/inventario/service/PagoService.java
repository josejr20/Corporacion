package com.empresa.inventario.service;

import java.util.List;

import com.empresa.inventario.dao.PagoDAO;
import com.empresa.inventario.model.Pago;

/**
 * Servicio que contiene la lógica de negocio para la gestión de pagos
 */
public class PagoService {
    
    private final PagoDAO pagoDAO;
    
    public PagoService() {
        this.pagoDAO = new PagoDAO();
    }
    
    /**
     * Registra un nuevo pago
     */
    public boolean registrarPago(Pago pago) {
        // Validaciones
        if (pago == null) {
            System.out.println("Error: Pago no puede ser nulo");
            return false;
        }
        
        if (pago.getFactura() == null || pago.getFactura().getIdFactura() <= 0) {
            System.out.println("Error: Debe especificar una factura válida");
            return false;
        }
        
        if (pago.getMonto() <= 0) {
            System.out.println("Error: El monto debe ser mayor a 0");
            return false;
        }
        
        String[] metodosValidos = {"Efectivo", "Transferencia", "Tarjeta", "Yape", "Plin"};
        boolean metodoValido = false;
        
        for (String metodo : metodosValidos) {
            if (metodo.equalsIgnoreCase(pago.getMetodoPago())) {
                metodoValido = true;
                break;
            }
        }
        
        if (!metodoValido) {
            System.out.println("Error: Método de pago no válido");
            return false;
        }
        
        return pagoDAO.registrarPago(pago);
    }
    
    /**
     * Lista los pagos de una factura
     */
    public List<Pago> listarPagosPorFactura(int idFactura) {
        if (idFactura <= 0) {
            return null;
        }
        return pagoDAO.listarPorFactura(idFactura);
    }
}