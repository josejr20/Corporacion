package com.empresa.inventario.service;

import com.empresa.inventario.dao.FacturaDAO;
import com.empresa.inventario.model.Factura;

/**
 * Servicio que contiene la lógica de negocio para la gestión de facturas
 */
public class FacturaService {
    
    private final FacturaDAO facturaDAO;
    
    public FacturaService() {
        this.facturaDAO = new FacturaDAO();
    }
    
    /**
     * Busca una factura por el ID del pedido
     */
    public Factura buscarFacturaPorPedido(int idPedido) {
        if (idPedido <= 0) {
            System.out.println("Error: ID de pedido inválido");
            return null;
        }
        
        return facturaDAO.buscarPorPedido(idPedido);
    }
}