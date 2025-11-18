package com.empresa.inventario.controller;

import com.empresa.inventario.service.EntregaService;
import com.empresa.inventario.model.Entrega;
import com.empresa.inventario.model.Pedido;
import java.util.Date;
import java.util.List;

/**
 * Controlador para gestionar las operaciones de entregas
 */
public class EntregaController {
    
    private final EntregaService entregaService;
    
    public EntregaController() {
        this.entregaService = new EntregaService();
    }
    
    /**
     * Programa una nueva entrega
     * @param idPedido ID del pedido
     * @param fechaEntrega Fecha programada de entrega
     * @return true si se programó correctamente
     */
    public boolean programarEntrega(int idPedido, Date fechaEntrega) {
        // Validaciones
        if (idPedido <= 0) {
            mostrarError("ID de pedido inválido");
            return false;
        }
        
        if (fechaEntrega == null) {
            mostrarError("La fecha de entrega es obligatoria");
            return false;
        }
        
        // Validar que la fecha no sea pasada
        if (fechaEntrega.before(new Date())) {
            mostrarError("La fecha de entrega no puede ser en el pasado");
            return false;
        }
        
        // Crear el objeto Entrega
        Pedido pedido = new Pedido();
        pedido.setIdPedido(idPedido);
        
        Entrega entrega = new Entrega();
        entrega.setPedido(pedido);
        entrega.setFechaEntrega(fechaEntrega);
        entrega.setEstado("Pendiente");
        
        // Programar la entrega
        boolean resultado = entregaService.programarEntrega(entrega);
        
        if (resultado) {
            mostrarMensaje("Entrega programada exitosamente");
            mostrarMensaje("Fecha de entrega: " + fechaEntrega);
        } else {
            mostrarError("Error al programar la entrega");
        }
        
        return resultado;
    }
    
    /**
     * Actualiza el estado de una entrega
     * @param idEntrega ID de la entrega
     * @param nuevoEstado Nuevo estado (Pendiente, En Camino, Entregado, Cancelado)
     */
    public boolean actualizarEstadoEntrega(int idEntrega, String nuevoEstado) {
        if (idEntrega <= 0) {
            mostrarError("ID de entrega inválido");
            return false;
        }
        
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            mostrarError("El estado es obligatorio");
            return false;
        }
        
        boolean resultado = entregaService.actualizarEstadoEntrega(idEntrega, nuevoEstado);
        
        if (resultado) {
            mostrarMensaje("Estado de entrega actualizado a: " + nuevoEstado);
        } else {
            mostrarError("Error al actualizar el estado de la entrega");
        }
        
        return resultado;
    }
    
    /**
     * Lista todas las entregas
     */
    public List<Entrega> listarEntregas() {
        List<Entrega> entregas = entregaService.listarEntregas();
        
        if (entregas.isEmpty()) {
            mostrarMensaje("No hay entregas programadas");
        } else {
            mostrarMensaje("Se encontraron " + entregas.size() + " entregas");
        }
        
        return entregas;
    }
    
    /**
     * Obtiene información formateada de una entrega
     */
    public String obtenerInformacionEntrega(int idEntrega) {
        List<Entrega> entregas = listarEntregas();
        Entrega entrega = entregas.stream()
            .filter(e -> e.getIdEntrega() == idEntrega)
            .findFirst()
            .orElse(null);
        
        if (entrega == null) {
            return "Entrega no encontrada";
        }
        
        return String.format(
            "=== ENTREGA #%d ===\n" +
            "Fecha: %s\n" +
            "Estado: %s",
            entrega.getIdEntrega(),
            entrega.getFechaEntrega(),
            entrega.getEstado()
        );
    }
    
    /**
     * Marca una entrega como completada
     */
    public boolean completarEntrega(int idEntrega) {
        return actualizarEstadoEntrega(idEntrega, "Entregado");
    }
    
    /**
     * Cancela una entrega
     */
    public boolean cancelarEntrega(int idEntrega) {
        return actualizarEstadoEntrega(idEntrega, "Cancelado");
    }
    
    // Métodos auxiliares
    private void mostrarMensaje(String mensaje) {
        System.out.println("✓ " + mensaje);
    }
    
    private void mostrarError(String mensaje) {
        System.err.println("✗ " + mensaje);
    }
}