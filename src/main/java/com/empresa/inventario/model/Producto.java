package com.empresa.inventario.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Producto {
    private int idProducto;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;
}