package com.empresa.inventario.view;

import com.empresa.inventario.controller.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ventana principal del sistema con menú de navegación
 */
public class MainFrame extends JFrame {
    
    private final LoginController loginController;
    private final MainController mainController;
    
    // Componentes
    private JPanel panelPrincipal;
    private JPanel panelContenido;
    private JMenuBar menuBar;
    private JLabel lblBienvenida;
    private JLabel lblRol;
    
    public MainFrame(LoginController loginController) {
        this.loginController = loginController;
        this.mainController = new MainController();
        initComponents();
        setupWindow();
        mostrarPanelInicio();
    }
    
    private void initComponents() {
        panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(Color.WHITE);
        
        // Panel superior con información del usuario
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSuperior.setBackground(new Color(0, 102, 204));
        panelSuperior.setPreferredSize(new Dimension(0, 50));
        
        lblBienvenida = new JLabel("Usuario: " + loginController.getUsuarioActual().getUsername());
        lblBienvenida.setForeground(Color.WHITE);
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 14));
        panelSuperior.add(lblBienvenida);
        
        lblRol = new JLabel(" | Rol: " + loginController.getRolActual() + " ");
        lblRol.setForeground(Color.WHITE);
        lblRol.setFont(new Font("Arial", Font.PLAIN, 14));
        panelSuperior.add(lblRol);
        
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setBackground(new Color(204, 0, 0));
        btnCerrarSesion.setForeground(Color.WHITE);
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        panelSuperior.add(btnCerrarSesion);
        
        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        
        // Panel de contenido
        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBackground(Color.WHITE);
        panelPrincipal.add(panelContenido, BorderLayout.CENTER);
        
        // Crear menú
        crearMenu();
    }
    
    private void crearMenu() {
        menuBar = new JMenuBar();
        menuBar.setBackground(new Color(240, 240, 240));
        
        // Menú Inicio
        JMenu menuInicio = new JMenu("Inicio");
        JMenuItem itemInicio = new JMenuItem("Panel Principal");
        itemInicio.addActionListener(e -> mostrarPanelInicio());
        menuInicio.add(itemInicio);
        menuBar.add(menuInicio);
        
        // Menú Clientes
        JMenu menuClientes = new JMenu("Clientes");
        JMenuItem itemGestionClientes = new JMenuItem("Gestión de Clientes");
        itemGestionClientes.addActionListener(e -> mostrarGestionClientes());
        menuClientes.add(itemGestionClientes);
        menuBar.add(menuClientes);
        
        // Menú Productos
        JMenu menuProductos = new JMenu("Productos");
        JMenuItem itemGestionProductos = new JMenuItem("Gestión de Productos");
        itemGestionProductos.addActionListener(e -> mostrarGestionProductos());
        menuProductos.add(itemGestionProductos);
        menuBar.add(menuProductos);
        
        // Menú Pedidos
        JMenu menuPedidos = new JMenu("Pedidos");
        JMenuItem itemNuevoPedido = new JMenuItem("Nuevo Pedido");
        itemNuevoPedido.addActionListener(e -> mostrarNuevoPedido());
        JMenuItem itemListaPedidos = new JMenuItem("Lista de Pedidos");
        itemListaPedidos.addActionListener(e -> mostrarListaPedidos());
        menuPedidos.add(itemNuevoPedido);
        menuPedidos.add(itemListaPedidos);
        menuBar.add(menuPedidos);
        
        // Menú Empleados (solo para administradores)
        if (loginController.esAdministrador()) {
            JMenu menuEmpleados = new JMenu("Empleados");
            JMenuItem itemGestionEmpleados = new JMenuItem("Gestión de Empleados");
            itemGestionEmpleados.addActionListener(e -> mostrarGestionEmpleados());
            menuEmpleados.add(itemGestionEmpleados);
            menuBar.add(menuEmpleados);
        }
        
        // Menú Reportes
        JMenu menuReportes = new JMenu("Reportes");
        JMenuItem itemReporteVentas = new JMenuItem("Reporte de Ventas");
        itemReporteVentas.addActionListener(e -> mostrarReporteVentas());
        JMenuItem itemReporteInventario = new JMenuItem("Reporte de Inventario");
        itemReporteInventario.addActionListener(e -> mostrarReporteInventario());
        JMenuItem itemReporteClientes = new JMenuItem("Reporte de Clientes");
        itemReporteClientes.addActionListener(e -> mostrarReporteClientes());
        menuReportes.add(itemReporteVentas);
        menuReportes.add(itemReporteInventario);
        menuReportes.add(itemReporteClientes);
        menuBar.add(menuReportes);
        
        setJMenuBar(menuBar);
    }
    
    private void setupWindow() {
        setTitle("CORPORACIÓN VLAG S.A.C. - Sistema de Gestión");
        setContentPane(panelPrincipal);
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    private void mostrarPanelInicio() {
        PanelInicio panelInicio = new PanelInicio(loginController);
        cambiarPanel(panelInicio);
    }
    
    private void mostrarGestionClientes() {
        PanelClientes panelClientes = new PanelClientes(mainController.getClienteController());
        cambiarPanel(panelClientes);
    }
    
    private void mostrarGestionProductos() {
        PanelProductos panelProductos = new PanelProductos(mainController.getProductoController());
        cambiarPanel(panelProductos);
    }
    
    private void mostrarNuevoPedido() {
        PanelNuevoPedido panelNuevoPedido = new PanelNuevoPedido(
            mainController.getPedidoController(),
            mainController.getClienteController(),
            mainController.getProductoController(),
            loginController.getUsuarioActual()
        );
        cambiarPanel(panelNuevoPedido);
    }
    
    private void mostrarListaPedidos() {
        PanelListaPedidos panelListaPedidos = new PanelListaPedidos(
            mainController.getPedidoController()
        );
        cambiarPanel(panelListaPedidos);
    }
    
    private void mostrarGestionEmpleados() {
        PanelEmpleados panelEmpleados = new PanelEmpleados(mainController.getEmpleadoController());
        cambiarPanel(panelEmpleados);
    }
    
    private void mostrarReporteVentas() {
        PanelReportes panelReportes = new PanelReportes(
            mainController.getReporteController(), 
            "VENTAS"
        );
        cambiarPanel(panelReportes);
    }
    
    private void mostrarReporteInventario() {
        PanelReportes panelReportes = new PanelReportes(
            mainController.getReporteController(), 
            "INVENTARIO"
        );
        cambiarPanel(panelReportes);
    }
    
    private void mostrarReporteClientes() {
        PanelReportes panelReportes = new PanelReportes(
            mainController.getReporteController(), 
            "CLIENTES"
        );
        cambiarPanel(panelReportes);
    }
    
    private void cambiarPanel(JPanel nuevoPanel) {
        panelContenido.removeAll();
        panelContenido.add(nuevoPanel, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }
    
    private void cerrarSesion() {
        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro que desea cerrar sesión?",
            "Confirmar Cierre de Sesión",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            loginController.cerrarSesion();
            this.dispose();
            new LoginView().setVisible(true);
        }
    }
}