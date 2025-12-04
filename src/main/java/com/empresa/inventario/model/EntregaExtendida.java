package com.empresa.inventario.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntregaExtendida extends Entrega {
    private Empleado repartidor;
    private Date fechaProgramada;
    private Date fechaEntregaReal;
    private String observaciones;
    
    public EntregaExtendida(int idEntrega, Pedido pedido, Date fechaEntrega, 
                           String estado, Empleado repartidor, Date fechaProgramada,
                           Date fechaEntregaReal, String observaciones) {
        super(idEntrega, pedido, fechaEntrega, estado);
        this.repartidor = repartidor;
        this.fechaProgramada = fechaProgramada;
        this.fechaEntregaReal = fechaEntregaReal;
        this.observaciones = observaciones;
    }
}