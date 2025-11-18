package com.empresa.inventario.controller;

/**
 * Controlador principal que coordina todos los demás controladores
 * Punto de entrada para la aplicación
 */
public class MainController {
    
    private final LoginController loginController;
    private final ProductoController productoController;
    private final ClienteController clienteController;
    private final PedidoController pedidoController;
    private final EmpleadoController empleadoController;
    private final FacturaController facturaController;
    private final PagoController pagoController;
    private final EntregaController entregaController;
    private final ReporteController reporteController;
    
    public MainController() {
        this.loginController = new LoginController();
        this.productoController = new ProductoController();
        this.clienteController = new ClienteController();
        this.pedidoController = new PedidoController();
        this.empleadoController = new EmpleadoController();
        this.facturaController = new FacturaController();
        this.pagoController = new PagoController();
        this.entregaController = new EntregaController();
        this.reporteController = new ReporteController();
    }
    
    // Getters para acceder a los controladores
    public LoginController getLoginController() {
        return loginController;
    }
    
    public ProductoController getProductoController() {
        return productoController;
    }
    
    public ClienteController getClienteController() {
        return clienteController;
    }
    
    public PedidoController getPedidoController() {
        return pedidoController;
    }
    
    public EmpleadoController getEmpleadoController() {
        return empleadoController;
    }
    
    public FacturaController getFacturaController() {
        return facturaController;
    }
    
    public PagoController getPagoController() {
        return pagoController;
    }
    
    public EntregaController getEntregaController() {
        return entregaController;
    }
    
    public ReporteController getReporteController() {
        return reporteController;
    }
    
    /**
     * Verifica que el usuario tenga permisos antes de ejecutar acciones
     */
    public boolean verificarPermisos(String accion) {
        if (!loginController.haySesionActiva()) {
            System.err.println("⚠ Debe iniciar sesión para realizar esta acción");
            return false;
        }
        
        // Aquí puedes agregar lógica adicional de permisos por rol
        return true;
    }
}