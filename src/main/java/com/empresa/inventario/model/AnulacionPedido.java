package com.empresa.inventario.model;

import lombok.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnulacionPedido {
    private int idAnulacion;
    private Pedido pedido;
    private String motivoAnulacion;
    private String tipoAnulacion; // ClienteNoDisponible, ClienteRechazo, ErrorSistema, Otro
    private Empleado empleadoAnula;
    private Date fechaAnulacion;
    private String estadoEvaluacion; // Pendiente, Aprobada, EnRestauracion, Completada
    private Empleado empleadoEvalua;
    private Date fechaEvaluacion;
    private String observacionesEvaluacion;
}
