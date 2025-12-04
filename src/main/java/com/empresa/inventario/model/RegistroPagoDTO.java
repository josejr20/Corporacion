package com.empresa.inventario.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistroPagoDTO {
    private int idPedido;
    private double montoRecibido;
    private String metodoPago;
    private String motivoFaltante;
    private int idEmpleado;
}