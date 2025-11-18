package com.empresa.inventario.controller;

import com.empresa.inventario.service.UsuarioService;
import com.empresa.inventario.model.Usuario;

/**
 * Controlador para gestionar el inicio de sesión y autenticación
 */
public class LoginController {
    
    private final UsuarioService usuarioService;
    private Usuario usuarioActual;
    
    public LoginController() {
        this.usuarioService = new UsuarioService();
        this.usuarioActual = null;
    }
    
    /**
     * Intenta autenticar al usuario con las credenciales proporcionadas
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return true si la autenticación fue exitosa
     */
    public boolean iniciarSesion(String username, String password) {
        // Validaciones básicas en el controlador
        if (username == null || username.trim().isEmpty()) {
            mostrarError("Por favor ingrese un nombre de usuario");
            return false;
        }
        
        if (password == null || password.trim().isEmpty()) {
            mostrarError("Por favor ingrese una contraseña");
            return false;
        }
        
        // Delegar la autenticación al servicio
        usuarioActual = usuarioService.autenticar(username, password);
        
        if (usuarioActual != null) {
            mostrarMensaje("Bienvenido, " + username);
            return true;
        } else {
            mostrarError("Usuario o contraseña incorrectos");
            return false;
        }
    }
    
    /**
     * Cierra la sesión del usuario actual
     */
    public void cerrarSesion() {
        if (usuarioActual != null) {
            System.out.println("Sesión cerrada para: " + usuarioActual.getUsername());
            usuarioActual = null;
        }
    }
    
    /**
     * Obtiene el usuario actualmente autenticado
     * @return Usuario actual o null si no hay sesión
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    /**
     * Verifica si hay una sesión activa
     * @return true si hay un usuario autenticado
     */
    public boolean haySesionActiva() {
        return usuarioActual != null;
    }
    
    /**
     * Verifica si el usuario actual es administrador
     * @return true si es administrador
     */
    public boolean esAdministrador() {
        return usuarioActual != null && 
               usuarioService.esAdministrador(usuarioActual);
    }
    
    /**
     * Verifica si el usuario actual es vendedor
     * @return true si es vendedor
     */
    public boolean esVendedor() {
        return usuarioActual != null && 
               usuarioService.esVendedor(usuarioActual);
    }
    
    /**
     * Obtiene el rol del usuario actual
     * @return Nombre del rol o "Sin sesión"
     */
    public String getRolActual() {
        if (usuarioActual != null && usuarioActual.getRol() != null) {
            return usuarioActual.getRol().getNombreRol();
        }
        return "Sin sesión";
    }
    
    /**
     * Cambia la contraseña del usuario actual
     * @param passwordActual Contraseña actual
     * @param passwordNueva Nueva contraseña
     * @return true si se cambió correctamente
     */
    public boolean cambiarPassword(String passwordActual, String passwordNueva) {
        if (usuarioActual == null) {
            mostrarError("No hay sesión activa");
            return false;
        }
        
        if (passwordNueva == null || passwordNueva.length() < 6) {
            mostrarError("La nueva contraseña debe tener al menos 6 caracteres");
            return false;
        }
        
        // Verificar que la contraseña actual sea correcta
        if (!usuarioActual.getPassword().equals(passwordActual)) {
            mostrarError("La contraseña actual es incorrecta");
            return false;
        }
        
        boolean resultado = usuarioService.cambiarPassword(
            usuarioActual.getIdUsuario(), 
            passwordActual, 
            passwordNueva
        );
        
        if (resultado) {
            mostrarMensaje("Contraseña actualizada exitosamente");
            // Actualizar el objeto en memoria
            usuarioActual.setPassword(passwordNueva);
        } else {
            mostrarError("Error al actualizar la contraseña");
        }
        
        return resultado;
    }
    
    // Métodos auxiliares para mostrar mensajes
    private void mostrarMensaje(String mensaje) {
        System.out.println("✓ " + mensaje);
    }
    
    private void mostrarError(String mensaje) {
        System.err.println("✗ " + mensaje);
    }
}