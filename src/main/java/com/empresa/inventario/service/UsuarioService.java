package com.empresa.inventario.service;

import com.empresa.inventario.dao.EmpleadoDAO;
import com.empresa.inventario.dao.UsuarioDAO;
import com.empresa.inventario.model.Usuario;

/**
 * Servicio que contiene la lógica de negocio para la gestión de usuarios
 */
public class UsuarioService {
    
    private final UsuarioDAO usuarioDAO;
    private final EmpleadoDAO empleadoDAO;
    
    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
        this.empleadoDAO = new EmpleadoDAO();
    }
    
    /**
     * Autentica un usuario en el sistema
     */
    public Usuario autenticar(String username, String password) {
        // Validaciones básicas
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Error: El nombre de usuario es obligatorio");
            return null;
        }
        
        if (password == null || password.trim().isEmpty()) {
            System.out.println("Error: La contraseña es obligatoria");
            return null;
        }
        
        // Intentar autenticar
        Usuario usuario = usuarioDAO.autenticar(username, password);
        
        if (usuario != null) {
            System.out.println("Inicio de sesión exitoso para: " + username);
            System.out.println("Rol: " + usuario.getRol().getNombreRol());
        } else {
            System.out.println("Error: Usuario o contraseña incorrectos");
        }
        
        return usuario;
    }
    
    /**
     * Registra un nuevo usuario
     */
    public boolean registrarUsuario(Usuario usuario) {
        // Validaciones
        if (usuario == null) {
            System.out.println("Error: Usuario no puede ser nulo");
            return false;
        }
        
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            System.out.println("Error: El nombre de usuario es obligatorio");
            return false;
        }
        
        if (usuario.getPassword() == null || usuario.getPassword().length() < 6) {
            System.out.println("Error: La contraseña debe tener al menos 6 caracteres");
            return false;
        }
        
        if (usuario.getRol() == null) {
            System.out.println("Error: Debe asignar un rol al usuario");
            return false;
        }
        
        // Verificar que el empleado existe
        if (usuario.getIdEmpleado() <= 0) {
            System.out.println("Error: Debe asociar un empleado válido");
            return false;
        }
        
        // Encriptar contraseña (simplificado - en producción usar BCrypt)
        // usuario.setPassword(encriptarPassword(usuario.getPassword()));
        
        return usuarioDAO.insertar(usuario);
    }
    
    /**
     * Actualiza la contraseña de un usuario
     */
    public boolean cambiarPassword(int idUsuario, String passwordActual, String passwordNueva) {
        if (idUsuario <= 0) {
            System.out.println("Error: ID de usuario inválido");
            return false;
        }
        
        if (passwordNueva == null || passwordNueva.length() < 6) {
            System.out.println("Error: La nueva contraseña debe tener al menos 6 caracteres");
            return false;
        }
        
        // TODO: Verificar que la contraseña actual sea correcta
        
        return usuarioDAO.actualizarPassword(idUsuario, passwordNueva);
    }
    
    /**
     * Verifica si un usuario tiene permisos de administrador
     */
    public boolean esAdministrador(Usuario usuario) {
        if (usuario == null || usuario.getRol() == null) {
            return false;
        }
        return "Administrador".equalsIgnoreCase(usuario.getRol().getNombreRol());
    }
    
    /**
     * Verifica si un usuario tiene permisos de ventas
     */
    public boolean esVendedor(Usuario usuario) {
        if (usuario == null || usuario.getRol() == null) {
            return false;
        }
        return "Vendedor".equalsIgnoreCase(usuario.getRol().getNombreRol());
    }
    
    // Método auxiliar para encriptar contraseñas (simplificado)
    private String encriptarPassword(String password) {
        // En producción usar BCrypt o similar
        return password; // Por ahora sin encriptación
    }
}