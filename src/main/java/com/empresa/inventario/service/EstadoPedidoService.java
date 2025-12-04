package com.empresa.inventario.service;

import com.empresa.inventario.dao.*;
import com.empresa.inventario.model.*;
import java.util.List;
import java.util.Date;

/**
 * Servicio que contiene la lógica de negocio para gestión de estados
 */
public class EstadoPedidoService {
    
    private final PedidoDAO pedidoDAO;
    private final DetallePedidoDAO detallePedidoDAO;
    private final ProductoDAO productoDAO;
    private final HistorialEstadoDAO historialDAO;
    private final AnulacionDAO anulacionDAO;
    private final DeudaDAO deudaDAO;
    private final EntregaDAO entregaDAO;
    
    public EstadoPedidoService() {
        this.pedidoDAO = new PedidoDAO();
        this.detallePedidoDAO = new DetallePedidoDAO();
        this.productoDAO = new ProductoDAO();
        this.historialDAO = new HistorialEstadoDAO();
        this.anulacionDAO = new AnulacionDAO();
        this.deudaDAO = new DeudaDAO();
        this.entregaDAO = new EntregaDAO();
    }
    
    /**
     * Cambia el estado de un pedido
     */
    public boolean cambiarEstado(int idPedido, String nuevoEstado, int idEmpleado, String observaciones) {
        // Validar que el pedido existe
        Pedido pedido = pedidoDAO.obtenerPorId(idPedido);
        if (pedido == null) {
            System.err.println("Pedido no encontrado: " + idPedido);
            return false;
        }
        
        // Validar transición de estado
        if (!validarTransicionEstado(pedido.getEstadoPedido(), nuevoEstado)) {
            System.err.println("Transición de estado inválida: " + 
                pedido.getEstadoPedido() + " -> " + nuevoEstado);
            return false;
        }
        
        // Actualizar estado en la BD
        boolean resultado = pedidoDAO.actualizarEstado(idPedido, nuevoEstado);
        
        if (resultado) {
            // Registrar en historial (el trigger lo hace automáticamente)
            System.out.println("✓ Estado actualizado: " + nuevoEstado);
        }
        
        return resultado;
    }
    
    /**
     * Rechaza un pedido con motivo
     */
    public boolean rechazarPedido(int idPedido, String estadoRechazo, int idEmpleado, String motivo) {
        boolean resultado = pedidoDAO.rechazarPedido(idPedido, estadoRechazo, motivo);
        
        if (resultado) {
            // Registrar en historial
            historialDAO.registrar(idPedido, null, estadoRechazo, idEmpleado, 
                "RECHAZADO: " + motivo);
        }
        
        return resultado;
    }
    
    /**
     * Anula un pedido
     */
    public boolean anularPedido(int idPedido, int idEmpleado, String motivoAnulacion) {
        // Cambiar estado a Anulado
        boolean resultado = cambiarEstado(idPedido, "Anulado", idEmpleado, motivoAnulacion);
        
        if (resultado) {
            // Registrar en tabla de anulaciones
            anulacionDAO.registrarAnulacion(
                idPedido,
                motivoAnulacion,
                "ClienteNoDisponible", // Por defecto
                idEmpleado
            );
            
            // Cambiar a estado de evaluación
            cambiarEstado(idPedido, "EnEvaluacion", idEmpleado, 
                "Automático: Enviado a logística para evaluación");
        }
        
        return resultado;
    }
    
    /**
     * Evalúa una anulación (logística)
     */
    public boolean evaluarAnulacion(int idPedido, int idEmpleado, String observaciones, boolean aprobar) {
        // Actualizar registro de anulación
        boolean resultado = anulacionDAO.evaluarAnulacion(
            idPedido,
            idEmpleado,
            observaciones,
            aprobar
        );
        
        if (resultado && aprobar) {
            // Si se aprueba, enviar a almacén para restaurar stock
            cambiarEstado(idPedido, "EnRestauracion", idEmpleado, 
                "Anulación aprobada. Restaurando stock.");
        }
        
        return resultado;
    }
    
