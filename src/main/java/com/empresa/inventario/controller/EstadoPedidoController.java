package com.empresa.inventario.controller;

import com.empresa.inventario.service.EstadoPedidoService;
import com.empresa.inventario.model.*;
import java.util.List;

/**
 * Controlador para gestionar el cambio de estados de pedidos
 * según el flujo del negocio
 */
public class EstadoPedidoController {
    
    private final EstadoPedidoService estadoPedidoService;
    
    public EstadoPedidoController() {
        this.estadoPedidoService = new EstadoPedidoService();
    }
    
    // ========================================
    // ÁREA COMERCIAL
    // ========================================
    
    /**
     * Obtiene pedidos pendientes de validación comercial
     */
    public List<Pedido> obtenerPedidosPendientesComercial() {
        return estadoPedidoService.listarPedidosPorEstado(
            "Registrado", "PendienteComercial"
        );
    }
    
    /**
     * Aprobar pedido por área comercial
     */
    public boolean aprobarPedidoComercial(int idPedido, int idEmpleado, String observaciones) {
        if (idPedido <= 0 || idEmpleado <= 0) {
            mostrarError("Datos inválidos");
            return false;
        }
        
        boolean resultado = estadoPedidoService.cambiarEstado(
            idPedido,
            "AprobadoComercial",
            idEmpleado,
            observaciones
        );
        
        if (resultado) {
            mostrarMensaje("✅ Pedido aprobado. Enviado a área administrativa.");
            // Cambiar a PendienteAdministrativo
            estadoPedidoService.cambiarEstado(
                idPedido, 
                "PendienteAdministrativo", 
                idEmpleado, 
                "Automático: Aprobado por comercial"
            );
        } else {
            mostrarError("Error al aprobar pedido");
        }
        
        return resultado;
    }
    
    /**
     * Rechazar pedido por área comercial
     */
    public boolean rechazarPedidoComercial(int idPedido, int idEmpleado, String motivoRechazo) {
        if (motivoRechazo == null || motivoRechazo.trim().isEmpty()) {
            mostrarError("Debe especificar el motivo del rechazo");
            return false;
        }
        
        boolean resultado = estadoPedidoService.rechazarPedido(
            idPedido,
            "RechazadoComercial",
            idEmpleado,
            motivoRechazo
        );
        
        if (resultado) {
            mostrarMensaje("❌ Pedido rechazado. Cliente será notificado.");
        } else {
            mostrarError("Error al rechazar pedido");
        }
        
        return resultado;
    }
    
    // ========================================
    // ÁREA ADMINISTRATIVA
    // ========================================
    
    /**
     * Obtiene pedidos pendientes de validación administrativa
     */
    public List<Pedido> obtenerPedidosPendientesAdministrativo() {
        return estadoPedidoService.listarPedidosPorEstado(
            "PendienteAdministrativo", "AprobadoComercial"
        );
    }
    
    /**
     * Aprobar pedido por área administrativa
     */
    public boolean aprobarPedidoAdministrativo(int idPedido, int idEmpleado, String observaciones) {
        boolean resultado = estadoPedidoService.cambiarEstado(
            idPedido,
            "AprobadoAdministrativo",
            idEmpleado,
            observaciones
        );
        
        if (resultado) {
            mostrarMensaje("✅ Pedido aprobado. Enviado a almacén.");
            // Cambiar a EnAlmacen
            estadoPedidoService.cambiarEstado(
                idPedido, 
                "EnAlmacen", 
                idEmpleado, 
                "Automático: Aprobado por administrativa"
            );
        } else {
            mostrarError("Error al aprobar pedido");
        }
        
        return resultado;
    }
    
    /**
     * Rechazar pedido por área administrativa
     */
    public boolean rechazarPedidoAdministrativo(int idPedido, int idEmpleado, String motivoRechazo) {
        if (motivoRechazo == null || motivoRechazo.trim().isEmpty()) {
            mostrarError("Debe especificar el motivo del rechazo");
            return false;
        }
        
        boolean resultado = estadoPedidoService.rechazarPedido(
            idPedido,
            "RechazadoAdministrativo",
            idEmpleado,
            motivoRechazo
        );
        
        if (resultado) {
            mostrarMensaje("❌ Pedido rechazado administrativamente.");
        } else {
            mostrarError("Error al rechazar pedido");
        }
        
        return resultado;
    }
    
    // ========================================
    // ÁREA ALMACÉN
    // ========================================
    
    /**
     * Obtiene pedidos en almacén para alistar
     */
    public List<Pedido> obtenerPedidosEnAlmacen() {
        return estadoPedidoService.listarPedidosPorEstado(
            "AprobadoAdministrativo", "EnAlmacen"
        );
    }
    
