package com.empresa.inventario.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cliente {
    private int idCliente;
    private String razonSocial;
    private String ruc;
    private String direccion;
    private String telefono;
    private String email;
}
