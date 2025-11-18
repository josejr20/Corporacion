package com.empresa.inventario.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {
    private int idUsuario;
    private String username;
    private String password;
    private Rol rol;
    private int idEmpleado; // necesario para relación con Empleado
}