    /**
     * Marcar pedido como alistado
     */
    public boolean marcarPedidoAlistado(int idPedido, int idEmpleado) {
        boolean resultado = estadoPedidoService.cambiarEstado(
            idPedido,
            "Alistado",
            idEmpleado,
            "Productos alistados correctamente"
        );
        
        if (resultado) {
            mostrarMensaje("📦 Pedido alistado. Listo para empaquetar.");
        }
        
        return resultado;
    }
    
    /**
     * Marcar pedido como empaquetado
     */
    public boolean marcarPedidoEmpaquetado(int idPedido, int idEmpleado) {
        boolean resultado = estadoPedidoService.cambiarEstado(
            idPedido,
            "Empaquetado",
            idEmpleado,
            "Pedido empaquetado y sellado"
        );
        
        if (resultado) {
            mostrarMensaje("📦 Pedido empaquetado. Listo para distribución.");
            // Cambiar a EnDistribucion
            estadoPedidoService.cambiarEstado(
                idPedido, 
                "EnDistribucion", 
                idEmpleado, 
                "Automático: Transferido a distribución"
            );
        }
        
        return resultado;
    }
    
    /**
     * Restaurar stock de pedido anulado
     */
    public boolean restaurarStockPedidoAnulado(int idPedido, int idEmpleado) {
        boolean resultado = estadoPedidoService.restaurarStockAnulado(
            idPedido,
            idEmpleado
        );
        
        if (resultado) {
            mostrarMensaje("♻️ Stock restaurado correctamente");
        } else {
            mostrarError("Error al restaurar stock");
        }
        
        return resultado;
    }
    
    // ========================================
    // ÁREA LOGÍSTICA
    // ========================================
    
    /**
     * Obtiene pedidos listos para asignar repartidor
     */
    public List<Pedido> obtenerPedidosParaDistribucion() {
        return estadoPedidoService.listarPedidosPorEstado(
            "EnDistribucion", "Empaquetado"
        );
    }
    
    /**
     * Asignar repartidor a pedido
     */
    public boolean asignarRepartidor(int idPedido, int idRepartidor, int idEmpleado) {
        if (idRepartidor <= 0) {
            mostrarError("Debe seleccionar un repartidor");
            return false;
        }
        
        boolean resultado = estadoPedidoService.asignarRepartidor(
            idPedido,
            idRepartidor,
            idEmpleado
        );
        
        if (resultado) {
            mostrarMensaje("🚚 Repartidor asignado exitosamente");
        } else {
            mostrarError("Error al asignar repartidor");
        }
        
        return resultado;
    }
    
    /**
     * Evaluar motivo de anulación
     */
    public boolean evaluarAnulacion(int idPedido, int idEmpleado, String observaciones, boolean aprobar) {
        boolean resultado = estadoPedidoService.evaluarAnulacion(
            idPedido,
            idEmpleado,
            observaciones,
            aprobar
        );
        
        if (resultado) {
            if (aprobar) {
                mostrarMensaje("✅ Anulación aprobada. Enviado a almacén para restaurar stock.");
            } else {
                mostrarMensaje("❌ Anulación rechazada. Se reintentará entrega.");
            }
        } else {
            mostrarError("Error al evaluar anulación");
        }
        
        return resultado;
    }
    
    // ========================================
    // ÁREA DISTRIBUCIÓN
    // ========================================
    
    /**
     * Obtiene pedidos asignados para entregar
     */
    public List<Pedido> obtenerPedidosParaEntregar(int idRepartidor) {
        return estadoPedidoService.listarPedidosPorRepartidor(idRepartidor);
    }
    
    /**
     * Iniciar ruta de entrega
     */
    public boolean iniciarRutaEntrega(int idPedido, int idEmpleado) {
        boolean resultado = estadoPedidoService.cambiarEstado(
            idPedido,
            "EnCamino",
            idEmpleado,
            "Repartidor en camino al destino"
        );
        
        if (resultado) {
            mostrarMensaje("🚗 Ruta de entrega iniciada");
        }
        
        return resultado;
    }
    
