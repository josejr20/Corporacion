package com.empresa.inventario.service;

import com.empresa.inventario.dao.ClienteDAO;
import com.empresa.inventario.model.Cliente;
import java.util.List;

/**
 * Servicio que contiene la lógica de negocio para la gestión de clientes
 */
public class ClienteService {
    
    private final ClienteDAO clienteDAO;
    
    public ClienteService() {
        this.clienteDAO = new ClienteDAO();
    }
    
    /**
     * Registra un nuevo cliente validando los datos
     */
    public boolean registrarCliente(Cliente cliente) {
        // Validaciones de negocio
        if (cliente == null) {
            System.out.println("Error: Cliente no puede ser nulo");
            return false;
        }
        
        if (cliente.getRazonSocial() == null || cliente.getRazonSocial().trim().isEmpty()) {
            System.out.println("Error: La razón social es obligatoria");
            return false;
        }
        
        if (cliente.getRuc() == null || cliente.getRuc().trim().isEmpty()) {
            System.out.println("Error: El RUC es obligatorio");
            return false;
        }
        
        // Validar formato de RUC (11 dígitos para Perú)
        if (!cliente.getRuc().matches("\\d{11}")) {
            System.out.println("Error: El RUC debe tener 11 dígitos");
            return false;
        }
        
        // Validar email si se proporciona
        if (cliente.getEmail() != null && !cliente.getEmail().isEmpty()) {
            if (!cliente.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                System.out.println("Error: Email inválido");
                return false;
            }
        }
        
        // Si todas las validaciones pasan, insertar el cliente
        return clienteDAO.insertarCliente(cliente);
    }
    
    /**
     * Actualiza los datos de un cliente existente
     */
    public boolean actualizarCliente(Cliente cliente) {
        if (cliente == null || cliente.getIdCliente() <= 0) {
            System.out.println("Error: Cliente inválido");
            return false;
        }
        
        // Validar que el cliente existe
        Cliente clienteExistente = clienteDAO.obtenerClientePorId(cliente.getIdCliente());
        if (clienteExistente == null) {
            System.out.println("Error: Cliente no encontrado");
            return false;
        }
        
        // Validaciones similares al registro
        if (cliente.getRazonSocial() == null || cliente.getRazonSocial().trim().isEmpty()) {
            System.out.println("Error: La razón social es obligatoria");
            return false;
        }
        
        if (cliente.getRuc() == null || !cliente.getRuc().matches("\\d{11}")) {
            System.out.println("Error: RUC inválido");
            return false;
        }
        
        return clienteDAO.actualizarCliente(cliente);
    }
    
    /**
     * Elimina un cliente si no tiene pedidos asociados
     */
    public boolean eliminarCliente(int idCliente) {
        if (idCliente <= 0) {
            System.out.println("Error: ID de cliente inválido");
            return false;
        }
        
        // Verificar que el cliente existe
        Cliente cliente = clienteDAO.obtenerClientePorId(idCliente);
        if (cliente == null) {
            System.out.println("Error: Cliente no encontrado");
            return false;
        }
        
        // TODO: Verificar que no tenga pedidos activos antes de eliminar
        // Esta lógica se puede agregar consultando PedidoDAO
        
        return clienteDAO.eliminarCliente(idCliente);
    }
    
    /**
     * Obtiene un cliente por su ID
     */
    public Cliente obtenerClientePorId(int idCliente) {
        if (idCliente <= 0) {
            System.out.println("Error: ID de cliente inválido");
            return null;
        }
        return clienteDAO.obtenerClientePorId(idCliente);
    }
    
    /**
     * Lista todos los clientes registrados
     */
    public List<Cliente> listarTodosLosClientes() {
        return clienteDAO.listarClientes();
    }
    
    /**
     * Busca clientes por RUC
     */
    public Cliente buscarPorRuc(String ruc) {
        if (ruc == null || ruc.trim().isEmpty()) {
            return null;
        }
        
        List<Cliente> clientes = clienteDAO.listarClientes();
        return clientes.stream()
                .filter(c -> c.getRuc().equals(ruc))
                .findFirst()
                .orElse(null);
    }
}