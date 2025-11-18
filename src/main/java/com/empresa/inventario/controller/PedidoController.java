package com.empresa.inventario.controller;

import com.empresa.inventario.service.PedidoService;
import com.empresa.inventario.service.ClienteService;
import com.empresa.inventario.service.EmpleadoService;
import com.empresa.inventario.service.ProductoService;
import com.empresa.inventario.model.Pedido;
import com.empresa.inventario.model.DetallePedido;
import com.empresa.inventario.model.Cliente;
import com.empresa.inventario.model.Empleado;
import com.empresa.inventario.model.Producto;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controlador para gestionar las operaciones de pedidos
 */
public class PedidoController {
    
    private final PedidoService pedidoService;
    private final ClienteService clienteService;
    private final EmpleadoService empleadoService;
    private final ProductoService productoService;
    
    public PedidoController() {
        this.pedidoService = new PedidoService();
        this.clienteService = new ClienteService();
        this.empleadoService = new EmpleadoService();
        this.productoService = new ProductoService();
    }
    
    /**
     * Registra un nuevo pedido
     * @param idCliente ID del cliente
     * @param idEmpleado ID del empleado que registra el pedido
     * @param detalles Lista de detalles del pedido (idProducto, cantidad)
     * @return true si se registró correctamente
     */
    public boolean registrarPedido(int idCliente, int idEmpleado, 
                                  List<DetallePedidoDTO> detalles) {
        // Validaciones
        if (idCliente <= 0 || idEmpleado <= 0) {
            mostrarError("IDs de cliente y empleado son obligatorios");
            return false;
        }
        
        if (detalles == null || detalles.isEmpty()) {
            mostrarError("El pedido debe tener al menos un producto");
            return false;
        }
        
        // Obtener cliente y empleado
        Cliente cliente = clienteService.obtenerClientePorId(idCliente);
        if (cliente == null) {
            mostrarError("Cliente no encontrado");
            return false;
        }
        
        Empleado empleado = empleadoService.listarEmpleados().stream()
            .filter(e -> e.getIdEmpleado() == idEmpleado)
            .findFirst()
            .orElse(null);
        
        if (empleado == null) {
            mostrarError("Empleado no encontrado");
            return false;
        }
        
        // Crear el pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setEmpleado(empleado);
        pedido.setFecha(new Date());
        pedido.setEstadoPedido("Pendiente");
        
        // Construir la lista de detalles
        List<DetallePedido> detallesCompletos = new ArrayList<>();
        
        for (DetallePedidoDTO dto : detalles) {
            // Validar producto
            Producto producto = productoService.obtenerProductoPorId(dto.getIdProducto());
            if (producto == null) {
                mostrarError("Producto no encontrado: " + dto.getIdProducto());
                return false;
            }
            
            // Validar cantidad
            if (dto.getCantidad() <= 0) {
                mostrarError("La cantidad debe ser mayor a 0");
                return false;
            }
            
            // Verificar stock
            if (!productoService.verificarStock(dto.getIdProducto(), dto.getCantidad())) {
                mostrarError("Stock insuficiente para: " + producto.getNombre());
                return false;
            }
            
            // Crear detalle
            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(producto);
            detalle.setCantidad(dto.getCantidad());
            detalle.setSubtotal(producto.getPrecio() * dto.getCantidad());
            
            detallesCompletos.add(detalle);
        }
        
        // Registrar el pedido completo
        boolean resultado = pedidoService.registrarPedido(pedido, detallesCompletos);
        
        if (resultado) {
            mostrarMensaje("Pedido registrado exitosamente con ID: " + pedido.getIdPedido());
            mostrarMensaje("Factura generada automáticamente");
        } else {
            mostrarError("Error al registrar el pedido");
        }
        
        return resultado;
    }
    
    /**
     * Lista todos los pedidos
     */
    public List<Pedido> listarPedidos() {
        List<Pedido> pedidos = pedidoService.listarPedidos();
        
        if (pedidos.isEmpty()) {
            mostrarMensaje("No hay pedidos registrados");
        } else {
            mostrarMensaje("Se encontraron " + pedidos.size() + " pedidos");
        }
        
        return pedidos;
    }
    
    /**
     * Obtiene los detalles de un pedido
     */
    public List<DetallePedido> obtenerDetallesPedido(int idPedido) {
        if (idPedido <= 0) {
            mostrarError("ID de pedido inválido");
            return new ArrayList<>();
        }
        
        return pedidoService.obtenerDetallesPedido(idPedido);
    }
    
    /**
     * Actualiza el estado de un pedido
     */
    public boolean actualizarEstadoPedido(int idPedido, String nuevoEstado) {
        if (idPedido <= 0) {
            mostrarError("ID de pedido inválido");
            return false;
        }
        
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            mostrarError("El estado es obligatorio");
            return false;
        }
        
        boolean resultado = pedidoService.actualizarEstadoPedido(idPedido, nuevoEstado);
        
        if (resultado) {
            mostrarMensaje("Estado del pedido actualizado a: " + nuevoEstado);
        } else {
            mostrarError("Error al actualizar el estado del pedido");
        }
        
        return resultado;
    }
    
    /**
     * Calcula el total de un pedido
     */
    public double calcularTotalPedido(int idPedido) {
        List<DetallePedido> detalles = obtenerDetallesPedido(idPedido);
        
        double total = 0;
        for (DetallePedido detalle : detalles) {
            total += detalle.getSubtotal();
        }
        
        return total;
    }
    
    /**
     * Obtiene información formateada de un pedido
     */
    public String obtenerInformacionPedido(int idPedido) {
        List<Pedido> pedidos = pedidoService.listarPedidos();
        Pedido pedido = pedidos.stream()
            .filter(p -> p.getIdPedido() == idPedido)
            .findFirst()
            .orElse(null);
        
        if (pedido == null) {
            return "Pedido no encontrado";
        }
        
        List<DetallePedido> detalles = obtenerDetallesPedido(idPedido);
        double total = calcularTotalPedido(idPedido);
        
        StringBuilder info = new StringBuilder();
        info.append("=== PEDIDO #").append(pedido.getIdPedido()).append(" ===\n");
        info.append("Cliente: ").append(pedido.getCliente().getRazonSocial()).append("\n");
        info.append("Empleado: ").append(pedido.getEmpleado().getNombre()).append("\n");
        info.append("Fecha: ").append(pedido.getFecha()).append("\n");
        info.append("Estado: ").append(pedido.getEstadoPedido()).append("\n\n");
        
        info.append("Detalles:\n");
        for (DetallePedido detalle : detalles) {
            info.append(String.format("- %s | Cantidad: %d | Subtotal: S/ %.2f\n",
                detalle.getProducto().getNombre(),
                detalle.getCantidad(),
                detalle.getSubtotal()));
        }
        
        info.append(String.format("\nTOTAL: S/ %.2f", total));
        
        return info.toString();
    }
    
    // Clase interna para transferencia de datos de detalle de pedido
    public static class DetallePedidoDTO {
        private int idProducto;
        private int cantidad;
        
        public DetallePedidoDTO(int idProducto, int cantidad) {
            this.idProducto = idProducto;
            this.cantidad = cantidad;
        }
        
        public int getIdProducto() { return idProducto; }
        public int getCantidad() { return cantidad; }
    }
    
    // Métodos auxiliares
    private void mostrarMensaje(String mensaje) {
        System.out.println("✓ " + mensaje);
    }
    
    private void mostrarError(String mensaje) {
        System.err.println("✗ " + mensaje);
    }
}