package com.empresa.inventario.controller;

import com.empresa.inventario.service.PedidoService;
import com.empresa.inventario.service.ClienteService;
import com.empresa.inventario.service.ProductoService;
import com.empresa.inventario.model.Pedido;
import com.empresa.inventario.model.Cliente;
import com.empresa.inventario.model.Producto;
import com.empresa.inventario.model.DetallePedido;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Controlador para generar reportes del sistema
 */
public class ReporteController {
    
    private final PedidoService pedidoService;
    private final ClienteService clienteService;
    private final ProductoService productoService;
    
    public ReporteController() {
        this.pedidoService = new PedidoService();
        this.clienteService = new ClienteService();
        this.productoService = new ProductoService();
    }
    
    /**
     * Genera un reporte de ventas general
     */
    public String generarReporteVentas() {
        List<Pedido> pedidos = pedidoService.listarPedidos();
        
        if (pedidos.isEmpty()) {
            return "No hay pedidos registrados para generar reporte";
        }
        
        StringBuilder reporte = new StringBuilder();
        reporte.append("========================================\n");
        reporte.append("       REPORTE DE VENTAS GENERAL        \n");
        reporte.append("========================================\n\n");
        
        int totalPedidos = pedidos.size();
        double ventaTotal = 0;
        
        Map<String, Integer> pedidosPorEstado = new HashMap<>();
        
        for (Pedido pedido : pedidos) {
            List<DetallePedido> detalles = pedidoService.obtenerDetallesPedido(pedido.getIdPedido());
            double totalPedido = detalles.stream()
                .mapToDouble(DetallePedido::getSubtotal)
                .sum();
            
            ventaTotal += totalPedido;
            
            String estado = pedido.getEstadoPedido();
            pedidosPorEstado.put(estado, pedidosPorEstado.getOrDefault(estado, 0) + 1);
        }
        
        reporte.append("Total de Pedidos: ").append(totalPedidos).append("\n");
        reporte.append(String.format("Venta Total: S/ %.2f\n", ventaTotal));
        reporte.append(String.format("Promedio por Pedido: S/ %.2f\n\n", ventaTotal / totalPedidos));
        
        reporte.append("Pedidos por Estado:\n");
        pedidosPorEstado.forEach((estado, cantidad) -> 
            reporte.append(String.format("  - %s: %d\n", estado, cantidad))
        );
        
        reporte.append("\n========================================\n");
        
        return reporte.toString();
    }
    
    /**
     * Genera un reporte de clientes
     */
    public String generarReporteClientes() {
        List<Cliente> clientes = clienteService.listarTodosLosClientes();
        
        if (clientes.isEmpty()) {
            return "No hay clientes registrados para generar reporte";
        }
        
        StringBuilder reporte = new StringBuilder();
        reporte.append("========================================\n");
        reporte.append("        REPORTE DE CLIENTES             \n");
        reporte.append("========================================\n\n");
        
        reporte.append("Total de Clientes: ").append(clientes.size()).append("\n\n");
        
        reporte.append("Listado de Clientes:\n");
        for (Cliente cliente : clientes) {
            reporte.append(String.format("ID: %d | %s | RUC: %s\n",
                cliente.getIdCliente(),
                cliente.getRazonSocial(),
                cliente.getRuc()
            ));
        }
        
        reporte.append("\n========================================\n");
        
        return reporte.toString();
    }
    
