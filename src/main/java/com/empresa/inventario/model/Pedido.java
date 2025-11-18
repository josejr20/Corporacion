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
    private String estadoPedido;
}