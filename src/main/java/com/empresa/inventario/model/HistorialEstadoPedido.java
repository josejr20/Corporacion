package com.empresa.inventario.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistorialEstadoPedido {
    private int idHistorial;
    private Pedido pedido;
    private String estadoAnterior;
    private String estadoNuevo;
    private Empleado empleado;
    private Date fechaCambio;
    private String observaciones;
}
