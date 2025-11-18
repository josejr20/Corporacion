package com.empresa.inventario.controller;

import com.empresa.inventario.service.ClienteService;
import com.empresa.inventario.model.Cliente;
import java.util.List;

/**
 * Controlador para gestionar las operaciones de clientes
 */
public class ClienteController {
    
    private final ClienteService clienteService;
    
    public ClienteController() {
        this.clienteService = new ClienteService();
    }
    
    /**
     * Registra un nuevo cliente
     */
    public boolean registrarCliente(String razonSocial, String ruc, String direccion, 
                                   String telefono, String email) {
        // Validaciones básicas
        if (razonSocial == null || razonSocial.trim().isEmpty()) {
            mostrarError("La razón social es obligatoria");
            return false;
        }
        
        if (ruc == null || ruc.trim().isEmpty()) {
            mostrarError("El RUC es obligatorio");
            return false;
        }
        
        // Crear el objeto Cliente
        Cliente cliente = new Cliente();
        cliente.setRazonSocial(razonSocial.trim());
        cliente.setRuc(ruc.trim());
        cliente.setDireccion(direccion != null ? direccion.trim() : "");
        cliente.setTelefono(telefono != null ? telefono.trim() : "");
        cliente.setEmail(email != null ? email.trim() : "");
        
        // Delegar al servicio
        boolean resultado = clienteService.registrarCliente(cliente);
        
        if (resultado) {
            mostrarMensaje("Cliente registrado exitosamente");
        } else {
            mostrarError("Error al registrar el cliente");
        }
        
        return resultado;
    }
    
    /**
     * Actualiza los datos de un cliente
     */
    public boolean actualizarCliente(int idCliente, String razonSocial, String ruc, 
                                    String direccion, String telefono, String email) {
        if (idCliente <= 0) {
            mostrarError("ID de cliente inválido");
            return false;
        }
        
        // Crear el objeto Cliente con los nuevos datos
        Cliente cliente = new Cliente();
        cliente.setIdCliente(idCliente);
        cliente.setRazonSocial(razonSocial.trim());
        cliente.setRuc(ruc.trim());
        cliente.setDireccion(direccion != null ? direccion.trim() : "");
        cliente.setTelefono(telefono != null ? telefono.trim() : "");
        cliente.setEmail(email != null ? email.trim() : "");
        
        boolean resultado = clienteService.actualizarCliente(cliente);
        
        if (resultado) {
            mostrarMensaje("Cliente actualizado exitosamente");
        } else {
            mostrarError("Error al actualizar el cliente");
        }
        
        return resultado;
    }
    
    /**
     * Elimina un cliente
     */
    public boolean eliminarCliente(int idCliente) {
        if (idCliente <= 0) {
            mostrarError("ID de cliente inválido");
            return false;
        }
        
        boolean resultado = clienteService.eliminarCliente(idCliente);
        
        if (resultado) {
            mostrarMensaje("Cliente eliminado exitosamente");
        } else {
            mostrarError("Error al eliminar el cliente. Puede tener pedidos asociados.");
        }
        
        return resultado;
    }
    
    /**
     * Obtiene un cliente por su ID
     */
    public Cliente obtenerCliente(int idCliente) {
        if (idCliente <= 0) {
            mostrarError("ID de cliente inválido");
            return null;
        }
        
        Cliente cliente = clienteService.obtenerClientePorId(idCliente);
        
        if (cliente == null) {
            mostrarError("Cliente no encontrado");
        }
        
        return cliente;
    }
    
    /**
     * Lista todos los clientes
     */
    public List<Cliente> listarClientes() {
        List<Cliente> clientes = clienteService.listarTodosLosClientes();
        
        if (clientes.isEmpty()) {
            mostrarMensaje("No hay clientes registrados");
        } else {
            mostrarMensaje("Se encontraron " + clientes.size() + " clientes");
        }
        
        return clientes;
    }
    
    /**
     * Busca un cliente por su RUC
     */
    public Cliente buscarPorRuc(String ruc) {
        if (ruc == null || ruc.trim().isEmpty()) {
            mostrarError("Debe ingresar un RUC");
            return null;
        }
        
        Cliente cliente = clienteService.buscarPorRuc(ruc.trim());
        
        if (cliente == null) {
            mostrarError("No se encontró cliente con el RUC: " + ruc);
        }
        
        return cliente;
    }
    
    /**
     * Obtiene información formateada de un cliente
     */
    public String obtenerInformacionCliente(int idCliente) {
        Cliente cliente = obtenerCliente(idCliente);
        
        if (cliente == null) {
            return "Cliente no encontrado";
        }
        
        return String.format(
            "ID: %d\nRazón Social: %s\nRUC: %s\nDirección: %s\nTeléfono: %s\nEmail: %s",
            cliente.getIdCliente(),
            cliente.getRazonSocial(),
            cliente.getRuc(),
            cliente.getDireccion(),
            cliente.getTelefono(),
            cliente.getEmail()
        );
    }
    
    // Métodos auxiliares
    private void mostrarMensaje(String mensaje) {
        System.out.println("✓ " + mensaje);
    }
    
    private void mostrarError(String mensaje) {
        System.err.println("✗ " + mensaje);
    }
}