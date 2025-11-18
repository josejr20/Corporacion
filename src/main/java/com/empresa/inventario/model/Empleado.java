package com.empresa.inventario.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Empleado {
    private int idEmpleado;
    private String nombre;
    private String cargo;
}