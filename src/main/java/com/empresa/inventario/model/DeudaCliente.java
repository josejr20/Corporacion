package com.empresa.inventario.model;

import com.empresa.inventario.model.Cliente;
import com.empresa.inventario.model.Pedido;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeudaCliente {
    private int idDeuda;
    private Cliente cliente;
    private Pedido pedido;
    private double montoDeuda;
    private String motivoDeuda;
    private Date fechaRegistro;
    private String estadoDeuda; // Pendiente, Pagada, Parcial
    private double montoPagado;
    
    public double getMontoPendiente() {
        return montoDeuda - montoPagado;
    }
    
    public boolean estaPendiente() {
        return "Pendiente".equals(estadoDeuda) || getMontoPendiente() > 0;
    }
}