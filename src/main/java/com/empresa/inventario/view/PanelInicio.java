package com.empresa.inventario.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.empresa.inventario.controller.LoginController;

/**
 * Panel de inicio/dashboard
 */
public class PanelInicio extends JPanel {
    
    private final LoginController loginController;
    
    public PanelInicio(LoginController loginController) {
        this.loginController = loginController;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Panel central con información
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBackground(Color.WHITE);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        // Logo y título
        JLabel lblLogo = new JLabel("CORPORACIÓN VLAG S.A.C.");
        lblLogo.setFont(new Font("Arial", Font.BOLD, 36));
        lblLogo.setForeground(new Color(0, 102, 204));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(lblLogo);
        
        panelCentral.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JLabel lblSubtitulo = new JLabel("Sistema de Gestión de Pedidos Mayoristas");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 20));
        lblSubtitulo.setForeground(new Color(100, 100, 100));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelCentral.add(lblSubtitulo);
        
        panelCentral.add(Box.createRigidArea(new Dimension(0, 50)));
        
        // Información del usuario
        JPanel panelInfo = new JPanel(new GridLayout(3, 1, 10, 10));
        panelInfo.setBackground(new Color(240, 240, 240));
        panelInfo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panelInfo.setMaximumSize(new Dimension(600, 150));
        
        JLabel lblUsuario = new JLabel("Usuario: " + loginController.getUsuarioActual().getUsername());
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 18));
        panelInfo.add(lblUsuario);
        
        JLabel lblRol = new JLabel("Rol: " + loginController.getRolActual());
        lblRol.setFont(new Font("Arial", Font.PLAIN, 16));
        panelInfo.add(lblRol);
        
        JLabel lblInstrucciones = new JLabel("Utilice el menú superior para navegar por el sistema");
        lblInstrucciones.setFont(new Font("Arial", Font.ITALIC, 14));
        lblInstrucciones.setForeground(new Color(100, 100, 100));
        panelInfo.add(lblInstrucciones);
        
        panelCentral.add(panelInfo);
        
        panelCentral.add(Box.createRigidArea(new Dimension(0, 50)));
        
        // Tarjetas de acceso rápido
        JPanel panelTarjetas = new JPanel(new GridLayout(1, 3, 20, 0));
        panelTarjetas.setBackground(Color.WHITE);
        panelTarjetas.setMaximumSize(new Dimension(900, 150));
        
        panelTarjetas.add(crearTarjeta("Clientes", "Gestionar clientes", new Color(52, 152, 219)));
        panelTarjetas.add(crearTarjeta("Productos", "Gestionar inventario", new Color(46, 204, 113)));
        panelTarjetas.add(crearTarjeta("Pedidos", "Registrar pedidos", new Color(155, 89, 182)));
        
        panelCentral.add(panelTarjetas);
        
        add(panelCentral, BorderLayout.CENTER);
    }
    
    private JPanel crearTarjeta(String titulo, String descripcion, Color color) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(color);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        tarjeta.add(lblTitulo);
        
        tarjeta.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JLabel lblDescripcion = new JLabel(descripcion);
        lblDescripcion.setFont(new Font("Arial", Font.PLAIN, 14));
        lblDescripcion.setForeground(Color.WHITE);
        lblDescripcion.setAlignmentX(Component.CENTER_ALIGNMENT);
        tarjeta.add(lblDescripcion);
        
        return tarjeta;
    }
}