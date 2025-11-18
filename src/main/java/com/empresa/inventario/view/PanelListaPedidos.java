package com.empresa.inventario.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.empresa.inventario.controller.PedidoController;
import com.empresa.inventario.model.DetallePedido;
import com.empresa.inventario.model.Pedido;

/**
 * Panel para listar y gestionar pedidos existentes
 */
public class PanelListaPedidos extends JPanel {
    
    private final PedidoController pedidoController;
    
    // Componentes
    private JTable tablaPedidos;
    private DefaultTableModel modeloTabla;
    private JTable tablaDetalle;
    private DefaultTableModel modeloDetalle;
    private JComboBox<String> cmbEstado;
    private JButton btnActualizarEstado;
    private JButton btnVerDetalle;
    private JButton btnActualizar;
    private JLabel lblTotalPedido;
    
    private Pedido pedidoSeleccionado;
    
    public PanelListaPedidos(PedidoController pedidoController) {
        this.pedidoController = pedidoController;
        this.pedidoSeleccionado = null;
        initComponents();
        cargarPedidos();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior - Título
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTitulo.setBackground(Color.WHITE);
        JLabel lblTitulo = new JLabel("Lista de Pedidos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(155, 89, 182));
        panelTitulo.add(lblTitulo);
        
        btnActualizar = new JButton("Actualizar Lista");
        btnActualizar.setBackground(new Color(52, 152, 219));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        btnActualizar.addActionListener(e -> cargarPedidos());
        panelTitulo.add(btnActualizar);
        
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central - División vertical
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300);
        
        // Panel superior - Tabla de pedidos
        JPanel panelSuperior = crearPanelPedidos();
        splitPane.setTopComponent(panelSuperior);
        
        // Panel inferior - Detalle del pedido
        JPanel panelInferior = crearPanelDetalle();
        splitPane.setBottomComponent(panelInferior);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private JPanel crearPanelPedidos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Pedidos Registrados"));
        
        // Tabla de pedidos
        String[] columnas = {"ID", "Cliente", "Empleado", "Fecha", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaPedidos = new JTable(modeloTabla);
        tablaPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPedidos.getTableHeader().setReorderingAllowed(false);
        tablaPedidos.setRowHeight(25);
        
        // Listener para selección
        tablaPedidos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int filaSeleccionada = tablaPedidos.getSelectedRow();
                if (filaSeleccionada != -1) {
                    cargarPedidoSeleccionado(filaSeleccionada);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaPedidos);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de acciones
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAcciones.setBackground(Color.WHITE);
        
        JLabel lblEstado = new JLabel("Cambiar Estado:");
        panelAcciones.add(lblEstado);
        
        cmbEstado = new JComboBox<>(new String[]{"Pendiente", "En Proceso", "Completado", "Cancelado"});
        panelAcciones.add(cmbEstado);
        
        btnActualizarEstado = new JButton("Actualizar Estado");
        btnActualizarEstado.setBackground(new Color(241, 196, 15));
        btnActualizarEstado.setForeground(Color.WHITE);
        btnActualizarEstado.setFocusPainted(false);
        btnActualizarEstado.setEnabled(false);
        btnActualizarEstado.addActionListener(e -> actualizarEstado());
        panelAcciones.add(btnActualizarEstado);
        
        btnVerDetalle = new JButton("Ver Detalle");
        btnVerDetalle.setBackground(new Color(52, 152, 219));
        btnVerDetalle.setForeground(Color.WHITE);
        btnVerDetalle.setFocusPainted(false);
        btnVerDetalle.setEnabled(false);
        btnVerDetalle.addActionListener(e -> cargarDetallePedido());
        panelAcciones.add(btnVerDetalle);
        
        panel.add(panelAcciones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelDetalle() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Detalle del Pedido"));
        
        // Tabla de detalles
        String[] columnas = {"Producto", "Precio Unit.", "Cantidad", "Subtotal"};
        modeloDetalle = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaDetalle = new JTable(modeloDetalle);
        tablaDetalle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaDetalle.getTableHeader().setReorderingAllowed(false);
        tablaDetalle.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(tablaDetalle);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de total
        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTotal.setBackground(Color.WHITE);
        
        JLabel lblTotalTexto = new JLabel("TOTAL DEL PEDIDO: ");
        lblTotalTexto.setFont(new Font("Arial", Font.BOLD, 16));
        panelTotal.add(lblTotalTexto);
        
        lblTotalPedido = new JLabel("S/ 0.00");
        lblTotalPedido.setFont(new Font("Arial", Font.BOLD, 20));
        lblTotalPedido.setForeground(new Color(46, 204, 113));
        panelTotal.add(lblTotalPedido);
        
        panel.add(panelTotal, BorderLayout.SOUTH);
        
        return panel;
    }

    private void cargarPedidos() {
        modeloTabla.setRowCount(0);
        List<Pedido> pedidos = pedidoController.listarPedidos();
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        for (Pedido pedido : pedidos) {
            Object[] fila = {
                pedido.getIdPedido(),
                pedido.getCliente().getRazonSocial(),
                pedido.getEmpleado().getNombre(),
                sdf.format(pedido.getFecha()),
                pedido.getEstadoPedido()
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void cargarPedidoSeleccionado(int fila) {
        List<Pedido> pedidos = pedidoController.listarPedidos();
        int idPedido = (int) modeloTabla.getValueAt(fila, 0);
        
        pedidoSeleccionado = pedidos.stream()
            .filter(p -> p.getIdPedido() == idPedido)
            .findFirst()
            .orElse(null);
        
        if (pedidoSeleccionado != null) {
            cmbEstado.setSelectedItem(pedidoSeleccionado.getEstadoPedido());
            btnActualizarEstado.setEnabled(true);
            btnVerDetalle.setEnabled(true);
            
            // Cargar automáticamente el detalle
            cargarDetallePedido();
        }
    }
    
    private void cargarDetallePedido() {
        if (pedidoSeleccionado == null) {
            return;
        }
        
        modeloDetalle.setRowCount(0);
        List<DetallePedido> detalles = pedidoController.obtenerDetallesPedido(
            pedidoSeleccionado.getIdPedido()
        );
        
        double total = 0;
        
        for (DetallePedido detalle : detalles) {
            Object[] fila = {
                detalle.getProducto().getNombre(),
                String.format("S/ %.2f", detalle.getProducto().getPrecio()),
                detalle.getCantidad(),
                String.format("S/ %.2f", detalle.getSubtotal())
            };
            modeloDetalle.addRow(fila);
            total += detalle.getSubtotal();
        }
        
        lblTotalPedido.setText(String.format("S/ %.2f", total));
    }
    
    private void actualizarEstado() {
        if (pedidoSeleccionado == null) {
            return;
        }
        
        String nuevoEstado = (String) cmbEstado.getSelectedItem();
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de cambiar el estado a: " + nuevoEstado + "?",
            "Confirmar Cambio de Estado",
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean resultado = pedidoController.actualizarEstadoPedido(
                pedidoSeleccionado.getIdPedido(),
                nuevoEstado
            );
            
            if (resultado) {
                JOptionPane.showMessageDialog(this,
                    "Estado actualizado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                cargarPedidos();
                pedidoSeleccionado = null;
                btnActualizarEstado.setEnabled(false);
                btnVerDetalle.setEnabled(false);
                modeloDetalle.setRowCount(0);
                lblTotalPedido.setText("S/ 0.00");
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al actualizar el estado",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}