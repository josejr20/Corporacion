package com.empresa.inventario.controller;

import com.empresa.inventario.service.PagoService;
import com.empresa.inventario.service.FacturaService;
import com.empresa.inventario.model.Pago;
import com.empresa.inventario.model.Factura;
import java.util.List;

/**
 * Controlador para gestionar las operaciones de pagos
 */
public class PagoController {
    
    private final PagoService pagoService;
    private final FacturaService facturaService;
    
    public PagoController() {
        this.pagoService = new PagoService();
        this.facturaService = new FacturaService();
    }
    
    /**
     * Registra un nuevo pago
     * @param idFactura ID de la factura
     * @param metodoPago Método de pago (Efectivo, Transferencia, Tarjeta, Yape, Plin)
     * @param monto Monto del pago
     * @return true si se registró correctamente
     */
    public boolean registrarPago(int idFactura, String metodoPago, double monto) {
        // Validaciones
        if (idFactura <= 0) {
            mostrarError("ID de factura inválido");
            return false;
        }
        
        if (metodoPago == null || metodoPago.trim().isEmpty()) {
            mostrarError("El método de pago es obligatorio");
            return false;
        }
        
        if (monto <= 0) {
            mostrarError("El monto debe ser mayor a 0");
            return false;
        }
        
        // Verificar que la factura existe (buscando por cualquier pedido)
        // En un caso real, necesitaríamos un método para buscar factura por ID
        
        // Crear el objeto Pago
        Factura factura = new Factura();
        factura.setIdFactura(idFactura);
        
        Pago pago = new Pago();
        pago.setFactura(factura);
        pago.setMetodoPago(metodoPago.trim());
        pago.setMonto(monto);
        
        // Registrar el pago
        boolean resultado = pagoService.registrarPago(pago);
        
        if (resultado) {
            mostrarMensaje("Pago registrado exitosamente");
            mostrarMensaje(String.format("Monto: S/ %.2f | Método: %s", monto, metodoPago));
        } else {
            mostrarError("Error al registrar el pago");
        }
        
        return resultado;
    }
    
    /**
     * Lista todos los pagos de una factura
     */
    public List<Pago> listarPagosPorFactura(int idFactura) {
        if (idFactura <= 0) {
            mostrarError("ID de factura inválido");
            return null;
        }
        
        List<Pago> pagos = pagoService.listarPagosPorFactura(idFactura);
        
        if (pagos == null || pagos.isEmpty()) {
            mostrarMensaje("No hay pagos registrados para esta factura");
        } else {
            mostrarMensaje("Se encontraron " + pagos.size() + " pagos");
        }
        
        return pagos;
    }
    
    /**
     * Calcula el total pagado de una factura
     */
    public double calcularTotalPagado(int idFactura) {
        List<Pago> pagos = listarPagosPorFactura(idFactura);
        
        if (pagos == null || pagos.isEmpty()) {
            return 0;
        }
        
        double totalPagado = 0;
        for (Pago pago : pagos) {
            totalPagado += pago.getMonto();
        }
        
        return totalPagado;
    }
    
    /**
     * Verifica si una factura está completamente pagada
     */
    public boolean verificarFacturaPagada(int idFactura, int idPedido) {
        // Obtener la factura para conocer el total
        Factura factura = facturaService.buscarFacturaPorPedido(idPedido);
        
        if (factura == null) {
            mostrarError("Factura no encontrada");
            return false;
        }
        
        double totalPagado = calcularTotalPagado(idFactura);
        double totalFactura = factura.getTotal();
        
        boolean pagado = totalPagado >= totalFactura;
        
        if (pagado) {
            mostrarMensaje("La factura está completamente pagada");
        } else {
            mostrarMensaje(String.format("Saldo pendiente: S/ %.2f", totalFactura - totalPagado));
        }
        
        return pagado;
    }
    
    /**
     * Obtiene información formateada de los pagos de una factura
     */
    public String obtenerInformacionPagos(int idFactura) {
        List<Pago> pagos = listarPagosPorFactura(idFactura);
        
        if (pagos == null || pagos.isEmpty()) {
            return "No hay pagos registrados para esta factura";
        }
        
        StringBuilder info = new StringBuilder();
        info.append("=== PAGOS DE FACTURA #").append(idFactura).append(" ===\n\n");
        
        double total = 0;
        for (Pago pago : pagos) {
            info.append(String.format("Pago #%d | Método: %s | Monto: S/ %.2f\n",
                pago.getIdPago(),
                pago.getMetodoPago(),
                pago.getMonto()));
            total += pago.getMonto();
        }
        
        info.append(String.format("\nTOTAL PAGADO: S/ %.2f", total));
        
        return info.toString();
    }
    
    // Métodos auxiliares
    private void mostrarMensaje(String mensaje) {
        System.out.println("✓ " + mensaje);
    }
    
    private void mostrarError(String mensaje) {
        System.err.println("✗ " + mensaje);
    }
}