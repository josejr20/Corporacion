package com.empresa.inventario.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CambioEstadoDTO {
    private int idPedido;
    private String estadoActual;
    private String nuevoEstado;
    private int idEmpleado;
    private String observaciones;
    private String motivoRechazo;
    private String motivoAnulacion;
}
