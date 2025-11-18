package com.empresa.inventario.model;

import lombok.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Entrega {
    private int idEntrega;
    private Pedido pedido;
    private Date fechaEntrega;
    private String estado;
}