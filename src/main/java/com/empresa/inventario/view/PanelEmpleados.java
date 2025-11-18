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

import com.empresa.inventario.controller.EmpleadoController;
import com.empresa.inventario.model.Empleado;

/**
 * Panel para gestionar empleados
 */
public class PanelEmpleados extends JPanel {
    
    private final EmpleadoController empleadoController;
    
    // Componentes
    private JTable tablaEmpleados;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombre;
    private JTextField txtCargo;
    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    
    private Empleado empleadoSeleccionado;
    
    public PanelEmpleados(EmpleadoController empleadoController) {
        this.empleadoController = empleadoController;
        this.empleadoSeleccionado = null;
        initComponents();
        cargarEmpleados();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior - Título
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTitulo.setBackground(Color.WHITE);
        JLabel lblTitulo = new JLabel("Gestión de Empleados");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(52, 73, 94));
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
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Empleado"));
        
        int y = 30;
        int labelX = 20;
        int fieldX = 20;
        int fieldWidth = 300;
        int fieldHeight = 30;
        int espaciado = 60;
        
        // Nombre
        JLabel lblNombre = new JLabel("Nombre Completo: *");
        lblNombre.setBounds(labelX, y, 150, 25);
        panel.add(lblNombre);
        
        txtNombre = new JTextField();
        txtNombre.setBounds(fieldX, y + 25, fieldWidth, fieldHeight);
        panel.add(txtNombre);
        
        y += espaciado;
        
        // Cargo
        JLabel lblCargo = new JLabel("Cargo: *");
        lblCargo.setBounds(labelX, y, 150, 25);
        panel.add(lblCargo);
        
        txtCargo = new JTextField();
        txtCargo.setBounds(fieldX, y + 25, fieldWidth, fieldHeight);
        panel.add(txtCargo);
        
        y += espaciado + 40;
        
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
        btnGuardar.addActionListener(e -> guardarEmpleado());
        panel.add(btnGuardar);
        
        y += 45;
        
        btnActualizar = new JButton("Actualizar");
        btnActualizar.setBounds(20, y, 140, 35);
        btnActualizar.setBackground(new Color(241, 196, 15));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setEnabled(false);
        btnActualizar.addActionListener(e -> actualizarEmpleado());
        panel.add(btnActualizar);
        
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(180, y, 140, 35);
        btnEliminar.setBackground(new Color(231, 76, 60));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setEnabled(false);
        btnEliminar.addActionListener(e -> eliminarEmpleado());
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
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Empleados"));
        
        // Tabla
        String[] columnas = {"ID", "Nombre", "Cargo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaEmpleados = new JTable(modeloTabla);
        tablaEmpleados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEmpleados.getTableHeader().setReorderingAllowed(false);
        tablaEmpleados.setRowHeight(25);
        
        // Listener para selección
        tablaEmpleados.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int filaSeleccionada = tablaEmpleados.getSelectedRow();
                if (filaSeleccionada != -1) {
                    cargarEmpleadoSeleccionado(filaSeleccionada);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaEmpleados);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void cargarEmpleados() {
        modeloTabla.setRowCount(0);
        List<Empleado> empleados = empleadoController.listarEmpleados();
        
        for (Empleado empleado : empleados) {
            Object[] fila = {
                empleado.getIdEmpleado(),
                empleado.getNombre(),
                empleado.getCargo()
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void cargarEmpleadoSeleccionado(int fila) {
        empleadoSeleccionado = new Empleado();
        empleadoSeleccionado.setIdEmpleado((int) modeloTabla.getValueAt(fila, 0));
        empleadoSeleccionado.setNombre((String) modeloTabla.getValueAt(fila, 1));
        empleadoSeleccionado.setCargo((String) modeloTabla.getValueAt(fila, 2));
        
        // Cargar datos en el formulario
        txtNombre.setText(empleadoSeleccionado.getNombre());
        txtCargo.setText(empleadoSeleccionado.getCargo());
        
        // Habilitar botones
        btnActualizar.setEnabled(true);
        btnEliminar.setEnabled(true);
        btnGuardar.setEnabled(false);
    }
    
    private void guardarEmpleado() {
        if (!validarCampos()) {
            return;
        }
        
        boolean resultado = empleadoController.registrarEmpleado(
            txtNombre.getText().trim(),
            txtCargo.getText().trim()
        );
        
        if (resultado) {
            JOptionPane.showMessageDialog(this,
                "Empleado registrado exitosamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarEmpleados();
        } else {
            JOptionPane.showMessageDialog(this,
                "Error al registrar el empleado",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarEmpleado() {
        if (empleadoSeleccionado == null) {
            return;
        }
        
        if (!validarCampos()) {
            return;
        }
        
        boolean resultado = empleadoController.actualizarEmpleado(
            empleadoSeleccionado.getIdEmpleado(),
            txtNombre.getText().trim(),
            txtCargo.getText().trim()
        );
        
        if (resultado) {
            JOptionPane.showMessageDialog(this,
                "Empleado actualizado exitosamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarEmpleados();
        } else {
            JOptionPane.showMessageDialog(this,
                "Error al actualizar el empleado",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarEmpleado() {
        if (empleadoSeleccionado == null) {
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar al empleado?\n" + empleadoSeleccionado.getNombre(),
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean resultado = empleadoController.eliminarEmpleado(
                empleadoSeleccionado.getIdEmpleado()
            );
            
            if (resultado) {
                JOptionPane.showMessageDialog(this,
                    "Empleado eliminado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarEmpleados();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar el empleado",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void limpiarFormulario() {
        txtNombre.setText("");
        txtCargo.setText("");
        
        empleadoSeleccionado = null;
        
        btnGuardar.setEnabled(true);
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
        
        tablaEmpleados.clearSelection();
    }
    
    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El nombre es obligatorio",
                "Campo Requerido",
                JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return false;
        }
        
        if (txtCargo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El cargo es obligatorio",
                "Campo Requerido",
                JOptionPane.WARNING_MESSAGE);
            txtCargo.requestFocus();
            return false;
        }
        
        return true;
    }
}