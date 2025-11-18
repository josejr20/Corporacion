package com.empresa.inventario.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Factura {
    private int idFactura;
    private Pedido pedido;
    private double total;
}