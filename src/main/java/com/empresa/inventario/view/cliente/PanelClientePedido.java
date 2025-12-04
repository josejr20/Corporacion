package com.empresa.inventario.view.cliente;

import com.empresa.inventario.controller.PedidoController;
import com.empresa.inventario.controller.ProductoController;
import com.empresa.inventario.model.Producto;
import com.empresa.inventario.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel para que los CLIENTES realicen sus pedidos
 * Estado inicial: Registrado -> PendienteComercial
 */
public class PanelClientePedido extends JPanel {
    
    private final ProductoController productoController;
    private final PedidoController pedidoController;
    private final Usuario usuarioCliente;
    
    // Componentes UI
    private JTable tablaProductosDisponibles;
    private DefaultTableModel modeloProductos;
    private JTable tablaCarrito;
    private DefaultTableModel modeloCarrito;
    private JLabel lblTotal;
    private JSpinner spnCantidad;
    private JTextArea txtDireccionEntrega;
    private JTextArea txtObservaciones;
    private JButton btnAgregarCarrito;
    private JButton btnQuitarCarrito;
    private JButton btnRealizarPedido;
    private JButton btnLimpiarCarrito;
    private JComboBox<String> cmbCategoria;
    
    private List<ItemCarrito> carrito;
    private double totalPedido;
    
