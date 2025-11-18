package com.empresa.inventario.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetallePedido {
    private int idDetalle;
    private int idPedido;
    private Producto producto;
    private int cantidad;
    private double subtotal;
}