package com.empresa.inventario.controller;

import com.empresa.inventario.service.FacturaService;
import com.empresa.inventario.model.Factura;

/**
 * Controlador para gestionar las operaciones de facturas
 */
public class FacturaController {
    
    private final FacturaService facturaService;
    
    public FacturaController() {
        this.facturaService = new FacturaService();
    }
    
    /**
     * Busca una factura por el ID del pedido
     */
    public Factura buscarFacturaPorPedido(int idPedido) {
        if (idPedido <= 0) {
            mostrarError("ID de pedido inválido");
            return null;
        }
        
        Factura factura = facturaService.buscarFacturaPorPedido(idPedido);
        
        if (factura == null) {
            mostrarError("No se encontró factura para el pedido: " + idPedido);
        } else {
            mostrarMensaje("Factura encontrada: ID " + factura.getIdFactura());
        }
        
        return factura;
    }
    
    /**
     * Obtiene información formateada de una factura
     */
    public String obtenerInformacionFactura(int idPedido) {
        Factura factura = buscarFacturaPorPedido(idPedido);
        
        if (factura == null) {
            return "Factura no encontrada para el pedido: " + idPedido;
        }
        
        return String.format(
            "=== FACTURA #%d ===\n" +
            "Pedido ID: %d\n" +
            "Total: S/ %.2f\n" +
            "Estado: Generada",
            factura.getIdFactura(),
            idPedido,
            factura.getTotal()
        );
    }
    
    // Métodos auxiliares
    private void mostrarMensaje(String mensaje) {
        System.out.println("✓ " + mensaje);
    }
    
    private void mostrarError(String mensaje) {
        System.err.println("✗ " + mensaje);
    }
}