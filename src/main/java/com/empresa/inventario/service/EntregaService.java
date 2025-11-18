package com.empresa.inventario.service;

import java.util.Date;
import java.util.List;

import com.empresa.inventario.dao.EntregaDAO;
import com.empresa.inventario.model.Entrega;

/**
 * Servicio que contiene la lógica de negocio para la gestión de entregas
 */
public class EntregaService {
    
    private final EntregaDAO entregaDAO;
    
    public EntregaService() {
        this.entregaDAO = new EntregaDAO();
    }
    
    /**
     * Programa una nueva entrega
     */
    public boolean programarEntrega(Entrega entrega) {
        // Validaciones
        if (entrega == null) {
            System.out.println("Error: Entrega no puede ser nula");
            return false;
        }
        
        if (entrega.getPedido() == null || entrega.getPedido().getIdPedido() <= 0) {
            System.out.println("Error: Debe especificar un pedido válido");
            return false;
        }
        
        if (entrega.getFechaEntrega() == null) {
            System.out.println("Error: Debe especificar una fecha de entrega");
            return false;
        }
        
        // Validar que la fecha no sea pasada
        if (entrega.getFechaEntrega().before(new Date())) {
            System.out.println("Error: La fecha de entrega no puede ser en el pasado");
            return false;
        }
        
        return entregaDAO.insertar(entrega);
    }
    
    /**
     * Actualiza el estado de una entrega
     */
    public boolean actualizarEstadoEntrega(int idEntrega, String nuevoEstado) {
        String[] estadosValidos = {"Pendiente", "En Camino", "Entregado", "Cancelado"};
        boolean valido = false;
        
        for (String estado : estadosValidos) {
            if (estado.equalsIgnoreCase(nuevoEstado)) {
                valido = true;
                break;
            }
        }
        
        if (!valido) {
            System.out.println("Error: Estado de entrega no válido");
            return false;
        }
        
        return entregaDAO.actualizarEstado(idEntrega, nuevoEstado);
    }
    
    /**
     * Lista todas las entregas
     */
    public List<Entrega> listarEntregas() {
        return entregaDAO.listar();
    }
}