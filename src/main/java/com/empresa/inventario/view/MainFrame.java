package com.empresa.inventario.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.empresa.inventario.controller.LoginController;
import com.empresa.inventario.controller.MainController;

/**
 * Ventana principal del sistema con menú de navegación según rol
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
        
        // Verificar que hay sesión activa
        if (!loginController.haySesionActiva()) {
            JOptionPane.showMessageDialog(null, "No hay sesión activa");
            dispose();
            return;
        }
        
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
        
        // Crear menú según el rol
        crearMenuSegunRol();
    }
    
    private void crearMenuSegunRol() {
        menuBar = new JMenuBar();
        menuBar.setBackground(new Color(240, 240, 240));
        
        System.out.println("🔍 Creando menú para rol: " + loginController.getRolActual());
        
        // Menú Inicio (Todos los roles)
        agregarMenuInicio();
        
        // Menús según rol
        if (loginController.esGerente()) {
            crearMenuGerente();
        } else if (loginController.esAdministrador()) {
            crearMenuAdministrador();
        } else if (loginController.esJefeVentas()) {
            crearMenuJefeVentas();
        } else if (loginController.esVendedor()) {
            crearMenuVendedor();
        } else if (loginController.esAsistenteComercial()) {
            crearMenuAsistenteComercial();
        } else if (loginController.esJefeAlmacen()) {
            crearMenuJefeAlmacen();
        } else if (loginController.esAuxiliarAlmacen()) {
            crearMenuAuxiliarAlmacen();
        } else if (loginController.esJefeLogistica()) {
            crearMenuJefeLogistica();
        } else if (loginController.esEncargadoFlota()) {
            crearMenuEncargadoFlota();
        } else if (loginController.esRepartidor()) {
            crearMenuRepartidor();
        } else {
            // Rol no reconocido - menú básico
            agregarMenuBasico();
        }
        
        setJMenuBar(menuBar);
    }
    
    private void agregarMenuInicio() {
        JMenu menuInicio = new JMenu("Inicio");
        JMenuItem itemInicio = new JMenuItem("Panel Principal");
        itemInicio.addActionListener(e -> mostrarPanelInicio());
        menuInicio.add(itemInicio);
        menuBar.add(menuInicio);
    }
    
    // ========== MENÚS POR ROL ==========
    
    private void crearMenuGerente() {
        System.out.println("✓ Menú GERENTE cargado");
        // Gerente ve TODO
        agregarMenuClientes();
        agregarMenuProductos();
        agregarMenuPedidos();
        agregarMenuEmpleados();
        agregarMenuReportes();
    }
    
    private void crearMenuAdministrador() {
        System.out.println("✓ Menú ADMINISTRADOR cargado");
        agregarMenuClientes();
        agregarMenuProductos();
        agregarMenuPedidos();
        agregarMenuEmpleados();
        agregarMenuReportes();
    }
    
    private void crearMenuJefeVentas() {
        System.out.println("✓ Menú JEFE DE VENTAS cargado");
        agregarMenuClientes();
        agregarMenuProductos();
        
        // Menú Pedidos con validación
        JMenu menuPedidos = new JMenu("Pedidos");
        JMenuItem itemNuevoPedido = new JMenuItem("Nuevo Pedido");
        itemNuevoPedido.addActionListener(e -> mostrarNuevoPedido());
        JMenuItem itemListaPedidos = new JMenuItem("Lista de Pedidos");
        itemListaPedidos.addActionListener(e -> mostrarListaPedidos());
        JMenuItem itemValidarPedidos = new JMenuItem("⭐ Validar Pedidos");
        itemValidarPedidos.addActionListener(e -> mostrarValidarPedidos());
        menuPedidos.add(itemNuevoPedido);
        menuPedidos.add(itemListaPedidos);
        menuPedidos.addSeparator();
        menuPedidos.add(itemValidarPedidos);
        menuBar.add(menuPedidos);
        
        agregarMenuReportes();
    }
    
    private void crearMenuVendedor() {
        System.out.println("✓ Menú VENDEDOR cargado");
        
        // Solo consulta de clientes
        JMenu menuClientes = new JMenu("Clientes");
        JMenuItem itemConsultarClientes = new JMenuItem("Consultar Clientes");
        itemConsultarClientes.addActionListener(e -> mostrarGestionClientes());
        menuClientes.add(itemConsultarClientes);
        menuBar.add(menuClientes);
        
        // Solo consulta de productos
        JMenu menuProductos = new JMenu("Productos");
        JMenuItem itemConsultarProductos = new JMenuItem("Consultar Productos");
        itemConsultarProductos.addActionListener(e -> mostrarGestionProductos());
        menuProductos.add(itemConsultarProductos);
        menuBar.add(menuProductos);
        
        // Pedidos - función principal
        JMenu menuPedidos = new JMenu("⭐ Pedidos");
        JMenuItem itemNuevoPedido = new JMenuItem("Nuevo Pedido");
        itemNuevoPedido.addActionListener(e -> mostrarNuevoPedido());
        JMenuItem itemMisPedidos = new JMenuItem("Mis Pedidos");
        itemMisPedidos.addActionListener(e -> mostrarListaPedidos());
        menuPedidos.add(itemNuevoPedido);
        menuPedidos.add(itemMisPedidos);
        menuBar.add(menuPedidos);
    }
    
    private void crearMenuAsistenteComercial() {
        System.out.println("✓ Menú ASISTENTE COMERCIAL cargado");
        agregarMenuPedidos();
        agregarMenuReportes();
    }
    
    private void crearMenuJefeAlmacen() {
        System.out.println("✓ Menú JEFE DE ALMACÉN cargado");
        agregarMenuProductos();
        
        JMenu menuAlmacen = new JMenu("⭐ Almacén");
        JMenuItem itemPedidosPendientes = new JMenuItem("Pedidos Pendientes");
        itemPedidosPendientes.addActionListener(e -> mostrarListaPedidos());
        JMenuItem itemInventario = new JMenuItem("Control de Inventario");
        itemInventario.addActionListener(e -> mostrarGestionProductos());
        menuAlmacen.add(itemPedidosPendientes);
        menuAlmacen.add(itemInventario);
        menuBar.add(menuAlmacen);
    }
    
    private void crearMenuAuxiliarAlmacen() {
        System.out.println("✓ Menú AUXILIAR DE ALMACÉN cargado");
        
        JMenu menuAlmacen = new JMenu("⭐ Almacén");
        JMenuItem itemTareas = new JMenuItem("Mis Tareas");
        itemTareas.addActionListener(e -> mostrarListaPedidos());
        menuAlmacen.add(itemTareas);
        menuBar.add(menuAlmacen);
    }
    
    private void crearMenuJefeLogistica() {
        System.out.println("✓ Menú JEFE DE LOGÍSTICA cargado");
        
        JMenu menuLogistica = new JMenu("⭐ Logística");
        JMenuItem itemEntregas = new JMenuItem("Planificar Entregas");
        itemEntregas.addActionListener(e -> mostrarListaPedidos());
        menuLogistica.add(itemEntregas);
        menuBar.add(menuLogistica);
    }
    
    private void crearMenuEncargadoFlota() {
        System.out.println("✓ Menú ENCARGADO DE FLOTA cargado");
        
        JMenu menuFlota = new JMenu("⭐ Flota");
        JMenuItem itemAsignar = new JMenuItem("Asignar Repartidores");
        itemAsignar.addActionListener(e -> mostrarListaPedidos());
        menuFlota.add(itemAsignar);
        menuBar.add(menuFlota);
    }
    
    private void crearMenuRepartidor() {
        System.out.println("✓ Menú REPARTIDOR cargado");
        
        JMenu menuEntregas = new JMenu("⭐ Mis Entregas");
        JMenuItem itemEntregas = new JMenuItem("Ver Entregas Asignadas");
        itemEntregas.addActionListener(e -> mostrarListaPedidos());
        menuEntregas.add(itemEntregas);
        menuBar.add(menuEntregas);
    }
    
    // ========== MENÚS GENÉRICOS ==========
    
    private void agregarMenuClientes() {
        JMenu menuClientes = new JMenu("Clientes");
        JMenuItem itemGestionClientes = new JMenuItem("Gestión de Clientes");
        itemGestionClientes.addActionListener(e -> mostrarGestionClientes());
        menuClientes.add(itemGestionClientes);
        menuBar.add(menuClientes);
    }
    
    private void agregarMenuProductos() {
        JMenu menuProductos = new JMenu("Productos");
        JMenuItem itemGestionProductos = new JMenuItem("Gestión de Productos");
        itemGestionProductos.addActionListener(e -> mostrarGestionProductos());
        menuProductos.add(itemGestionProductos);
        menuBar.add(menuProductos);
    }
    
    private void agregarMenuPedidos() {
        JMenu menuPedidos = new JMenu("Pedidos");
        JMenuItem itemNuevoPedido = new JMenuItem("Nuevo Pedido");
        itemNuevoPedido.addActionListener(e -> mostrarNuevoPedido());
        JMenuItem itemListaPedidos = new JMenuItem("Lista de Pedidos");
        itemListaPedidos.addActionListener(e -> mostrarListaPedidos());
        menuPedidos.add(itemNuevoPedido);
        menuPedidos.add(itemListaPedidos);
        menuBar.add(menuPedidos);
    }
    
    private void agregarMenuEmpleados() {
        JMenu menuEmpleados = new JMenu("Empleados");
        JMenuItem itemGestionEmpleados = new JMenuItem("Gestión de Empleados");
        itemGestionEmpleados.addActionListener(e -> mostrarGestionEmpleados());
        menuEmpleados.add(itemGestionEmpleados);
        menuBar.add(menuEmpleados);
    }
    
    private void agregarMenuReportes() {
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
    }
    
    private void agregarMenuBasico() {
        System.out.println("⚠ Rol no reconocido - Menú básico cargado");
        JMenu menuGeneral = new JMenu("General");
        JMenuItem itemInfo = new JMenuItem("Información");
        itemInfo.addActionListener(e -> JOptionPane.showMessageDialog(this, 
            "Sistema en construcción para su rol: " + loginController.getRolActual()));
        menuGeneral.add(itemInfo);
        menuBar.add(menuGeneral);
    }
    
    // ========== NAVEGACIÓN ==========
    
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
    
    private void mostrarValidarPedidos() {
        // TODO: Crear PanelValidarPedidos
        JOptionPane.showMessageDialog(this,
            "Panel de Validación de Pedidos en construcción",
            "Próximamente",
            JOptionPane.INFORMATION_MESSAGE);
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
            System.out.println("🔓 Cerrando sesión...");
            loginController.cerrarSesion();
            this.dispose();
            
            // Crear nueva instancia de LoginView
            SwingUtilities.invokeLater(() -> {
                LoginView loginView = new LoginView();
                loginView.setVisible(true);
                System.out.println("✓ LoginView reiniciado correctamente");
            });
        }
    }
}