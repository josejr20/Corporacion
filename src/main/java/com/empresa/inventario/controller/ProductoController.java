package com.empresa.inventario.controller;

import com.empresa.inventario.service.ProductoService;
import com.empresa.inventario.model.Producto;
import java.util.List;

/**
 * Controlador para gestionar las operaciones de productos
 */
public class ProductoController {
    
    private final ProductoService productoService;
    
    public ProductoController() {
        this.productoService = new ProductoService();
    }
    
    /**
     * Registra un nuevo producto
     * @param nombre Nombre del producto
     * @param descripcion Descripción
     * @param precio Precio unitario
     * @param stock Stock inicial
     * @return true si se registró correctamente
     */
    public boolean registrarProducto(String nombre, String descripcion, double precio, int stock) {
        // Validaciones en el controlador
        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarError("El nombre del producto es obligatorio");
            return false;
        }
        
        if (precio <= 0) {
            mostrarError("El precio debe ser mayor a 0");
            return false;
        }
        
        if (stock < 0) {
            mostrarError("El stock no puede ser negativo");
            return false;
        }
        
        // Crear el objeto Producto
        Producto producto = new Producto();
        producto.setNombre(nombre.trim());
        producto.setDescripcion(descripcion != null ? descripcion.trim() : "");
        producto.setPrecio(precio);
        producto.setStock(stock);
        
        // Delegar al servicio
        boolean resultado = productoService.registrarProducto(producto);
        
        if (resultado) {
            mostrarMensaje("Producto registrado exitosamente con ID: " + producto.getIdProducto());
        } else {
            mostrarError("Error al registrar el producto");
        }
        
        return resultado;
    }
    
    /**
     * Actualiza los datos de un producto existente
     */
    public boolean actualizarProducto(int idProducto, String nombre, String descripcion, 
                                     double precio, int stock) {
        if (idProducto <= 0) {
            mostrarError("ID de producto inválido");
            return false;
        }
        
        // Obtener el producto actual
        Producto producto = productoService.obtenerProductoPorId(idProducto);
        if (producto == null) {
            mostrarError("Producto no encontrado");
            return false;
        }
        
        // Actualizar los campos
        if (nombre != null && !nombre.trim().isEmpty()) {
            producto.setNombre(nombre.trim());
        }
        if (descripcion != null) {
            producto.setDescripcion(descripcion.trim());
        }
        if (precio > 0) {
            producto.setPrecio(precio);
        }
        if (stock >= 0) {
            producto.setStock(stock);
        }
        
        // Delegar al servicio
        boolean resultado = productoService.actualizarProducto(producto);
        
        if (resultado) {
            mostrarMensaje("Producto actualizado exitosamente");
        } else {
            mostrarError("Error al actualizar el producto");
        }
        
        return resultado;
    }
    
    /**
     * Elimina un producto
     */
    public boolean eliminarProducto(int idProducto) {
        if (idProducto <= 0) {
            mostrarError("ID de producto inválido");
            return false;
        }
        
        // Verificar que existe
        Producto producto = productoService.obtenerProductoPorId(idProducto);
        if (producto == null) {
            mostrarError("Producto no encontrado");
            return false;
        }
        
        boolean resultado = productoService.eliminarProducto(idProducto);
        
        if (resultado) {
            mostrarMensaje("Producto eliminado exitosamente");
        } else {
            mostrarError("Error al eliminar el producto. Puede tener pedidos asociados.");
        }
        
        return resultado;
    }
    
    /**
     * Obtiene un producto por su ID
     */
    public Producto obtenerProducto(int idProducto) {
        if (idProducto <= 0) {
            mostrarError("ID de producto inválido");
            return null;
        }
        
        Producto producto = productoService.obtenerProductoPorId(idProducto);
        
        if (producto == null) {
            mostrarError("Producto no encontrado");
        }
        
        return producto;
    }
    
    /**
     * Lista todos los productos
     */
    public List<Producto> listarProductos() {
        List<Producto> productos = productoService.listarTodosLosProductos();
        
        if (productos.isEmpty()) {
            mostrarMensaje("No hay productos registrados");
        } else {
            mostrarMensaje("Se encontraron " + productos.size() + " productos");
        }
        
        return productos;
    }
    
    /**
     * Verifica si hay stock disponible de un producto
     */
    public boolean verificarStock(int idProducto, int cantidad) {
        if (idProducto <= 0 || cantidad <= 0) {
            return false;
        }
        
        return productoService.verificarStock(idProducto, cantidad);
    }
    
    /**
     * Actualiza el stock de un producto
     */
    public boolean actualizarStock(int idProducto, int nuevoStock) {
        if (idProducto <= 0) {
            mostrarError("ID de producto inválido");
            return false;
        }
        
        if (nuevoStock < 0) {
            mostrarError("El stock no puede ser negativo");
            return false;
        }
        
        boolean resultado = productoService.actualizarStock(idProducto, nuevoStock);
        
        if (resultado) {
            mostrarMensaje("Stock actualizado exitosamente");
        } else {
            mostrarError("Error al actualizar el stock");
        }
        
        return resultado;
    }
    
    /**
     * Reduce el stock después de un pedido
     */
    public boolean reducirStock(int idProducto, int cantidad) {
        if (idProducto <= 0 || cantidad <= 0) {
            mostrarError("Parámetros inválidos");
            return false;
        }
        
        boolean resultado = productoService.reducirStock(idProducto, cantidad);
        
        if (!resultado) {
            mostrarError("Stock insuficiente o error al reducir stock");
        }
        
        return resultado;
    }
    
    /**
     * Obtiene información formateada de un producto
     */
    public String obtenerInformacionProducto(int idProducto) {
        Producto producto = obtenerProducto(idProducto);
        
        if (producto == null) {
            return "Producto no encontrado";
        }
        
        return String.format(
            "ID: %d\nNombre: %s\nDescripción: %s\nPrecio: S/ %.2f\nStock: %d",
            producto.getIdProducto(),
            producto.getNombre(),
            producto.getDescripcion(),
            producto.getPrecio(),
            producto.getStock()
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