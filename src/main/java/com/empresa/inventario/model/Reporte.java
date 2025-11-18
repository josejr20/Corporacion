package com.empresa.inventario.model;

import lombok.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reporte {
    private int idReporte;
    private String tipo;
    private Date fechaGeneracion;
}