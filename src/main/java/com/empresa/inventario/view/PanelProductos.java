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
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import com.empresa.inventario.controller.ProductoController;
import com.empresa.inventario.model.Producto;

/**
 * Panel para gestionar productos
 */
public class PanelProductos extends JPanel {
    
    private final ProductoController productoController;
    
    // Componentes
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JTextField txtPrecio;
    private JSpinner spnStock;
    private JTextField txtBuscarId;
    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnBuscar;
    
    private Producto productoSeleccionado;
    
    public PanelProductos(ProductoController productoController) {
        this.productoController = productoController;
        this.productoSeleccionado = null;
        initComponents();
        cargarProductos();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior - Título
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTitulo.setBackground(Color.WHITE);
        JLabel lblTitulo = new JLabel("Gestión de Productos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(46, 204, 113));
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
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Producto"));
        
        int y = 30;
        int labelX = 20;
        int fieldX = 20;
        int fieldWidth = 300;
        int fieldHeight = 30;
        int espaciado = 60;
        
        // Nombre
        JLabel lblNombre = new JLabel("Nombre: *");
        lblNombre.setBounds(labelX, y, 150, 25);
        panel.add(lblNombre);
        
        txtNombre = new JTextField();
        txtNombre.setBounds(fieldX, y + 25, fieldWidth, fieldHeight);
        panel.add(txtNombre);
        
        y += espaciado;
        
        // Descripción
        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setBounds(labelX, y, 150, 25);
        panel.add(lblDescripcion);
        
        txtDescripcion = new JTextArea();
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setBounds(fieldX, y + 25, fieldWidth, 70);
        panel.add(scrollDesc);
        
        y += 100;
        
        // Precio
        JLabel lblPrecio = new JLabel("Precio (S/): *");
        lblPrecio.setBounds(labelX, y, 150, 25);
        panel.add(lblPrecio);
        
        txtPrecio = new JTextField();
        txtPrecio.setBounds(fieldX, y + 25, fieldWidth, fieldHeight);
        panel.add(txtPrecio);
        
        y += espaciado;
        
        // Stock
        JLabel lblStock = new JLabel("Stock: *");
        lblStock.setBounds(labelX, y, 150, 25);
        panel.add(lblStock);
        
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0, 0, 999999, 1);
        spnStock = new JSpinner(spinnerModel);
        spnStock.setBounds(fieldX, y + 25, fieldWidth, fieldHeight);
        panel.add(spnStock);
        
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
        btnGuardar.addActionListener(e -> guardarProducto());
        panel.add(btnGuardar);
        
        y += 45;
        
        btnActualizar = new JButton("Actualizar");
        btnActualizar.setBounds(20, y, 140, 35);
        btnActualizar.setBackground(new Color(241, 196, 15));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setEnabled(false);
        btnActualizar.addActionListener(e -> actualizarProducto());
        panel.add(btnActualizar);
        
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(180, y, 140, 35);
        btnEliminar.setBackground(new Color(231, 76, 60));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setEnabled(false);
        btnEliminar.addActionListener(e -> eliminarProducto());
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
        panel.setBorder(BorderFactory.createTitledBorder("Lista de Productos"));
        
        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.setBackground(Color.WHITE);
        
        JLabel lblBuscar = new JLabel("Buscar por ID:");
        panelBusqueda.add(lblBuscar);
        
        txtBuscarId = new JTextField(15);
        panelBusqueda.add(txtBuscarId);
        
        btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(new Color(52, 152, 219));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.addActionListener(e -> buscarProducto());
        panelBusqueda.add(btnBuscar);
        
        JButton btnMostrarTodos = new JButton("Mostrar Todos");
        btnMostrarTodos.setBackground(new Color(149, 165, 166));
        btnMostrarTodos.setForeground(Color.WHITE);
        btnMostrarTodos.setFocusPainted(false);
        btnMostrarTodos.addActionListener(e -> cargarProductos());
        panelBusqueda.add(btnMostrarTodos);
        
        panel.add(panelBusqueda, BorderLayout.NORTH);
        
        // Tabla
        String[] columnas = {"ID", "Nombre", "Descripción", "Precio", "Stock"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.getTableHeader().setReorderingAllowed(false);
        tablaProductos.setRowHeight(25);
        
        // Listener para selección de fila
        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int filaSeleccionada = tablaProductos.getSelectedRow();
                if (filaSeleccionada != -1) {
                    cargarProductoSeleccionado(filaSeleccionada);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void cargarProductos() {
        modeloTabla.setRowCount(0);
        List<Producto> productos = productoController.listarProductos();
        
        for (Producto producto : productos) {
            Object[] fila = {
                producto.getIdProducto(),
                producto.getNombre(),
                producto.getDescripcion(),
                String.format("S/ %.2f", producto.getPrecio()),
                producto.getStock()
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void buscarProducto() {
        String idTexto = txtBuscarId.getText().trim();
        if (idTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Ingrese un ID para buscar",
                "Campo Vacío",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int id = Integer.parseInt(idTexto);
            Producto producto = productoController.obtenerProducto(id);
            
            if (producto != null) {
                modeloTabla.setRowCount(0);
                Object[] fila = {
                    producto.getIdProducto(),
                    producto.getNombre(),
                    producto.getDescripcion(),
                    String.format("S/ %.2f", producto.getPrecio()),
                    producto.getStock()
                };
                modeloTabla.addRow(fila);
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se encontró producto con el ID: " + id,
                    "Producto No Encontrado",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "El ID debe ser un número",
                "ID Inválido",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarProductoSeleccionado(int fila) {
        productoSeleccionado = new Producto();
        productoSeleccionado.setIdProducto((int) modeloTabla.getValueAt(fila, 0));
        productoSeleccionado.setNombre((String) modeloTabla.getValueAt(fila, 1));
        productoSeleccionado.setDescripcion((String) modeloTabla.getValueAt(fila, 2));
        
        // Extraer el precio del formato "S/ 99.99"
        String precioStr = ((String) modeloTabla.getValueAt(fila, 3)).replace("S/ ", "");
        productoSeleccionado.setPrecio(Double.parseDouble(precioStr));
        productoSeleccionado.setStock((int) modeloTabla.getValueAt(fila, 4));
        
        // Cargar datos en el formulario
        txtNombre.setText(productoSeleccionado.getNombre());
        txtDescripcion.setText(productoSeleccionado.getDescripcion());
        txtPrecio.setText(String.valueOf(productoSeleccionado.getPrecio()));
        spnStock.setValue(productoSeleccionado.getStock());
        
        // Habilitar botones de actualizar y eliminar
        btnActualizar.setEnabled(true);
        btnEliminar.setEnabled(true);
        btnGuardar.setEnabled(false);
    }
    
    private void guardarProducto() {
        if (!validarCampos()) {
            return;
        }
        
        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int stock = (int) spnStock.getValue();
            
            boolean resultado = productoController.registrarProducto(
                txtNombre.getText().trim(),
                txtDescripcion.getText().trim(),
                precio,
                stock
            );
            
            if (resultado) {
                JOptionPane.showMessageDialog(this,
                    "Producto registrado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarProductos();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al registrar el producto",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "El precio debe ser un número válido",
                "Precio Inválido",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarProducto() {
        if (productoSeleccionado == null) {
            return;
        }
        
        if (!validarCampos()) {
            return;
        }
        
        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int stock = (int) spnStock.getValue();
            
            boolean resultado = productoController.actualizarProducto(
                productoSeleccionado.getIdProducto(),
                txtNombre.getText().trim(),
                txtDescripcion.getText().trim(),
                precio,
                stock
            );
            
            if (resultado) {
                JOptionPane.showMessageDialog(this,
                    "Producto actualizado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarProductos();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al actualizar el producto",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "El precio debe ser un número válido",
                "Precio Inválido",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarProducto() {
        if (productoSeleccionado == null) {
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea eliminar el producto?\n" + 
            productoSeleccionado.getNombre(),
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean resultado = productoController.eliminarProducto(productoSeleccionado.getIdProducto());
            
            if (resultado) {
                JOptionPane.showMessageDialog(this,
                    "Producto eliminado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarProductos();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar el producto.\nPuede tener pedidos asociados.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void limpiarFormulario() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");
        spnStock.setValue(0);
        txtBuscarId.setText("");
        
        productoSeleccionado = null;
        
        btnGuardar.setEnabled(true);
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
        
        tablaProductos.clearSelection();
    }
    
    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El nombre del producto es obligatorio",
                "Campo Requerido",
                JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return false;
        }
        
        if (txtPrecio.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El precio es obligatorio",
                "Campo Requerido",
                JOptionPane.WARNING_MESSAGE);
            txtPrecio.requestFocus();
            return false;
        }
        
        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            if (precio <= 0) {
                JOptionPane.showMessageDialog(this,
                    "El precio debe ser mayor a 0",
                    "Precio Inválido",
                    JOptionPane.WARNING_MESSAGE);
                txtPrecio.requestFocus();
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "El precio debe ser un número válido",
                "Precio Inválido",
                JOptionPane.ERROR_MESSAGE);
            txtPrecio.requestFocus();
            return false;
        }
        
        return true;
    }
}