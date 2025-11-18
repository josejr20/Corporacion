package com.empresa.inventario.service;

import java.util.List;

import com.empresa.inventario.dao.EmpleadoDAO;
import com.empresa.inventario.model.Empleado;

/**
 * Servicio que contiene la lógica de negocio para la gestión de empleados
 */
public class EmpleadoService {
    
    private final EmpleadoDAO empleadoDAO;
    
    public EmpleadoService() {
        this.empleadoDAO = new EmpleadoDAO();
    }
    
    /**
     * Registra un nuevo empleado
     */
    public boolean registrarEmpleado(Empleado empleado) {
        // Validaciones
        if (empleado == null) {
            System.out.println("Error: Empleado no puede ser nulo");
            return false;
        }
        
        if (empleado.getNombre() == null || empleado.getNombre().trim().isEmpty()) {
            System.out.println("Error: El nombre del empleado es obligatorio");
            return false;
        }
        
        if (empleado.getCargo() == null || empleado.getCargo().trim().isEmpty()) {
            System.out.println("Error: El cargo es obligatorio");
            return false;
        }
        
        return empleadoDAO.insertar(empleado);
    }
    
    /**
     * Actualiza los datos de un empleado
     */
    public boolean actualizarEmpleado(Empleado empleado) {
        if (empleado == null || empleado.getIdEmpleado() <= 0) {
            System.out.println("Error: Empleado inválido");
            return false;
        }
        
        if (empleado.getNombre() == null || empleado.getNombre().trim().isEmpty()) {
            System.out.println("Error: El nombre es obligatorio");
            return false;
        }
        
        return empleadoDAO.actualizar(empleado);
    }
    
    /**
     * Elimina un empleado
     */
    public boolean eliminarEmpleado(int idEmpleado) {
        if (idEmpleado <= 0) {
            System.out.println("Error: ID de empleado inválido");
            return false;
        }
        
        // TODO: Verificar que no tenga usuarios asociados
        
        return empleadoDAO.eliminar(idEmpleado);
    }
    
    /**
     * Lista todos los empleados
     */
    public List<Empleado> listarEmpleados() {
        return empleadoDAO.listar();
    }
}