    /**
     * Registrar intento de entrega
     */
    public boolean registrarIntentoEntrega(int idPedido, int idEmpleado, String observaciones) {
        // Obtener número de intentos actuales
        int intentosActuales = estadoPedidoService.obtenerIntentosContacto(idPedido);
        
        if (intentosActuales >= 3) {
            mostrarError("⚠️ Se alcanzó el límite de 3 intentos. El pedido será anulado.");
            return anularPedidoDistribucion(
                idPedido, 
                idEmpleado, 
                "Cliente no disponible después de 3 intentos"
            );
        }
        
        boolean resultado = estadoPedidoService.registrarIntentoEntrega(
            idPedido,
            idEmpleado,
            observaciones
        );
        
        if (resultado) {
            intentosActuales++;
            mostrarMensaje(String.format(
                "📞 Intento %d/3 registrado. %s",
                intentosActuales,
                intentosActuales < 3 ? "Se reintentará contacto." : "⚠️ ÚLTIMO INTENTO REALIZADO"
            ));
        } else {
            mostrarError("Error al registrar intento");
        }
        
        return resultado;
    }
    
    /**
     * Confirmar entrega exitosa
     */
    public boolean confirmarEntrega(int idPedido, int idEmpleado, double montoPagado, String metodoPago) {
        if (montoPagado <= 0) {
            mostrarError("El monto pagado debe ser mayor a 0");
            return false;
        }
        
        boolean resultado = estadoPedidoService.confirmarEntrega(
            idPedido,
            idEmpleado,
            montoPagado,
            metodoPago
        );
        
        if (resultado) {
            // Verificar si el pago fue completo
            Pedido pedido = estadoPedidoService.obtenerPedido(idPedido);
            if (pedido != null) {
                double montoTotal = pedido.getMontoTotal();
                if (montoPagado < montoTotal) {
                    mostrarMensaje(String.format(
                        "✅ Pedido entregado con PAGO PARCIAL\n" +
                        "Pagado: S/ %.2f de S/ %.2f\n" +
                        "Deuda registrada: S/ %.2f",
                        montoPagado,
                        montoTotal,
                        montoTotal - montoPagado
                    ));
                } else {
                    mostrarMensaje("✅ Pedido entregado y PAGADO COMPLETAMENTE");
                }
            }
        } else {
            mostrarError("Error al confirmar entrega");
        }
        
        return resultado;
    }
    
    /**
     * Anular pedido desde distribución
     */
    public boolean anularPedidoDistribucion(int idPedido, int idEmpleado, String motivoAnulacion) {
        if (motivoAnulacion == null || motivoAnulacion.trim().isEmpty()) {
            mostrarError("Debe especificar el motivo de anulación");
            return false;
        }
        
        boolean resultado = estadoPedidoService.anularPedido(
            idPedido,
            idEmpleado,
            motivoAnulacion
        );
        
        if (resultado) {
            mostrarMensaje("❌ Pedido anulado. Enviado a logística para evaluación.");
        } else {
            mostrarError("Error al anular pedido");
        }
        
        return resultado;
    }
    
    // ========================================
    // ÁREA FINANZAS
    // ========================================
    
    /**
     * Obtiene pedidos con deudas pendientes
     */
    public List<Pedido> obtenerPedidosConDeuda() {
        return estadoPedidoService.listarPedidosConDeuda();
    }
    
    /**
     * Registrar pago de deuda
     */
    public boolean registrarPagoDeuda(int idPedido, double monto, String metodoPago, int idEmpleado) {
        if (monto <= 0) {
            mostrarError("El monto debe ser mayor a 0");
            return false;
        }
        
        boolean resultado = estadoPedidoService.registrarPagoDeuda(
            idPedido,
            monto,
            metodoPago,
            idEmpleado
        );
        
        if (resultado) {
            mostrarMensaje("💰 Pago de deuda registrado correctamente");
        } else {
            mostrarError("Error al registrar pago");
        }
        
        return resultado;
    }
    
    /**
     * Obtener resumen de deudas por cliente
     */
    public String obtenerResumenDeudas(int idCliente) {
        return estadoPedidoService.generarResumenDeudas(idCliente);
    }
    
    // ========================================
    // CONSULTAS GENERALES
    // ========================================
    
    /**
     * Obtiene el historial completo de estados de un pedido
     */
    public List<HistorialEstadoPedido> obtenerHistorialPedido(int idPedido) {
        return estadoPedidoService.obtenerHistorialEstados(idPedido);
    }
    
    /**
     * Obtiene información detallada de un pedido
     */
    public String obtenerInformacionDetallada(int idPedido) {
        return estadoPedidoService.generarInformacionDetallada(idPedido);
    }
    
    /**
     * Verifica si un empleado puede cambiar el estado de un pedido
     */
    public boolean puedeModificarEstado(int idEmpleado, String estadoActual, String nuevoEstado) {
        return estadoPedidoService.validarPermisosCambioEstado(
            idEmpleado,
            estadoActual,
            nuevoEstado
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