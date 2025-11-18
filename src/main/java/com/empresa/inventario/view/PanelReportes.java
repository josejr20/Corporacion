package com.empresa.inventario.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import com.empresa.inventario.controller.ReporteController;

/**
 * Panel para visualizar reportes del sistema
 */
public class PanelReportes extends JPanel {
    
    private final ReporteController reporteController;
    private final String tipoReporte;
    
    // Componentes
    private JTextArea txtAreaReporte;
    private JButton btnGenerar;
    private JButton btnExportar;
    private JSpinner spnUmbralStock;
    private JTextField txtIdCliente;
    
    public PanelReportes(ReporteController reporteController, String tipoReporte) {
        this.reporteController = reporteController;
        this.tipoReporte = tipoReporte;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior - Título y opciones
        JPanel panelSuperior = crearPanelSuperior();
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central - Área de reporte
        JPanel panelCentral = crearPanelCentral();
        add(panelCentral, BorderLayout.CENTER);
    }
    
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        
        // Título
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTitulo.setBackground(Color.WHITE);
        
        JLabel lblTitulo = new JLabel("Reporte de " + tipoReporte);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(52, 73, 94));
        panelTitulo.add(lblTitulo);
        
        panel.add(panelTitulo, BorderLayout.NORTH);
        
        // Panel de opciones según el tipo de reporte
        JPanel panelOpciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelOpciones.setBackground(Color.WHITE);
        
        if ("INVENTARIO".equals(tipoReporte)) {
            JLabel lblUmbral = new JLabel("Stock Bajo (umbral):");
            panelOpciones.add(lblUmbral);
            
            SpinnerNumberModel model = new SpinnerNumberModel(10, 0, 100, 5);
            spnUmbralStock = new JSpinner(model);
            spnUmbralStock.setPreferredSize(new Dimension(80, 25));
            panelOpciones.add(spnUmbralStock);
            
            JButton btnStockBajo = new JButton("Ver Stock Bajo");
            btnStockBajo.setBackground(new Color(230, 126, 34));
            btnStockBajo.setForeground(Color.WHITE);
            btnStockBajo.setFocusPainted(false);
            btnStockBajo.addActionListener(e -> generarReporteStockBajo());
            panelOpciones.add(btnStockBajo);
        }
        
        if ("CLIENTES".equals(tipoReporte)) {
            JLabel lblIdCliente = new JLabel("ID Cliente:");
            panelOpciones.add(lblIdCliente);
            
            txtIdCliente = new JTextField(10);
            panelOpciones.add(txtIdCliente);
            
            JButton btnPedidosCliente = new JButton("Ver Pedidos del Cliente");
            btnPedidosCliente.setBackground(new Color(41, 128, 185));
            btnPedidosCliente.setForeground(Color.WHITE);
            btnPedidosCliente.setFocusPainted(false);
            btnPedidosCliente.addActionListener(e -> generarReportePorCliente());
            panelOpciones.add(btnPedidosCliente);
        }
        
        btnGenerar = new JButton("Generar Reporte");
        btnGenerar.setBackground(new Color(46, 204, 113));
        btnGenerar.setForeground(Color.WHITE);
        btnGenerar.setFocusPainted(false);
        btnGenerar.addActionListener(e -> generarReporte());
        panelOpciones.add(btnGenerar);
        
        btnExportar = new JButton("Exportar a TXT");
        btnExportar.setBackground(new Color(52, 152, 219));
        btnExportar.setForeground(Color.WHITE);
        btnExportar.setFocusPainted(false);
        btnExportar.addActionListener(e -> exportarReporte());
        panelOpciones.add(btnExportar);
        
        panel.add(panelOpciones, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Contenido del Reporte"));
        
        txtAreaReporte = new JTextArea();
        txtAreaReporte.setEditable(false);
        txtAreaReporte.setFont(new Font("Courier New", Font.PLAIN, 12));
        txtAreaReporte.setBackground(new Color(250, 250, 250));
        
        JScrollPane scrollPane = new JScrollPane(txtAreaReporte);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void generarReporte() {
        String reporte = "";
        
        switch (tipoReporte) {
            case "VENTAS":
                reporte = reporteController.generarReporteVentas();
                break;
            case "INVENTARIO":
                reporte = reporteController.generarReporteInventario();
                break;
            case "CLIENTES":
                reporte = reporteController.generarReporteClientes();
                break;
            default:
                reporte = "Tipo de reporte no reconocido";
        }
        
        txtAreaReporte.setText(reporte);
        txtAreaReporte.setCaretPosition(0);
    }
    
    private void generarReporteStockBajo() {
        int umbral = (int) spnUmbralStock.getValue();
        String reporte = reporteController.generarReporteStockBajo(umbral);
        txtAreaReporte.setText(reporte);
        txtAreaReporte.setCaretPosition(0);
    }
    
    private void generarReportePorCliente() {
        String idTexto = txtIdCliente.getText().trim();
        
        if (idTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Ingrese un ID de cliente",
                "Campo Requerido",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int idCliente = Integer.parseInt(idTexto);
            String reporte = reporteController.generarReportePedidosPorCliente(idCliente);
            txtAreaReporte.setText(reporte);
            txtAreaReporte.setCaretPosition(0);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "El ID debe ser un número válido",
                "ID Inválido",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportarReporte() {
        String contenido = txtAreaReporte.getText();
        
        if (contenido.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Genere un reporte primero",
                "Sin Contenido",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte");
        fileChooser.setSelectedFile(new java.io.File("Reporte_" + tipoReporte + ".txt"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            
            try (java.io.FileWriter writer = new java.io.FileWriter(fileToSave)) {
                writer.write(contenido);
                JOptionPane.showMessageDialog(this,
                    "Reporte exportado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (java.io.IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al exportar el reporte: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}