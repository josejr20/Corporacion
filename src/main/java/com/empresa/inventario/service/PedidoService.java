package com.empresa.inventario.service;

import com.empresa.inventario.dao.PedidoDAO;
import com.empresa.inventario.dao.DetallePedidoDAO;
import com.empresa.inventario.dao.FacturaDAO;
import com.empresa.inventario.dao.ProductoDAO;
import com.empresa.inventario.model.Pedido;
import com.empresa.inventario.model.DetallePedido;
import com.empresa.inventario.model.Factura;
import com.empresa.inventario.model.Producto;
import java.util.List;

/**
 * Servicio que contiene la lógica de negocio para la gestión de pedidos
 */
public class PedidoService {
    
    private final PedidoDAO pedidoDAO;
    private final DetallePedidoDAO detallePedidoDAO;
    private final FacturaDAO facturaDAO;
    private final ProductoDAO productoDAO;
    
    public PedidoService() {
        this.pedidoDAO = new PedidoDAO();
        this.detallePedidoDAO = new DetallePedidoDAO();
        this.facturaDAO = new FacturaDAO();
        this.productoDAO = new ProductoDAO();
    }
    
    /**
     * Registra un pedido completo con sus detalles y genera la factura
     */
    public boolean registrarPedido(Pedido pedido, List<DetallePedido> detalles) {
        // Validaciones
        if (pedido == null) {
            System.out.println("Error: Pedido no puede ser nulo");
            return false;
        }
        
        if (pedido.getCliente() == null || pedido.getCliente().getIdCliente() <= 0) {
            System.out.println("Error: Debe especificar un cliente válido");
            return false;
        }
        
        if (pedido.getEmpleado() == null || pedido.getEmpleado().getIdEmpleado() <= 0) {
            System.out.println("Error: Debe especificar un empleado válido");
            return false;
        }
        
        if (detalles == null || detalles.isEmpty()) {
            System.out.println("Error: El pedido debe tener al menos un producto");
            return false;
        }
        
        // Validar stock de todos los productos
        for (DetallePedido detalle : detalles) {
            Producto producto = productoDAO.obtenerPorId(detalle.getProducto().getIdProducto());
            if (producto == null) {
                System.out.println("Error: Producto no encontrado: " + detalle.getProducto().getIdProducto());
                return false;
            }
            
            if (producto.getStock() < detalle.getCantidad()) {
                System.out.println("Error: Stock insuficiente para: " + producto.getNombre());
                System.out.println("Stock disponible: " + producto.getStock() + ", solicitado: " + detalle.getCantidad());
                return false;
            }
        }
        
        // Registrar el pedido
        pedido.setEstadoPedido("Pendiente");
        boolean pedidoInsertado = pedidoDAO.insertar(pedido);
        
        if (!pedidoInsertado) {
            System.out.println("Error al registrar el pedido");
            return false;
        }
        
        // Registrar detalles del pedido y actualizar stock
        double totalPedido = 0;
        for (DetallePedido detalle : detalles) {
            detalle.setIdPedido(pedido.getIdPedido());
            
            // Calcular subtotal
            Producto producto = productoDAO.obtenerPorId(detalle.getProducto().getIdProducto());
            double subtotal = producto.getPrecio() * detalle.getCantidad();
            detalle.setSubtotal(subtotal);
            totalPedido += subtotal;
            
            // Insertar detalle
            if (!detallePedidoDAO.insertar(detalle)) {
                System.out.println("Error al insertar detalle del pedido");
                return false;
            }
            
            // Reducir stock
            int nuevoStock = producto.getStock() - detalle.getCantidad();
            producto.setStock(nuevoStock);
            productoDAO.actualizar(producto);
        }
        
        // Generar factura
        Factura factura = new Factura();
        factura.setPedido(pedido);
        factura.setTotal(totalPedido);
        
        if (!facturaDAO.generarFactura(factura)) {
            System.out.println("Error al generar la factura");
            return false;
        }
        
        System.out.println("Pedido registrado exitosamente con ID: " + pedido.getIdPedido());
        System.out.println("Total: S/ " + totalPedido);
        
        return true;
    }
    
    /**
     * Lista todos los pedidos
     */
    public List<Pedido> listarPedidos() {
        return pedidoDAO.listar();
    }
    
    /**
     * Obtiene los detalles de un pedido específico
     */
    public List<DetallePedido> obtenerDetallesPedido(int idPedido) {
        return detallePedidoDAO.listarPorPedido(idPedido);
    }
    
    /**
     * Actualiza el estado de un pedido
     */
    public boolean actualizarEstadoPedido(int idPedido, String nuevoEstado) {
        // Validar estados permitidos
        String[] estadosPermitidos = {"Pendiente", "En Proceso", "Completado", "Cancelado"};
        boolean estadoValido = false;
        
        for (String estado : estadosPermitidos) {
            if (estado.equalsIgnoreCase(nuevoEstado)) {
                estadoValido = true;
                break;
            }
        }
        
        if (!estadoValido) {
            System.out.println("Error: Estado no válido");
            return false;
        }
        
        return pedidoDAO.actualizarEstado(idPedido, nuevoEstado);
    }
}