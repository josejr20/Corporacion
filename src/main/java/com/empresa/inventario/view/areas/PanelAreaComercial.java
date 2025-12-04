package com.empresa.inventario.view.areas;

import com.empresa.inventario.controller.EstadoPedidoController;
import com.empresa.inventario.controller.PedidoController;
import com.empresa.inventario.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Panel para que el ÁREA COMERCIAL valide pedidos registrados
 * Estados: Registrado/PendienteComercial -> AprobadoComercial o RechazadoComercial
 */
public class PanelAreaComercial extends JPanel {
    
    private final EstadoPedidoController estadoController;
    private final PedidoController pedidoController;
    private final Usuario usuarioActual;
    
    // Componentes UI
    private JTable tablaPedidosPendientes;
    private DefaultTableModel modeloPendientes;
    private JTable tablaDetallePedido;
    private DefaultTableModel modeloDetalle;
    private JTextArea txtInfoCliente;
    private JTextArea txtObservaciones;
    private JLabel lblTotalPedido;
    private JLabel lblEstadisticas;
    private JButton btnAprobar;
    private JButton btnRechazar;
    private JButton btnActualizar;
    
    private Pedido pedidoSeleccionado;
    
    public PanelAreaComercial(EstadoPedidoController estadoController,
                             PedidoController pedidoController,
                             Usuario usuarioActual) {
        this.estadoController = estadoController;
        this.pedidoController = pedidoController;
        this.usuarioActual = usuarioActual;
        this.pedidoSeleccionado = null;
        
        initComponents();
        cargarPedidosPendientes();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel superior
        add(crearPanelSuperior(), BorderLayout.NORTH);
        
        // Panel central - Split vertical
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(250);
        splitPane.setTopComponent(crearPanelPedidosPendientes());
        splitPane.setBottomComponent(crearPanelDetalle());
        add(splitPane, BorderLayout.CENTER);
        
        // Panel inferior - Acciones
        add(crearPanelAcciones(), BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(41, 128, 185));
        panel.setPreferredSize(new Dimension(0, 90));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.setOpaque(false);
        
        JLabel lblTitulo = new JLabel("📋 Validación Comercial de Pedidos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo);
        
        lblEstadisticas = new JLabel("Cargando...");
        lblEstadisticas.setFont(new Font("Arial", Font.PLAIN, 14));
        lblEstadisticas.setForeground(Color.WHITE);
        panelTitulo.add(lblEstadisticas);
        
        panel.add(panelTitulo, BorderLayout.WEST);
        
        // Botón actualizar
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setOpaque(false);
        
        btnActualizar = new JButton("🔄 Actualizar");
        btnActualizar.setFont(new Font("Arial", Font.BOLD, 14));
        btnActualizar.setBackground(Color.WHITE);
        btnActualizar.setForeground(new Color(41, 128, 185));
        btnActualizar.setFocusPainted(false);
        btnActualizar.addActionListener(e -> cargarPedidosPendientes());
        panelBotones.add(btnActualizar);
        
        panel.add(panelBotones, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel crearPanelPedidosPendientes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
            "Pedidos Pendientes de Validación",
            0, 0, new Font("Arial", Font.BOLD, 14), new Color(41, 128, 185)
        ));
        
        // Tabla de pedidos pendientes
        String[] columnas = {"ID", "Cliente", "RUC", "Fecha", "Estado", "Monto Total"};
        modeloPendientes = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaPedidosPendientes = new JTable(modeloPendientes);
        tablaPedidosPendientes.setRowHeight(30);
        tablaPedidosPendientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tablaPedidosPendientes.getSelectedRow();
                if (fila != -1) {
                    cargarDetallePedido(fila);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaPedidosPendientes);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelDetalle() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        
        // Panel izquierdo - Info del cliente
        JPanel panelInfoCliente = new JPanel(new BorderLayout(5, 5));
        panelInfoCliente.setBackground(Color.WHITE);
        panelInfoCliente.setBorder(BorderFactory.createTitledBorder("Información del Cliente"));
        panelInfoCliente.setPreferredSize(new Dimension(300, 0));
        
        txtInfoCliente = new JTextArea();
        txtInfoCliente.setEditable(false);
        txtInfoCliente.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtInfoCliente.setBackground(new Color(245, 245, 245));
        JScrollPane scrollInfo = new JScrollPane(txtInfoCliente);
        panelInfoCliente.add(scrollInfo, BorderLayout.CENTER);
        
        lblTotalPedido = new JLabel("TOTAL: S/ 0.00");
        lblTotalPedido.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotalPedido.setForeground(new Color(46, 204, 113));
        lblTotalPedido.setHorizontalAlignment(SwingConstants.CENTER);
        panelInfoCliente.add(lblTotalPedido, BorderLayout.SOUTH);
        
        panel.add(panelInfoCliente, BorderLayout.WEST);
        
        // Panel central - Detalle de productos
        JPanel panelDetalle = new JPanel(new BorderLayout(5, 5));
        panelDetalle.setBackground(Color.WHITE);
        panelDetalle.setBorder(BorderFactory.createTitledBorder("Detalle de Productos"));
        
        String[] columnasDetalle = {"Producto", "Precio Unit.", "Cantidad", "Subtotal"};
        modeloDetalle = new DefaultTableModel(columnasDetalle, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaDetallePedido = new JTable(modeloDetalle);
        tablaDetallePedido.setRowHeight(25);
        
        JScrollPane scrollDetalle = new JScrollPane(tablaDetallePedido);
        panelDetalle.add(scrollDetalle, BorderLayout.CENTER);
        
        panel.add(panelDetalle, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelAcciones() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Panel izquierdo - Observaciones
        JPanel panelObs = new JPanel(new BorderLayout(5, 5));
        panelObs.setBackground(Color.WHITE);
        panelObs.setBorder(BorderFactory.createTitledBorder("Observaciones"));
        panelObs.setPreferredSize(new Dimension(400, 0));
        
        txtObservaciones = new JTextArea(3, 30);
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        txtObservaciones.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane scrollObs = new JScrollPane(txtObservaciones);
        panelObs.add(scrollObs, BorderLayout.CENTER);
        
        panel.add(panelObs, BorderLayout.WEST);
        
        // Panel derecho - Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelBotones.setBackground(Color.WHITE);
        
        btnRechazar = new JButton("❌ RECHAZAR PEDIDO");
        btnRechazar.setFont(new Font("Arial", Font.BOLD, 14));
        btnRechazar.setPreferredSize(new Dimension(200, 45));
        btnRechazar.setBackground(new Color(231, 76, 60));
        btnRechazar.setForeground(Color.WHITE);
        btnRechazar.setFocusPainted(false);
        btnRechazar.setEnabled(false);
        btnRechazar.addActionListener(e -> rechazarPedido());
        panelBotones.add(btnRechazar);
        
        btnAprobar = new JButton("✅ APROBAR PEDIDO");
        btnAprobar.setFont(new Font("Arial", Font.BOLD, 14));
        btnAprobar.setPreferredSize(new Dimension(200, 45));
        btnAprobar.setBackground(new Color(46, 204, 113));
        btnAprobar.setForeground(Color.WHITE);
        btnAprobar.setFocusPainted(false);
        btnAprobar.setEnabled(false);
        btnAprobar.addActionListener(e -> aprobarPedido());
        panelBotones.add(btnAprobar);
        
        panel.add(panelBotones, BorderLayout.EAST);
        
        return panel;
    }
    
    private void cargarPedidosPendientes() {
        modeloPendientes.setRowCount(0);
        List<Pedido> pedidos = estadoController.obtenerPedidosPendientesComercial();
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        for (Pedido pedido : pedidos) {
            modeloPendientes.addRow(new Object[]{
                pedido.getIdPedido(),
                pedido.getCliente().getRazonSocial(),
                pedido.getCliente().getRuc(),
                sdf.format(pedido.getFecha()),
                pedido.getEstadoPedido(),
                String.format("S/ %.2f", pedido.getMontoTotal())
            });
        }
        
        // Actualizar estadísticas
        lblEstadisticas.setText(String.format(
            "📊 Pedidos pendientes: %d | Usuario: %s",
            pedidos.size(),
            usuarioActual.getUsername()
        ));
        
        // Limpiar detalle
        limpiarDetalle();
    }
    
    private void cargarDetallePedido(int fila) {
        int idPedido = (int) modeloPendientes.getValueAt(fila, 0);
        
        // Obtener pedido completo
        List<Pedido> pedidos = estadoController.obtenerPedidosPendientesComercial();
        pedidoSeleccionado = pedidos.stream()
            .filter(p -> p.getIdPedido() == idPedido)
            .findFirst()
            .orElse(null);
        
        if (pedidoSeleccionado == null) {
            return;
        }
        
        // Cargar información del cliente
        Cliente cliente = pedidoSeleccionado.getCliente();
        txtInfoCliente.setText(String.format(
            "Razón Social:\n%s\n\n" +
            "RUC: %s\n\n" +
            "Dirección:\n%s\n\n" +
            "Teléfono: %s\n\n" +
            "Email: %s",
            cliente.getRazonSocial(),
            cliente.getRuc(),
            cliente.getDireccion(),
            cliente.getTelefono(),
            cliente.getEmail()
        ));
        
        // Cargar detalle de productos
        modeloDetalle.setRowCount(0);
        List<DetallePedido> detalles = pedidoController.obtenerDetallesPedido(idPedido);
        
        double total = 0;
        for (DetallePedido detalle : detalles) {
            modeloDetalle.addRow(new Object[]{
                detalle.getProducto().getNombre(),
                String.format("S/ %.2f", detalle.getProducto().getPrecio()),
                detalle.getCantidad(),
                String.format("S/ %.2f", detalle.getSubtotal())
            });
            total += detalle.getSubtotal();
        }
        
        lblTotalPedido.setText(String.format("TOTAL: S/ %.2f", total));
        
        // Habilitar botones
        btnAprobar.setEnabled(true);
        btnRechazar.setEnabled(true);
    }
    
    private void aprobarPedido() {
        if (pedidoSeleccionado == null) {
            return;
        }
        
        String observaciones = txtObservaciones.getText().trim();
        if (observaciones.isEmpty()) {
            observaciones = "Aprobado sin observaciones";
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            String.format(
                "¿Aprobar pedido #%d?\n\n" +
                "Cliente: %s\n" +
                "Monto: S/ %.2f\n\n" +
                "El pedido será enviado al área administrativa.",
                pedidoSeleccionado.getIdPedido(),
                pedidoSeleccionado.getCliente().getRazonSocial(),
                pedidoSeleccionado.getMontoTotal()
            ),
            "Confirmar Aprobación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean resultado = estadoController.aprobarPedidoComercial(
                pedidoSeleccionado.getIdPedido(),
                usuarioActual.getIdEmpleado(),
                observaciones
            );
            
            if (resultado) {
                JOptionPane.showMessageDialog(this,
                    "✅ Pedido aprobado exitosamente\n\n" +
                    "El pedido ha sido enviado al área administrativa para su validación.",
                    "Aprobación Exitosa",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                cargarPedidosPendientes();
                txtObservaciones.setText("");
            } else {
                JOptionPane.showMessageDialog(this,
                    "❌ Error al aprobar el pedido\n\n" +
                    "Por favor intente nuevamente.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    private void rechazarPedido() {
        if (pedidoSeleccionado == null) {
            return;
        }
        
        String motivo = JOptionPane.showInputDialog(this,
            "Ingrese el motivo del rechazo:",
            "Rechazar Pedido",
            JOptionPane.WARNING_MESSAGE
        );
        
        if (motivo == null || motivo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Debe especificar el motivo del rechazo",
                "Motivo Requerido",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            String.format(
                "¿Rechazar pedido #%d?\n\n" +
                "Cliente: %s\n" +
                "Motivo: %s\n\n" +
                "El cliente será notificado del rechazo.",
                pedidoSeleccionado.getIdPedido(),
                pedidoSeleccionado.getCliente().getRazonSocial(),
                motivo
            ),
            "Confirmar Rechazo",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean resultado = estadoController.rechazarPedidoComercial(
                pedidoSeleccionado.getIdPedido(),
                usuarioActual.getIdEmpleado(),
                motivo
            );
            
            if (resultado) {
                JOptionPane.showMessageDialog(this,
                    "❌ Pedido rechazado\n\n" +
                    "El cliente será notificado del rechazo.",
                    "Rechazo Registrado",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                cargarPedidosPendientes();
                txtObservaciones.setText("");
            } else {
                JOptionPane.showMessageDialog(this,
                    "❌ Error al rechazar el pedido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    private void limpiarDetalle() {
        pedidoSeleccionado = null;
        txtInfoCliente.setText("");
        txtObservaciones.setText("");
        modeloDetalle.setRowCount(0);
        lblTotalPedido.setText("TOTAL: S/ 0.00");
        btnAprobar.setEnabled(false);
        btnRechazar.setEnabled(false);
    }
}