    /**
     * Genera un reporte de inventario
     */
    public String generarReporteInventario() {
        List<Producto> productos = productoService.listarTodosLosProductos();
        
        if (productos.isEmpty()) {
            return "No hay productos registrados para generar reporte";
        }
        
        StringBuilder reporte = new StringBuilder();
        reporte.append("========================================\n");
        reporte.append("       REPORTE DE INVENTARIO            \n");
        reporte.append("========================================\n\n");
        
        int totalProductos = productos.size();
        int productosConStock = 0;
        int productosSinStock = 0;
        double valorInventario = 0;
        
        for (Producto producto : productos) {
            if (producto.getStock() > 0) {
                productosConStock++;
                valorInventario += producto.getPrecio() * producto.getStock();
            } else {
                productosSinStock++;
            }
        }
        
        reporte.append("Total de Productos: ").append(totalProductos).append("\n");
        reporte.append("Productos con Stock: ").append(productosConStock).append("\n");
        reporte.append("Productos sin Stock: ").append(productosSinStock).append("\n");
        reporte.append(String.format("Valor Total de Inventario: S/ %.2f\n\n", valorInventario));
        
        reporte.append("Detalle de Productos:\n");
        for (Producto producto : productos) {
            reporte.append(String.format("ID: %d | %s | Stock: %d | Precio: S/ %.2f\n",
                producto.getIdProducto(),
                producto.getNombre(),
                producto.getStock(),
                producto.getPrecio()
            ));
        }
        
        reporte.append("\n========================================\n");
        
        return reporte.toString();
    }
    
    /**
     * Genera un reporte de productos con stock bajo
     */
    public String generarReporteStockBajo(int umbral) {
        List<Producto> productos = productoService.listarTodosLosProductos();
        
        StringBuilder reporte = new StringBuilder();
        reporte.append("========================================\n");
        reporte.append("     REPORTE DE STOCK BAJO              \n");
        reporte.append("========================================\n\n");
        reporte.append("Umbral de Stock: ").append(umbral).append("\n\n");
        
        reporte.append("Productos con Stock Bajo:\n");
        
        int contador = 0;
        for (Producto producto : productos) {
            if (producto.getStock() <= umbral) {
                reporte.append(String.format("⚠ %s | Stock: %d | ID: %d\n",
                    producto.getNombre(),
                    producto.getStock(),
                    producto.getIdProducto()
                ));
                contador++;
            }
        }
        
        if (contador == 0) {
            reporte.append("No hay productos con stock bajo.\n");
        } else {
            reporte.append("\nTotal de productos con stock bajo: ").append(contador).append("\n");
        }
        
        reporte.append("\n========================================\n");
        
        return reporte.toString();
    }
    
    /**
     * Genera un reporte de pedidos por cliente
     */
    public String generarReportePedidosPorCliente(int idCliente) {
        Cliente cliente = clienteService.obtenerClientePorId(idCliente);
        
        if (cliente == null) {
            return "Cliente no encontrado";
        }
        
        List<Pedido> pedidos = pedidoService.listarPedidos();
        List<Pedido> pedidosCliente = pedidos.stream()
            .filter(p -> p.getCliente().getIdCliente() == idCliente)
            .toList();
        
        StringBuilder reporte = new StringBuilder();
        reporte.append("========================================\n");
        reporte.append("  REPORTE DE PEDIDOS POR CLIENTE        \n");
        reporte.append("========================================\n\n");
        
        reporte.append("Cliente: ").append(cliente.getRazonSocial()).append("\n");
        reporte.append("RUC: ").append(cliente.getRuc()).append("\n\n");
        
        if (pedidosCliente.isEmpty()) {
            reporte.append("Este cliente no tiene pedidos registrados.\n");
        } else {
            reporte.append("Total de Pedidos: ").append(pedidosCliente.size()).append("\n\n");
            
            double totalCompras = 0;
            
            for (Pedido pedido : pedidosCliente) {
                List<DetallePedido> detalles = pedidoService.obtenerDetallesPedido(pedido.getIdPedido());
                double totalPedido = detalles.stream()
                    .mapToDouble(DetallePedido::getSubtotal)
                    .sum();
                
                totalCompras += totalPedido;
                
                reporte.append(String.format("Pedido #%d | Fecha: %s | Total: S/ %.2f | Estado: %s\n",
                    pedido.getIdPedido(),
                    pedido.getFecha(),
                    totalPedido,
                    pedido.getEstadoPedido()
                ));
            }
            
            reporte.append(String.format("\nTotal de Compras: S/ %.2f\n", totalCompras));
        }
        
        reporte.append("\n========================================\n");
        
        return reporte.toString();
    }
    
    /**
     * Imprime un reporte en consola
     */
    public void imprimirReporte(String reporte) {
        System.out.println(reporte);
    }
}