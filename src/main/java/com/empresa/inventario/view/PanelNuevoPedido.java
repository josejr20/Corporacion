package com.empresa.inventario.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import com.empresa.inventario.controller.ClienteController;
import com.empresa.inventario.controller.PedidoController;
import com.empresa.inventario.controller.ProductoController;
import com.empresa.inventario.model.Cliente;
import com.empresa.inventario.model.Producto;
import com.empresa.inventario.model.Usuario;

/**
 * Panel para registrar nuevos pedidos
 */
public class PanelNuevoPedido extends JPanel {
    
    private final PedidoController pedidoController;
    private final ClienteController clienteController;
    private final ProductoController productoController;
    private final Usuario usuarioActual;
    
    // Componentes
    private JComboBox<ClienteItem> cmbClientes;
    private JComboBox<ProductoItem> cmbProductos;
    private JSpinner spnCantidad;
    private JTable tablaDetalle;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotal;
    private JButton btnAgregarProducto;
    private JButton btnQuitarProducto;
    private JButton btnRegistrarPedido;
    private JButton btnCancelar;
    private JTextArea txtInfoCliente;
    private JTextArea txtInfoProducto;
    
    private List<DetalleTemp> detallesTemporales;
    private double totalPedido;
    
    public PanelNuevoPedido(PedidoController pedidoController, ClienteController clienteController,
                           ProductoController productoController, Usuario usuarioActual) {
        this.pedidoController = pedidoController;
        this.clienteController = clienteController;
        this.productoController = productoController;
        this.usuarioActual = usuarioActual;
        this.detallesTemporales = new ArrayList<>();
        this.totalPedido = 0;
        
        initComponents();
        cargarClientes();
        cargarProductos();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior - Título
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTitulo.setBackground(Color.WHITE);
        JLabel lblTitulo = new JLabel("Registrar Nuevo Pedido");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(155, 89, 182));
        panelTitulo.add(lblTitulo);
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel principal con división
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        
        // Panel izquierdo - Selección
        JPanel panelIzquierdo = crearPanelSeleccion();
        splitPane.setLeftComponent(panelIzquierdo);
        
        // Panel derecho - Detalle del pedido
        JPanel panelDerecho = crearPanelDetalle();
        splitPane.setRightComponent(panelDerecho);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Panel inferior - Total y botones
        JPanel panelInferior = crearPanelInferior();
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelSeleccion() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Pedido"));
        
        // Sección Cliente
        JPanel panelCliente = new JPanel();
        panelCliente.setLayout(new BoxLayout(panelCliente, BoxLayout.Y_AXIS));
        panelCliente.setBackground(Color.WHITE);
        panelCliente.setBorder(BorderFactory.createTitledBorder("Seleccionar Cliente"));
        panelCliente.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        cmbClientes = new JComboBox<>();
        cmbClientes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        cmbClientes.addActionListener(e -> mostrarInfoCliente());
        panelCliente.add(cmbClientes);
        
        panelCliente.add(Box.createRigidArea(new Dimension(0, 10)));
        
        txtInfoCliente = new JTextArea(4, 30);
        txtInfoCliente.setEditable(false);
        txtInfoCliente.setBackground(new Color(245, 245, 245));
        txtInfoCliente.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane scrollCliente = new JScrollPane(txtInfoCliente);
        scrollCliente.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        panelCliente.add(scrollCliente);
        
        panel.add(panelCliente);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Sección Producto
        JPanel panelProducto = new JPanel();
        panelProducto.setLayout(new BoxLayout(panelProducto, BoxLayout.Y_AXIS));
        panelProducto.setBackground(Color.WHITE);
        panelProducto.setBorder(BorderFactory.createTitledBorder("Agregar Productos"));
        panelProducto.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblProducto = new JLabel("Producto:");
        lblProducto.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelProducto.add(lblProducto);
        
        cmbProductos = new JComboBox<>();
        cmbProductos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        cmbProductos.addActionListener(e -> mostrarInfoProducto());
        panelProducto.add(cmbProductos);
        
        panelProducto.add(Box.createRigidArea(new Dimension(0, 10)));
        
        txtInfoProducto = new JTextArea(3, 30);
        txtInfoProducto.setEditable(false);
        txtInfoProducto.setBackground(new Color(245, 245, 245));
        txtInfoProducto.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane scrollProducto = new JScrollPane(txtInfoProducto);
        scrollProducto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        panelProducto.add(scrollProducto);
        
