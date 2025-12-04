package com.empresa.inventario.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteExtendido extends Cliente {
    private String tipoCliente; // Bodega, Restaurante, Cevicheria, Polleria, Mercado, Otro
    private double deudaTotal;
    
    public ClienteExtendido(int idCliente, String razonSocial, String ruc, 
                           String direccion, String telefono, String email,
                           String tipoCliente, double deudaTotal) {
        super(idCliente, razonSocial, ruc, direccion, telefono, email);
        this.tipoCliente = tipoCliente;
        this.deudaTotal = deudaTotal;
    }
    
    public boolean tieneDeudas() {
        return deudaTotal > 0;
    }
}
