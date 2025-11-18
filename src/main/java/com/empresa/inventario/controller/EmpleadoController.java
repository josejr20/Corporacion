package com.empresa.inventario.controller;

import com.empresa.inventario.service.EmpleadoService;
import com.empresa.inventario.model.Empleado;
import java.util.List;

/**
 * Controlador para gestionar las operaciones de empleados
 */
public class EmpleadoController {
    
    private final EmpleadoService empleadoService;
    
    public EmpleadoController() {
        this.empleadoService = new EmpleadoService();
    }
    
    /**
     * Registra un nuevo empleado
     */
    public boolean registrarEmpleado(String nombre, String cargo) {
        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarError("El nombre del empleado es obligatorio");
            return false;
        }
        
        if (cargo == null || cargo.trim().isEmpty()) {
            mostrarError("El cargo es obligatorio");
            return false;
        }
        
        Empleado empleado = new Empleado();
        empleado.setNombre(nombre.trim());
        empleado.setCargo(cargo.trim());
        
        boolean resultado = empleadoService.registrarEmpleado(empleado);
        
        if (resultado) {
            mostrarMensaje("Empleado registrado exitosamente");
        } else {
            mostrarError("Error al registrar el empleado");
        }
        
        return resultado;
    }
    
    /**
     * Actualiza los datos de un empleado
     */
    public boolean actualizarEmpleado(int idEmpleado, String nombre, String cargo) {
        if (idEmpleado <= 0) {
            mostrarError("ID de empleado inválido");
            return false;
        }
        
        Empleado empleado = new Empleado();
        empleado.setIdEmpleado(idEmpleado);
        empleado.setNombre(nombre.trim());
        empleado.setCargo(cargo.trim());
        
        boolean resultado = empleadoService.actualizarEmpleado(empleado);
        
        if (resultado) {
            mostrarMensaje("Empleado actualizado exitosamente");
        } else {
            mostrarError("Error al actualizar el empleado");
        }
        
        return resultado;
    }
    
    /**
     * Elimina un empleado
     */
    public boolean eliminarEmpleado(int idEmpleado) {
        if (idEmpleado <= 0) {
            mostrarError("ID de empleado inválido");
            return false;
        }
        
        boolean resultado = empleadoService.eliminarEmpleado(idEmpleado);
        
        if (resultado) {
            mostrarMensaje("Empleado eliminado exitosamente");
        } else {
            mostrarError("Error al eliminar el empleado");
        }
        
        return resultado;
    }
    
    /**
     * Lista todos los empleados
     */
    public List<Empleado> listarEmpleados() {
        List<Empleado> empleados = empleadoService.listarEmpleados();
        
        if (empleados.isEmpty()) {
             mostrarMensaje("No hay empleados registrados");
        } else {
            mostrarMensaje("Se encontraron " + empleados.size() + " empleados");
        }
        
        return empleados;
    }
    
    /**
     * Obtiene información formateada de un empleado
     */
    public String obtenerInformacionEmpleado(int idEmpleado) {
        List<Empleado> empleados = empleadoService.listarEmpleados();
        Empleado empleado = empleados.stream()
            .filter(e -> e.getIdEmpleado() == idEmpleado)
            .findFirst()
            .orElse(null);
        
        if (empleado == null) {
            return "Empleado no encontrado";
        }
        
        return String.format(
            "ID: %d\nNombre: %s\nCargo: %s",
            empleado.getIdEmpleado(),
            empleado.getNombre(),
            empleado.getCargo()
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