        panelProducto.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JLabel lblCantidad = new JLabel("Cantidad:");
        lblCantidad.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelProducto.add(lblCantidad);
        
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 9999, 1);
        spnCantidad = new JSpinner(spinnerModel);
        spnCantidad.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panelProducto.add(spnCantidad);
        
        panelProducto.add(Box.createRigidArea(new Dimension(0, 15)));
        
        btnAgregarProducto = new JButton("Agregar al Pedido");
        btnAgregarProducto.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAgregarProducto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnAgregarProducto.setBackground(new Color(46, 204, 113));
        btnAgregarProducto.setForeground(Color.WHITE);
        btnAgregarProducto.setFocusPainted(false);
        btnAgregarProducto.addActionListener(e -> agregarProducto());
        panelProducto.add(btnAgregarProducto);
        
        panel.add(panelProducto);
        
        return panel;
    }
    
    private JPanel crearPanelDetalle() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Detalle del Pedido"));
        
        // Tabla de detalles
        String[] columnas = {"ID Producto", "Producto", "Precio Unit.", "Cantidad", "Subtotal"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaDetalle = new JTable(modeloTabla);
        tablaDetalle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaDetalle.getTableHeader().setReorderingAllowed(false);
        tablaDetalle.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(tablaDetalle);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotones.setBackground(Color.WHITE);
        
        btnQuitarProducto = new JButton("Quitar Producto Seleccionado");
        btnQuitarProducto.setBackground(new Color(231, 76, 60));
        btnQuitarProducto.setForeground(Color.WHITE);
        btnQuitarProducto.setFocusPainted(false);
        btnQuitarProducto.addActionListener(e -> quitarProducto());
        panelBotones.add(btnQuitarProducto);
        
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Panel izquierdo - Total
        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTotal.setBackground(Color.WHITE);
        
        JLabel lblTotalTexto = new JLabel("TOTAL: ");
        lblTotalTexto.setFont(new Font("Arial", Font.BOLD, 20));
        panelTotal.add(lblTotalTexto);
        
        lblTotal = new JLabel("S/ 0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 24));
        lblTotal.setForeground(new Color(46, 204, 113));
        panelTotal.add(lblTotal);
        
        panel.add(panelTotal, BorderLayout.WEST);
        
        // Panel derecho - Botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(Color.WHITE);
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(150, 40));
        btnCancelar.setBackground(new Color(149, 165, 166));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(e -> cancelarPedido());
        panelBotones.add(btnCancelar);
        
        btnRegistrarPedido = new JButton("Registrar Pedido");
        btnRegistrarPedido.setPreferredSize(new Dimension(150, 40));
        btnRegistrarPedido.setBackground(new Color(155, 89, 182));
        btnRegistrarPedido.setForeground(Color.WHITE);
        btnRegistrarPedido.setFocusPainted(false);
        btnRegistrarPedido.addActionListener(e -> registrarPedido());
        panelBotones.add(btnRegistrarPedido);
        
        panel.add(panelBotones, BorderLayout.EAST);
        
        return panel;
    }
    
    private void cargarClientes() {
        cmbClientes.removeAllItems();
        List<Cliente> clientes = clienteController.listarClientes();
        
        for (Cliente cliente : clientes) {
            cmbClientes.addItem(new ClienteItem(cliente));
        }
    }
    
    private void cargarProductos() {
        cmbProductos.removeAllItems();
        List<Producto> productos = productoController.listarProductos();
        
        for (Producto producto : productos) {
            if (producto.getStock() > 0) { // Solo productos con stock
                cmbProductos.addItem(new ProductoItem(producto));
            }
        }
    }
    
    private void mostrarInfoCliente() {
        ClienteItem item = (ClienteItem) cmbClientes.getSelectedItem();
        if (item != null) {
            Cliente cliente = item.getCliente();
            txtInfoCliente.setText(
                "RUC: " + cliente.getRuc() + "\n" +
                "Dirección: " + cliente.getDireccion() + "\n" +
                "Teléfono: " + cliente.getTelefono() + "\n" +
                "Email: " + cliente.getEmail()
            );
        }
    }
    
    private void mostrarInfoProducto() {
        ProductoItem item = (ProductoItem) cmbProductos.getSelectedItem();
        if (item != null) {
            Producto producto = item.getProducto();
            txtInfoProducto.setText(
                "Precio: S/ " + String.format("%.2f", producto.getPrecio()) + "\n" +
                "Stock Disponible: " + producto.getStock() + "\n" +
                "Descripción: " + producto.getDescripcion()
            );
        }
    }
    
    private void agregarProducto() {
        ProductoItem itemProducto = (ProductoItem) cmbProductos.getSelectedItem();
        
        if (itemProducto == null) {
            JOptionPane.showMessageDialog(this,
                "Seleccione un producto",
                "Producto No Seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Producto producto = itemProducto.getProducto();
        int cantidad = (int) spnCantidad.getValue();
        
        // Verificar stock
        if (cantidad > producto.getStock()) {
            JOptionPane.showMessageDialog(this,
                "Stock insuficiente. Disponible: " + producto.getStock(),
                "Stock Insuficiente",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Verificar si el producto ya está en la lista
        for (DetalleTemp detalle : detallesTemporales) {
            if (detalle.getIdProducto() == producto.getIdProducto()) {
                JOptionPane.showMessageDialog(this,
                    "El producto ya está en el pedido. Puede editarlo o quitarlo.",
                    "Producto Duplicado",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        // Agregar a la lista temporal
        double subtotal = producto.getPrecio() * cantidad;
        DetalleTemp detalle = new DetalleTemp(
            producto.getIdProducto(),
            producto.getNombre(),
            producto.getPrecio(),
            cantidad,
            subtotal
        );
        detallesTemporales.add(detalle);
        
        // Agregar a la tabla
        Object[] fila = {
            producto.getIdProducto(),
            producto.getNombre(),
            String.format("S/ %.2f", producto.getPrecio()),
            cantidad,
            String.format("S/ %.2f", subtotal)
        };
        modeloTabla.addRow(fila);
        
        // Actualizar total
        totalPedido += subtotal;
        lblTotal.setText(String.format("S/ %.2f", totalPedido));
        
        // Limpiar selección
        spnCantidad.setValue(1);
        
        JOptionPane.showMessageDialog(this,
            "Producto agregado al pedido",
            "Producto Agregado",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void quitarProducto() {
        int filaSeleccionada = tablaDetalle.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccione un producto de la tabla",
                "Producto No Seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Obtener el detalle temporal
        DetalleTemp detalle = detallesTemporales.get(filaSeleccionada);
        
        // Actualizar total
        totalPedido -= detalle.getSubtotal();
        lblTotal.setText(String.format("S/ %.2f", totalPedido));
        
        // Quitar de la lista y tabla
        detallesTemporales.remove(filaSeleccionada);
        modeloTabla.removeRow(filaSeleccionada);
        
        JOptionPane.showMessageDialog(this,
            "Producto quitado del pedido",
            "Producto Eliminado",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void registrarPedido() {
        // Validaciones
        ClienteItem itemCliente = (ClienteItem) cmbClientes.getSelectedItem();
        if (itemCliente == null) {
            JOptionPane.showMessageDialog(this,
                "Seleccione un cliente",
                "Cliente No Seleccionado",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (detallesTemporales.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Agregue al menos un producto al pedido",
                "Pedido Vacío",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Confirmar registro
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de registrar este pedido?\n" +
            "Total: S/ " + String.format("%.2f", totalPedido),
            "Confirmar Registro",
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Preparar datos para el controlador
        Cliente cliente = itemCliente.getCliente();
        List<PedidoController.DetallePedidoDTO> detalles = new ArrayList<>();
        
        for (DetalleTemp detalle : detallesTemporales) {
            detalles.add(new PedidoController.DetallePedidoDTO(
                detalle.getIdProducto(),
                detalle.getCantidad()
            ));
        }
        
        // Registrar el pedido
        boolean resultado = pedidoController.registrarPedido(
            cliente.getIdCliente(),
            usuarioActual.getIdEmpleado(),
            detalles
        );
        
        if (resultado) {
            JOptionPane.showMessageDialog(this,
                "Pedido registrado exitosamente\n" +
                "Se generó la factura automáticamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this,
                "Error al registrar el pedido",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cancelarPedido() {
        if (!detallesTemporales.isEmpty()) {
            int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de cancelar? Se perderán los datos.",
                "Confirmar Cancelación",
                JOptionPane.YES_NO_OPTION);
            
            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        limpiarFormulario();
    }
    
    private void limpiarFormulario() {
        detallesTemporales.clear();
        modeloTabla.setRowCount(0);
        totalPedido = 0;
        lblTotal.setText("S/ 0.00");
        spnCantidad.setValue(1);
        txtInfoCliente.setText("");
        txtInfoProducto.setText("");
        
        if (cmbClientes.getItemCount() > 0) {
            cmbClientes.setSelectedIndex(0);
        }
        if (cmbProductos.getItemCount() > 0) {
            cmbProductos.setSelectedIndex(0);
        }
    }
    
    // Clases internas para ComboBox
    private static class ClienteItem {
        private final Cliente cliente;
        
        public ClienteItem(Cliente cliente) {
            this.cliente = cliente;
        }
        
        public Cliente getCliente() {
            return cliente;
        }
        
        @Override
        public String toString() {
            return cliente.getRazonSocial() + " - " + cliente.getRuc();
        }
    }
    
    private static class ProductoItem {
        private final Producto producto;
        
        public ProductoItem(Producto producto) {
            this.producto = producto;
        }
        
        public Producto getProducto() {
            return producto;
        }
        
        @Override
        public String toString() {
            return producto.getNombre() + " - Stock: " + producto.getStock();
        }
    }
    
    // Clase para detalles temporales
    private static class DetalleTemp {
        private final int idProducto;
        private final String nombreProducto;
        private final double precioUnitario;
        private final int cantidad;
        private final double subtotal;
        
        public DetalleTemp(int idProducto, String nombreProducto, double precioUnitario,
                          int cantidad, double subtotal) {
            this.idProducto = idProducto;
            this.nombreProducto = nombreProducto;
            this.precioUnitario = precioUnitario;
            this.cantidad = cantidad;
            this.subtotal = subtotal;
        }
        
        public int getIdProducto() { return idProducto; }
        public String getNombreProducto() { return nombreProducto; }
        public double getPrecioUnitario() { return precioUnitario; }
        public int getCantidad() { return cantidad; }
        public double getSubtotal() { return subtotal; }
    }
}