    public PanelClientePedido(ProductoController productoController, 
                             PedidoController pedidoController,
                             Usuario usuarioCliente) {
        this.productoController = productoController;
        this.pedidoController = pedidoController;
        this.usuarioCliente = usuarioCliente;
        this.carrito = new ArrayList<>();
        this.totalPedido = 0;
        
        initComponents();
        cargarProductos();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel superior - Bienvenida
        add(crearPanelSuperior(), BorderLayout.NORTH);
        
        // Panel central - Split: Productos y Carrito
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setLeftComponent(crearPanelProductos());
        splitPane.setRightComponent(crearPanelCarrito());
        add(splitPane, BorderLayout.CENTER);
        
        // Panel inferior - Total y acciones
        add(crearPanelInferior(), BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 102, 204));
        panel.setPreferredSize(new Dimension(0, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel lblTitulo = new JLabel("🛒 Realizar Nuevo Pedido");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        panel.add(lblTitulo, BorderLayout.WEST);
        
        JLabel lblUsuario = new JLabel("Cliente: " + usuarioCliente.getUsername());
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 16));
        lblUsuario.setForeground(Color.WHITE);
        panel.add(lblUsuario, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
            "Productos Disponibles",
            0, 0, new Font("Arial", Font.BOLD, 14), new Color(0, 102, 204)
        ));
        
        // Panel de filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltros.setBackground(Color.WHITE);
        
        JLabel lblCategoria = new JLabel("Categoría:");
        lblCategoria.setFont(new Font("Arial", Font.BOLD, 12));
        panelFiltros.add(lblCategoria);
        
        cmbCategoria = new JComboBox<>(new String[]{
            "Todas", "Descartable", "Biodegradable", "Condimento", "Otro"
        });
        cmbCategoria.addActionListener(e -> filtrarProductos());
        panelFiltros.add(cmbCategoria);
        
        JButton btnActualizar = new JButton("🔄 Actualizar");
        btnActualizar.setBackground(new Color(52, 152, 219));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        btnActualizar.addActionListener(e -> cargarProductos());
        panelFiltros.add(btnActualizar);
        
        panel.add(panelFiltros, BorderLayout.NORTH);
        
        // Tabla de productos
        String[] columnas = {"ID", "Producto", "Categoría", "Precio", "Stock"};
        modeloProductos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaProductosDisponibles = new JTable(modeloProductos);
        tablaProductosDisponibles.setRowHeight(30);
        tablaProductosDisponibles.getColumnModel().getColumn(1).setPreferredWidth(200);
        
        JScrollPane scrollProductos = new JScrollPane(tablaProductosDisponibles);
        panel.add(scrollProductos, BorderLayout.CENTER);
        
        // Panel agregar al carrito
        JPanel panelAgregar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAgregar.setBackground(Color.WHITE);
        
        JLabel lblCantidad = new JLabel("Cantidad:");
        lblCantidad.setFont(new Font("Arial", Font.BOLD, 12));
        panelAgregar.add(lblCantidad);
        
        spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        spnCantidad.setPreferredSize(new Dimension(80, 30));
        panelAgregar.add(spnCantidad);
        
        btnAgregarCarrito = new JButton("➕ Agregar al Carrito");
        btnAgregarCarrito.setBackground(new Color(46, 204, 113));
        btnAgregarCarrito.setForeground(Color.WHITE);
        btnAgregarCarrito.setFont(new Font("Arial", Font.BOLD, 13));
        btnAgregarCarrito.setFocusPainted(false);
        btnAgregarCarrito.addActionListener(e -> agregarAlCarrito());
        panelAgregar.add(btnAgregarCarrito);
        
        panel.add(panelAgregar, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelCarrito() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
            "Mi Carrito de Compras",
            0, 0, new Font("Arial", Font.BOLD, 14), new Color(46, 204, 113)
        ));
        
        // Tabla carrito
        String[] columnas = {"ID", "Producto", "Precio Unit.", "Cant.", "Subtotal"};
        modeloCarrito = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaCarrito = new JTable(modeloCarrito);
        tablaCarrito.setRowHeight(30);
        
        JScrollPane scrollCarrito = new JScrollPane(tablaCarrito);
        panel.add(scrollCarrito, BorderLayout.CENTER);
        
        // Panel acciones carrito
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAcciones.setBackground(Color.WHITE);
        
        btnQuitarCarrito = new JButton("🗑️ Quitar Seleccionado");
        btnQuitarCarrito.setBackground(new Color(231, 76, 60));
        btnQuitarCarrito.setForeground(Color.WHITE);
        btnQuitarCarrito.setFocusPainted(false);
        btnQuitarCarrito.addActionListener(e -> quitarDelCarrito());
        panelAcciones.add(btnQuitarCarrito);
        
        btnLimpiarCarrito = new JButton("🧹 Limpiar Carrito");
        btnLimpiarCarrito.setBackground(new Color(149, 165, 166));
        btnLimpiarCarrito.setForeground(Color.WHITE);
        btnLimpiarCarrito.setFocusPainted(false);
        btnLimpiarCarrito.addActionListener(e -> limpiarCarrito());
        panelAcciones.add(btnLimpiarCarrito);
        
        panel.add(panelAcciones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Panel izquierdo - Datos adicionales
        JPanel panelDatos = new JPanel();
        panelDatos.setLayout(new BoxLayout(panelDatos, BoxLayout.Y_AXIS));
        panelDatos.setBackground(Color.WHITE);
        panelDatos.setBorder(BorderFactory.createTitledBorder("Datos de Entrega"));
        panelDatos.setPreferredSize(new Dimension(400, 0));
        
        JLabel lblDireccion = new JLabel("Dirección de entrega:");
        panelDatos.add(lblDireccion);
        
        txtDireccionEntrega = new JTextArea(2, 30);
        txtDireccionEntrega.setLineWrap(true);
        txtDireccionEntrega.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panelDatos.add(new JScrollPane(txtDireccionEntrega));
        
        panelDatos.add(Box.createVerticalStrut(10));
        
        JLabel lblObs = new JLabel("Observaciones (opcional):");
        panelDatos.add(lblObs);
        
        txtObservaciones = new JTextArea(2, 30);
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panelDatos.add(new JScrollPane(txtObservaciones));
        
        panel.add(panelDatos, BorderLayout.WEST);
        
        // Panel derecho - Total y botón
        JPanel panelAccion = new JPanel();
        panelAccion.setLayout(new BoxLayout(panelAccion, BoxLayout.Y_AXIS));
        panelAccion.setBackground(Color.WHITE);
        panelAccion.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblTotalTexto = new JLabel("TOTAL A PAGAR:");
        lblTotalTexto.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotalTexto.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelAccion.add(lblTotalTexto);
        
        panelAccion.add(Box.createVerticalStrut(10));
        
        lblTotal = new JLabel("S/ 0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 36));
        lblTotal.setForeground(new Color(46, 204, 113));
        lblTotal.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelAccion.add(lblTotal);
        
        panelAccion.add(Box.createVerticalStrut(20));
        
        btnRealizarPedido = new JButton("✅ REALIZAR PEDIDO");
        btnRealizarPedido.setFont(new Font("Arial", Font.BOLD, 16));
        btnRealizarPedido.setPreferredSize(new Dimension(250, 50));
        btnRealizarPedido.setMaximumSize(new Dimension(250, 50));
        btnRealizarPedido.setBackground(new Color(0, 102, 204));
        btnRealizarPedido.setForeground(Color.WHITE);
        btnRealizarPedido.setFocusPainted(false);
        btnRealizarPedido.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRealizarPedido.addActionListener(e -> confirmarPedido());
        panelAccion.add(btnRealizarPedido);
        
        panel.add(panelAccion, BorderLayout.EAST);
        
        return panel;
    }
    
    private void cargarProductos() {
        modeloProductos.setRowCount(0);
        List<Producto> productos = productoController.listarProductos();
        
        for (Producto p : productos) {
            if (p.getStock() > 0) { // Solo productos con stock
                modeloProductos.addRow(new Object[]{
                    p.getIdProducto(),
                    p.getNombre(),
                    p.getDescripcion() != null ? p.getDescripcion() : "General",
                    String.format("S/ %.2f", p.getPrecio()),
                    p.getStock()
                });
            }
        }
    }
    
    private void filtrarProductos() {
        String categoriaSeleccionada = (String) cmbCategoria.getSelectedItem();
        modeloProductos.setRowCount(0);
        List<Producto> productos = productoController.listarProductos();
        
        for (Producto p : productos) {
            if (p.getStock() > 0) {
                String categoria = p.getDescripcion() != null ? p.getDescripcion() : "General";
                if ("Todas".equals(categoriaSeleccionada) || categoria.contains(categoriaSeleccionada)) {
                    modeloProductos.addRow(new Object[]{
                        p.getIdProducto(),
                        p.getNombre(),
                        categoria,
                        String.format("S/ %.2f", p.getPrecio()),
                        p.getStock()
                    });
                }
            }
        }
    }
    
    private void agregarAlCarrito() {
        int filaSeleccionada = tablaProductosDisponibles.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor seleccione un producto",
                "Producto No Seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idProducto = (int) modeloProductos.getValueAt(filaSeleccionada, 0);
        String nombreProducto = (String) modeloProductos.getValueAt(filaSeleccionada, 1);
        String precioStr = ((String) modeloProductos.getValueAt(filaSeleccionada, 3)).replace("S/ ", "");
        double precio = Double.parseDouble(precioStr);
        int stockDisponible = (int) modeloProductos.getValueAt(filaSeleccionada, 4);
        int cantidad = (int) spnCantidad.getValue();
        
        // Validar stock
        if (cantidad > stockDisponible) {
            JOptionPane.showMessageDialog(this,
                "Stock insuficiente. Disponible: " + stockDisponible,
                "Stock Insuficiente",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Verificar si ya existe en el carrito
        for (ItemCarrito item : carrito) {
            if (item.getIdProducto() == idProducto) {
                JOptionPane.showMessageDialog(this,
                    "El producto ya está en el carrito",
                    "Producto Duplicado",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        // Agregar al carrito
        double subtotal = precio * cantidad;
        ItemCarrito item = new ItemCarrito(idProducto, nombreProducto, precio, cantidad, subtotal);
        carrito.add(item);
        
        // Actualizar tabla carrito
        modeloCarrito.addRow(new Object[]{
            idProducto,
            nombreProducto,
            String.format("S/ %.2f", precio),
            cantidad,
            String.format("S/ %.2f", subtotal)
        });
        
        // Actualizar total
        totalPedido += subtotal;
        lblTotal.setText(String.format("S/ %.2f", totalPedido));
        
        spnCantidad.setValue(1);
        
        JOptionPane.showMessageDialog(this,
            "✅ Producto agregado al carrito",
            "Éxito",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void quitarDelCarrito() {
        int filaSeleccionada = tablaCarrito.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccione un producto del carrito",
                "Sin Selección",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        ItemCarrito item = carrito.get(filaSeleccionada);
        totalPedido -= item.getSubtotal();
        lblTotal.setText(String.format("S/ %.2f", totalPedido));
        
        carrito.remove(filaSeleccionada);
        modeloCarrito.removeRow(filaSeleccionada);
    }
    
    private void limpiarCarrito() {
        if (carrito.isEmpty()) {
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de limpiar el carrito?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            carrito.clear();
            modeloCarrito.setRowCount(0);
            totalPedido = 0;
            lblTotal.setText("S/ 0.00");
        }
    }
    
    private void confirmarPedido() {
        // Validaciones
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El carrito está vacío",
                "Carrito Vacío",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String direccion = txtDireccionEntrega.getText().trim();
        if (direccion.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor ingrese la dirección de entrega",
                "Dirección Requerida",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Confirmar pedido
        String mensaje = String.format(
            "¿Confirmar pedido?\n\n" +
            "Total de productos: %d\n" +
            "Monto total: S/ %.2f\n" +
            "Dirección: %s",
            carrito.size(),
            totalPedido,
            direccion
        );
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            mensaje,
            "Confirmar Pedido",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            realizarPedido();
        }
    }
    
    private void realizarPedido() {
        // Convertir carrito a DTO
        List<PedidoController.DetallePedidoDTO> detalles = new ArrayList<>();
        for (ItemCarrito item : carrito) {
            detalles.add(new PedidoController.DetallePedidoDTO(
                item.getIdProducto(),
                item.getCantidad()
            ));
        }
        
        // TODO: Obtener idCliente del usuario actual
        int idCliente = 1; // Temporal - debe obtenerse del usuario logueado
        
        // Registrar pedido
        boolean resultado = pedidoController.registrarPedido(
            idCliente,
            usuarioCliente.getIdEmpleado(),
            detalles
        );
        
        if (resultado) {
            JOptionPane.showMessageDialog(this,
                "✅ ¡Pedido realizado exitosamente!\n\n" +
                "Su pedido será validado por el área comercial.\n" +
                "Recibirá una confirmación pronto.",
                "Pedido Registrado",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Limpiar formulario
            limpiarCarrito();
            txtDireccionEntrega.setText("");
            txtObservaciones.setText("");
            
        } else {
            JOptionPane.showMessageDialog(this,
                "❌ Error al realizar el pedido.\n" +
                "Por favor intente nuevamente.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Clase interna para items del carrito
    @lombok.Data
    @lombok.AllArgsConstructor
    private static class ItemCarrito {
        private int idProducto;
        private String nombreProducto;
        private double precioUnitario;
        private int cantidad;
        private double subtotal;
    }
}