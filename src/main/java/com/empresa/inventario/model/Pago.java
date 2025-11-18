package com.empresa.inventario.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pago {
    private int idPago;
    private Factura factura;
    private String metodoPago;
    private double monto;
}