    /**
     * Restaura el stock de un pedido anulado
     */
    public boolean restaurarStockAnulado(int idPedido, int idEmpleado) {
        // Obtener detalles del pedido
        List<DetallePedido> detalles = detallePedidoDAO.listarPorPedido(idPedido);
        
        if (detalles == null || detalles.isEmpty()) {
            System.err.println("No hay detalles para restaurar");
            return false;
        }
        
        // Restaurar stock de cada producto
        for (DetallePedido detalle : detalles) {
            Producto producto = productoDAO.obtenerPorId(detalle.getProducto().getIdProducto());
            if (producto != null) {
                int nuevoStock = producto.getStock() + detalle.getCantidad();
                producto.setStock(nuevoStock);
                productoDAO.actualizar(producto);
                
                System.out.println("✓ Stock restaurado: " + producto.getNombre() + 
                    " +" + detalle.getCantidad());
            }
        }
        
        // Marcar anulación como completada
        anulacionDAO.completarRestauracion(idPedido);
        
        return true;
    }
    
    /**
     * Asigna un repartidor a un pedido
     */
    public boolean asignarRepartidor(int idPedido, int idRepartidor, int idEmpleado) {
        boolean resultado = entregaDAO.asignarRepartidor(idPedido, idRepartidor);
        
        if (resultado) {
            cambiarEstado(idPedido, "EnCamino", idEmpleado, 
                "Repartidor asignado. ID: " + idRepartidor);
        }
        
        return resultado;
    }
    
    /**
     * Registra un intento de entrega
     */
    public boolean registrarIntentoEntrega(int idPedido, int idEmpleado, String observaciones) {
        // Incrementar contador de intentos
        int intentos = pedidoDAO.incrementarIntentosContacto(idPedido);
        
        // Registrar en historial
        historialDAO.registrar(
            idPedido,
            "IntentandoEntrega",
            "IntentandoEntrega",
            idEmpleado,
            "Intento " + intentos + "/3: " + observaciones
        );
        
        // Actualizar estado de entrega
        entregaDAO.actualizarEstado(idPedido, "IntentandoContacto" + intentos);
        
        return true;
    }
    
    /**
     * Obtiene el número de intentos de contacto
     */
    public int obtenerIntentosContacto(int idPedido) {
        return pedidoDAO.obtenerIntentosContacto(idPedido);
    }
    
    /**
     * Confirma la entrega de un pedido
     */
    public boolean confirmarEntrega(int idPedido, int idEmpleado, double montoPagado, String metodoPago) {
        // Obtener pedido
        Pedido pedido = pedidoDAO.obtenerPorId(idPedido);
        if (pedido == null) {
            return false;
        }
        
        // Actualizar estado a Entregado
        boolean resultado = cambiarEstado(idPedido, "Entregado", idEmpleado, 
            "Pedido entregado exitosamente");
        
        if (resultado) {
            // Actualizar montos
            double montoTotal = pedido.getMontoTotal();
            double deuda = montoTotal - montoPagado;
            
            pedidoDAO.actualizarMontoPagado(idPedido, montoPagado, deuda);
            
            // Si hay deuda, registrarla
            if (deuda > 0) {
                deudaDAO.registrarDeuda(
                    pedido.getCliente().getIdCliente(),
                    idPedido,
                    deuda,
                    "Pago incompleto en entrega"
                );
            }
            
            // Actualizar estado de entrega
            entregaDAO.confirmarEntrega(idPedido, new Date());
        }
        
        return resultado;
    }
    
    /**
     * Registra un pago de deuda
     */
    public boolean registrarPagoDeuda(int idPedido, double monto, String metodoPago, int idEmpleado) {
        return deudaDAO.registrarPago(idPedido, monto, metodoPago);
    }
    
    /**
     * Lista pedidos por estado(s)
     */
    public List<Pedido> listarPedidosPorEstado(String... estados) {
        return pedidoDAO.listarPorEstados(estados);
    }
    
