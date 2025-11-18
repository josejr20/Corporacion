package com.empresa.inventario.service;

import com.empresa.inventario.dao.ProductoDAO;
import com.empresa.inventario.model.Producto;
import java.util.List;

/**
 * Servicio que contiene la lógica de negocio para la gestión de productos
 */
public class ProductoService {
    
    private final ProductoDAO productoDAO;
    
    public ProductoService() {
        this.productoDAO = new ProductoDAO();
    }
    
    /**
     * Registra un nuevo producto validando los datos
     */
    public boolean registrarProducto(Producto producto) {
        // Validaciones de negocio
        if (producto == null) {
            System.out.println("Error: Producto no puede ser nulo");
            return false;
        }
        
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            System.out.println("Error: El nombre del producto es obligatorio");
            return false;
        }
        
        if (producto.getPrecio() <= 0) {
            System.out.println("Error: El precio debe ser mayor a 0");
            return false;
        }
        
        if (producto.getStock() < 0) {
            System.out.println("Error: El stock no puede ser negativo");
            return false;
        }
        
        return productoDAO.insertar(producto);
    }
    
    /**
     * Actualiza un producto existente
     */
    public boolean actualizarProducto(Producto producto) {
        if (producto == null || producto.getIdProducto() <= 0) {
            System.out.println("Error: Producto inválido");
            return false;
        }
        
        // Verificar que el producto existe
        Producto productoExistente = productoDAO.obtenerPorId(producto.getIdProducto());
        if (productoExistente == null) {
            System.out.println("Error: Producto no encontrado");
            return false;
        }
        
        if (producto.getPrecio() <= 0) {
            System.out.println("Error: El precio debe ser mayor a 0");
            return false;
        }
        
        return productoDAO.actualizar(producto);
    }
    
    /**
     * Elimina un producto
     */
    public boolean eliminarProducto(int idProducto) {
        if (idProducto <= 0) {
            System.out.println("Error: ID de producto inválido");
            return false;
        }
        
        return productoDAO.eliminar(idProducto);
    }
    
    /**
     * Obtiene un producto por su ID
     */
    public Producto obtenerProductoPorId(int idProducto) {
        if (idProducto <= 0) {
            return null;
        }
        return productoDAO.obtenerPorId(idProducto);
    }
    
    /**
     * Lista todos los productos
     */
    public List<Producto> listarTodosLosProductos() {
        return productoDAO.listar();
    }
    
    /**
     * Verifica si hay stock disponible
     */
    public boolean verificarStock(int idProducto, int cantidadRequerida) {
        Producto producto = productoDAO.obtenerPorId(idProducto);
        if (producto == null) {
            return false;
        }
        return producto.getStock() >= cantidadRequerida;
    }
    
    /**
     * Actualiza el stock de un producto (usado en pedidos)
     */
    public boolean actualizarStock(int idProducto, int nuevaCantidad) {
        if (idProducto <= 0 || nuevaCantidad < 0) {
            System.out.println("Error: Parámetros inválidos");
            return false;
        }
        
        Producto producto = productoDAO.obtenerPorId(idProducto);
        if (producto == null) {
            System.out.println("Error: Producto no encontrado");
            return false;
        }
        
        producto.setStock(nuevaCantidad);
        return productoDAO.actualizar(producto);
    }
    
    /**
     * Reduce el stock después de un pedido
     */
    public boolean reducirStock(int idProducto, int cantidad) {
        Producto producto = productoDAO.obtenerPorId(idProducto);
        if (producto == null) {
            System.out.println("Error: Producto no encontrado");
            return false;
        }
        
        if (producto.getStock() < cantidad) {
            System.out.println("Error: Stock insuficiente");
            return false;
        }
        
        int nuevoStock = producto.getStock() - cantidad;
        producto.setStock(nuevoStock);
        return productoDAO.actualizar(producto);
    }
}