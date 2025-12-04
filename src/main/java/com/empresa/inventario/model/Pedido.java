package com.empresa.inventario.model;

import lombok.*;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pedido {
    private int idPedido;
    private Cliente cliente;
    private Empleado empleado;
    private Date fecha;
    private List<DetallePedido> detalles;
    
    // Estados extendidos del pedido
    private String estadoPedido; // Enum en BD
    private String motivoRechazo;
    private String motivoAnulacion;
    private int intentosContacto;
    
    // Montos
    private double montoTotal;
    private double montoPagado;
    private double deudaPendiente;
    
    private Date fechaUltimaActualizacion;
    
    // Constructor simplificado
    public Pedido(int idPedido, Cliente cliente, Empleado empleado, Date fecha, 
                  List<DetallePedido> detalles, String estadoPedido) {
        this.idPedido = idPedido;
        this.cliente = cliente;
        this.empleado = empleado;
        this.fecha = fecha;
        this.detalles = detalles;
        this.estadoPedido = estadoPedido;
        this.intentosContacto = 0;
        this.montoTotal = 0;
        this.montoPagado = 0;
        this.deudaPendiente = 0;
    }
    
    // Métodos de utilidad
    public boolean necesitaValidacionComercial() {
        return "Registrado".equals(estadoPedido) || "PendienteComercial".equals(estadoPedido);
    }
    
    public boolean necesitaValidacionAdministrativa() {
        return "AprobadoComercial".equals(estadoPedido) || "PendienteAdministrativo".equals(estadoPedido);
    }
    
    public boolean estaEnAlmacen() {
        return "AprobadoAdministrativo".equals(estadoPedido) || 
               "EnAlmacen".equals(estadoPedido) || 
               "Alistado".equals(estadoPedido) || 
               "Empaquetado".equals(estadoPedido);
    }
    
    public boolean estaEnDistribucion() {
        return "EnDistribucion".equals(estadoPedido) || 
               "EnCamino".equals(estadoPedido) || 
               "IntentandoEntrega".equals(estadoPedido);
    }
    
    public boolean fueEntregado() {
        return "Entregado".equals(estadoPedido);
    }
    
    public boolean fueAnulado() {
        return "Anulado".equals(estadoPedido) || 
               "EnEvaluacion".equals(estadoPedido) || 
               "EnRestauracion".equals(estadoPedido);
    }
    
    public boolean tienePagoPendiente() {
        return deudaPendiente > 0;
    }
    
    public double calcularPorcentajePagado() {
        if (montoTotal == 0) return 0;
        return (montoPagado / montoTotal) * 100;
    }
}