    /**
     * Lista pedidos asignados a un repartidor
     */
    public List<Pedido> listarPedidosPorRepartidor(int idRepartidor) {
        return pedidoDAO.listarPorRepartidor(idRepartidor);
    }
    
    /**
     * Lista pedidos con deuda pendiente
     */
    public List<Pedido> listarPedidosConDeuda() {
        return pedidoDAO.listarConDeuda();
    }
    
    /**
     * Obtiene un pedido por ID
     */
    public Pedido obtenerPedido(int idPedido) {
        return pedidoDAO.obtenerPorId(idPedido);
    }
    
    /**
     * Obtiene el historial de estados de un pedido
     */
    public List<HistorialEstadoPedido> obtenerHistorialEstados(int idPedido) {
        return historialDAO.listarPorPedido(idPedido);
    }
    
    /**
     * Genera un resumen de deudas de un cliente
     */
    public String generarResumenDeudas(int idCliente) {
        List<DeudaCliente> deudas = deudaDAO.listarPorCliente(idCliente);
        
        if (deudas == null || deudas.isEmpty()) {
            return "El cliente no tiene deudas pendientes";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== RESUMEN DE DEUDAS ===\n\n");
        
        double totalDeuda = 0;
        for (DeudaCliente deuda : deudas) {
            sb.append(String.format("Pedido #%d | Deuda: S/ %.2f | Estado: %s\n",
                deuda.getPedido().getIdPedido(),
                deuda.getMontoPendiente(),
                deuda.getEstadoDeuda()
            ));
            totalDeuda += deuda.getMontoPendiente();
        }
        
        sb.append(String.format("\nTOTAL DEUDA: S/ %.2f", totalDeuda));
        
        return sb.toString();
    }
    
    /**
     * Genera información detallada de un pedido
     */
    public String generarInformacionDetallada(int idPedido) {
        Pedido pedido = pedidoDAO.obtenerPorId(idPedido);
        if (pedido == null) {
            return "Pedido no encontrado";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== PEDIDO #").append(idPedido).append(" ===\n\n");
        sb.append("Cliente: ").append(pedido.getCliente().getRazonSocial()).append("\n");
        sb.append("Estado: ").append(pedido.getEstadoPedido()).append("\n");
        sb.append("Fecha: ").append(pedido.getFecha()).append("\n");
        sb.append("Monto Total: S/ ").append(String.format("%.2f", pedido.getMontoTotal())).append("\n");
        sb.append("Monto Pagado: S/ ").append(String.format("%.2f", pedido.getMontoPagado())).append("\n");
        sb.append("Deuda: S/ ").append(String.format("%.2f", pedido.getDeudaPendiente())).append("\n");
        sb.append("Intentos Contacto: ").append(pedido.getIntentosContacto()).append("/3\n");
        
        return sb.toString();
    }
    
    /**
     * Valida si una transición de estado es válida
     */
    private boolean validarTransicionEstado(String estadoActual, String nuevoEstado) {
        // Matriz de transiciones válidas
        // Esto es simplificado - en producción usar una tabla de permisos
        
        if (estadoActual == null || nuevoEstado == null) {
            return false;
        }
        
        // Los estados pueden progresar o regresar según lógica de negocio
        // Por ahora aceptamos cualquier transición registrada en el enum
        String[] estadosValidos = {
            "Registrado", "PendienteComercial", "AprobadoComercial", "RechazadoComercial",
            "PendienteAdministrativo", "AprobadoAdministrativo", "RechazadoAdministrativo",
            "EnAlmacen", "Alistado", "Empaquetado", "EnDistribucion", "EnCamino",
            "IntentandoEntrega", "Entregado", "Anulado", "EnEvaluacion", "EnRestauracion"
        };
        
        for (String estado : estadosValidos) {
            if (estado.equals(nuevoEstado)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Valida permisos de cambio de estado según rol del empleado
     */
    public boolean validarPermisosCambioEstado(int idEmpleado, String estadoActual, String nuevoEstado) {
        // TODO: Implementar validación de permisos según rol
        // Por ahora retorna true
        return true;
    }
}