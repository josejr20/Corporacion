package com.empresa.inventario.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.empresa.inventario.controller.ClienteController;
import com.empresa.inventario.model.Cliente;

/**
 * Panel para gestionar clientes
 */
public class PanelClientes extends JPanel {
    
    private final ClienteController clienteController;
    
    // Componentes
    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private JTextField txtRazonSocial;
    private JTextField txtRuc;
    private JTextField txtDireccion;
    private JTextField txtTelefono;
    private JTextField txtEmail;
    private JTextField txtBuscar;
    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnBuscar;
    
    private Cliente clienteSeleccionado;
    
    public PanelClientes(ClienteController clienteController) {
        this.clienteController = clienteController;
        this.clienteSeleccionado = null;
        initComponents();
        cargarClientes();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior - Título
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTitulo.setBackground(Color.WHITE);
        JLabel lblTitulo = new JLabel("Gestión de Clientes");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(0, 102, 204));
        panelTitulo.add(lblTitulo);
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel izquierdo - Formulario
        JPanel panelFormulario = crearPanelFormulario();
        add(panelFormulario, BorderLayout.WEST);
        
        // Panel central - Tabla
        JPanel panelTabla = crearPanelTabla();
        add(panelTabla, BorderLayout.CENTER);
    }
    
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(350, 0));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Cliente"));
        
        int y = 30;
        int labelX = 20;
        int fieldX = 20;
        int fieldWidth = 300;
        int fieldHeight = 30;
        int espaciado = 60;
        
        // Razón Social
        JLabel lblRazonSocial = new JLabel("Razón Social: *");
        lblRazonSocial.setBounds(labelX, y, 150, 25);
        panel.add(lblRazonSocial);
        
        txtRazonSocial = new JTextField();
        txtRazonSocial.setBounds(fieldX, y + 25, fieldWidth, fieldHeight);
        panel.add(txtRazonSocial);
        
        y += espaciado;
        
        // RUC
        JLabel lblRuc = new JLabel("RUC: *");
        lblRuc.setBounds(labelX, y, 150, 25);
        panel.add(lblRuc);
        
        txtRuc = new JTextField();
        txtRuc.setBounds(fieldX, y + 25, fieldWidth, fieldHeight);
        panel.add(txtRuc);
        
        y += espaciado;
        
        // Dirección
        JLabel lblDireccion = new JLabel("Dirección:");
        lblDireccion.setBounds(labelX, y, 150, 25);
        panel.add(lblDireccion);
        
        txtDireccion = new JTextField();
        txtDireccion.setBounds(fieldX, y + 25, fieldWidth, fieldHeight);
        panel.add(txtDireccion);
        
        y += espaciado;
        
        // Teléfono
        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setBounds(labelX, y, 150, 25);
        panel.add(lblTelefono);
        
        txtTelefono = new JTextField();
        txtTelefono.setBounds(fieldX, y + 25, fieldWidth, fieldHeight);
        panel.add(txtTelefono);
        
        y += espaciado;
        
        // Email
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(labelX, y, 150, 25);
        panel.add(lblEmail);
        
        txtEmail = new JTextField();
        txtEmail.setBounds(fieldX, y + 25, fieldWidth, fieldHeight);
        panel.add(txtEmail);
        
        y += espaciado + 20;
        
        // Botones
        btnNuevo = new JButton("Nuevo");
        btnNuevo.setBounds(20, y, 140, 35);
        btnNuevo.setBackground(new Color(52, 152, 219));
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFocusPainted(false);
        btnNuevo.addActionListener(e -> limpiarFormulario());
        panel.add(btnNuevo);
        
        btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(180, y, 140, 35);
        btnGuardar.setBackground(new Color(46, 204, 113));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.addActionListener(e -> guardarCliente());
        panel.add(btnGuardar);
        
        y += 45;
        
        btnActualizar = new JButton("Actualizar");
        btnActualizar.setBounds(20, y, 140, 35);
        btnActualizar.setBackground(new Color(241, 196, 15));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setEnabled(false);
        btnActualizar.addActionListener(e -> actualizarCliente());
        panel.add(btnActualizar);
        
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(180, y, 140, 35);
        btnEliminar.setBackground(new Color(231, 76, 60));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setEnabled(false);
        btnEliminar.addActionListener(e -> eliminarCliente());
        panel.add(btnEliminar);
        
        y += 45;
        
        btnLimpiar = new JButton("Limpiar Formulario");
        btnLimpiar.setBounds(20, y, 300, 35);
        btnLimpiar.setBackground(new Color(149, 165, 166));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        panel.add(btnLimpiar);
        
        return panel;
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Clientes"));
        
        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.setBackground(Color.WHITE);
        
        JLabel lblBuscar = new JLabel("Buscar por RUC:");
        panelBusqueda.add(lblBuscar);
        
        txtBuscar = new JTextField(20);
        panelBusqueda.add(txtBuscar);
        
        btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(new Color(52, 152, 219));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.addActionListener(e -> buscarCliente());
        panelBusqueda.add(btnBuscar);
        
        JButton btnMostrarTodos = new JButton("Mostrar Todos");
        btnMostrarTodos.setBackground(new Color(149, 165, 166));
        btnMostrarTodos.setForeground(Color.WHITE);
        btnMostrarTodos.setFocusPainted(false);
        btnMostrarTodos.addActionListener(e -> cargarClientes());
        panelBusqueda.add(btnMostrarTodos);
        
        panel.add(panelBusqueda, BorderLayout.NORTH);
        
        // Tabla
        String[] columnas = {"ID", "Razón Social", "RUC", "Dirección", "Teléfono", "Email"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaClientes.getTableHeader().setReorderingAllowed(false);
        tablaClientes.setRowHeight(25);
        
        // Listener para selección de fila
        tablaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int filaSeleccionada = tablaClientes.getSelectedRow();
                if (filaSeleccionada != -1) {
                    cargarClienteSeleccionado(filaSeleccionada);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaClientes);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void cargarClientes() {
        modeloTabla.setRowCount(0);
        List<Cliente> clientes = clienteController.listarClientes();
        
        for (Cliente cliente : clientes) {
            Object[] fila = {
                cliente.getIdCliente(),
                cliente.getRazonSocial(),
                cliente.getRuc(),
                cliente.getDireccion(),
                cliente.getTelefono(),
                cliente.getEmail()
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void buscarCliente() {
        String ruc = txtBuscar.getText().trim();
        if (ruc.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Ingrese un RUC para buscar",
                "Campo Vacío",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Cliente cliente = clienteController.buscarPorRuc(ruc);
        
        if (cliente != null) {
            modeloTabla.setRowCount(0);
            Object[] fila = {
                cliente.getIdCliente(),
                cliente.getRazonSocial(),
                cliente.getRuc(),
                cliente.getDireccion(),
                cliente.getTelefono(),
                cliente.getEmail()
            };
            modeloTabla.addRow(fila);
        } else {
            JOptionPane.showMessageDialog(this,
                "No se encontró cliente con el RUC: " + ruc,
                "Cliente No Encontrado",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void cargarClienteSeleccionado(int fila) {
        clienteSeleccionado = new Cliente();
        clienteSeleccionado.setIdCliente((int) modeloTabla.getValueAt(fila, 0));
        clienteSeleccionado.setRazonSocial((String) modeloTabla.getValueAt(fila, 1));
        clienteSeleccionado.setRuc((String) modeloTabla.getValueAt(fila, 2));
        clienteSeleccionado.setDireccion((String) modeloTabla.getValueAt(fila, 3));
        clienteSeleccionado.setTelefono((String) modeloTabla.getValueAt(fila, 4));
        clienteSeleccionado.setEmail((String) modeloTabla.getValueAt(fila, 5));
        
        // Cargar datos en el formulario
        txtRazonSocial.setText(clienteSeleccionado.getRazonSocial());
        txtRuc.setText(clienteSeleccionado.getRuc());
        txtDireccion.setText(clienteSeleccionado.getDireccion());
        txtTelefono.setText(clienteSeleccionado.getTelefono());
        txtEmail.setText(clienteSeleccionado.getEmail());
        
        // Habilitar botones de actualizar y eliminar
        btnActualizar.setEnabled(true);
        btnEliminar.setEnabled(true);
        btnGuardar.setEnabled(false);
    }
    
    private void guardarCliente() {
        if (!validarCampos()) {
            return;
        }
        
        boolean resultado = clienteController.registrarCliente(
            txtRazonSocial.getText().trim(),
            txtRuc.getText().trim(),
            txtDireccion.getText().trim(),
            txtTelefono.getText().trim(),
            txtEmail.getText().trim()
        );
        
        if (resultado) {
            JOptionPane.showMessageDialog(this,
                "Cliente registrado exitosamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarClientes();
        } else {
            JOptionPane.showMessageDialog(this,
                "Error al registrar el cliente",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarCliente() {
        if (clienteSeleccionado == null) {
            return;
        }
        
        if (!validarCampos()) {
            return;
        }
        
        boolean resultado = clienteController.actualizarCliente(
            clienteSeleccionado.getIdCliente(),
            txtRazonSocial.getText().trim(),
            txtRuc.getText().trim(),
            txtDireccion.getText().trim(),
            txtTelefono.getText().trim(),
            txtEmail.getText().trim()
        );
        
        if (resultado) {
            JOptionPane.showMessageDialog(this,
                "Cliente actualizado exitosamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarClientes();
        } else {
            JOptionPane.showMessageDialog(this,
                "Error al actualizar el cliente",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarCliente() {
        if (clienteSeleccionado == null) {
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea eliminar el cliente?\n" + 
            clienteSeleccionado.getRazonSocial(),
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean resultado = clienteController.eliminarCliente(clienteSeleccionado.getIdCliente());
            
            if (resultado) {
                JOptionPane.showMessageDialog(this,
                    "Cliente eliminado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarClientes();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar el cliente.\nPuede tener pedidos asociados.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void limpiarFormulario() {
        txtRazonSocial.setText("");
        txtRuc.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        txtBuscar.setText("");
        
        clienteSeleccionado = null;
        
        btnGuardar.setEnabled(true);
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
        
        tablaClientes.clearSelection();
    }
    
    private boolean validarCampos() {
        if (txtRazonSocial.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "La razón social es obligatoria",
                "Campo Requerido",
                JOptionPane.WARNING_MESSAGE);
            txtRazonSocial.requestFocus();
            return false;
        }
        
        if (txtRuc.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El RUC es obligatorio",
                "Campo Requerido",
                JOptionPane.WARNING_MESSAGE);
            txtRuc.requestFocus();
            return false;
        }
        
        if (!txtRuc.getText().trim().matches("\\d{11}")) {
            JOptionPane.showMessageDialog(this,
                "El RUC debe tener 11 dígitos",
                "RUC Inválido",
                JOptionPane.WARNING_MESSAGE);
            txtRuc.requestFocus();
            return false;
        }
        
        return true;
    }
}