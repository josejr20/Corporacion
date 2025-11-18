package com.empresa.inventario.view;

import com.empresa.inventario.controller.LoginController;
import com.empresa.inventario.model.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ventana de inicio de sesión
 */
public class LoginView extends JFrame {
    
    private final LoginController loginController;
    
    // Componentes
    private JPanel panelPrincipal;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIniciarSesion;
    private JButton btnCancelar;
    private JLabel lblTitulo;
    private JLabel lblUsuario;
    private JLabel lblPassword;
    private JLabel lblLogo;
    
    public LoginView() {
        this.loginController = new LoginController();
        initComponents();
        setupWindow();
    }
    
    private void initComponents() {
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(null);
        panelPrincipal.setBackground(new Color(240, 240, 240));
        
        // Logo/Título
        lblLogo = new JLabel("CORPORACIÓN VLAG S.A.C.");
        lblLogo.setFont(new Font("Arial", Font.BOLD, 24));
        lblLogo.setForeground(new Color(0, 102, 204));
        lblLogo.setBounds(50, 20, 400, 40);
        panelPrincipal.add(lblLogo);
        
        lblTitulo = new JLabel("Sistema de Gestión de Pedidos");
        lblTitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblTitulo.setForeground(new Color(100, 100, 100));
        lblTitulo.setBounds(110, 60, 300, 25);
        panelPrincipal.add(lblTitulo);
        
        // Usuario
        lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 14));
        lblUsuario.setBounds(80, 120, 100, 25);
        panelPrincipal.add(lblUsuario);
        
        txtUsuario = new JTextField();
        txtUsuario.setBounds(80, 150, 300, 35);
        txtUsuario.setFont(new Font("Arial", Font.PLAIN, 14));
        panelPrincipal.add(txtUsuario);
        
        // Contraseña
        lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 14));
        lblPassword.setBounds(80, 200, 100, 25);
        panelPrincipal.add(lblPassword);
        
        txtPassword = new JPasswordField();
        txtPassword.setBounds(80, 230, 300, 35);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        panelPrincipal.add(txtPassword);
        
        // Botones
        btnIniciarSesion = new JButton("Iniciar Sesión");
        btnIniciarSesion.setBounds(80, 290, 140, 40);
        btnIniciarSesion.setFont(new Font("Arial", Font.BOLD, 14));
        btnIniciarSesion.setBackground(new Color(0, 153, 76));
        btnIniciarSesion.setForeground(Color.WHITE);
        btnIniciarSesion.setFocusPainted(false);
        btnIniciarSesion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarSesion();
            }
        });
        panelPrincipal.add(btnIniciarSesion);
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(240, 290, 140, 40);
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCancelar.setBackground(new Color(204, 0, 0));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        panelPrincipal.add(btnCancelar);
        
        // Enter para login
        txtPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarSesion();
            }
        });
    }
    
    private void setupWindow() {
        setTitle("VLAG - Inicio de Sesión");
        setContentPane(panelPrincipal);
        setSize(460, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void iniciarSesion() {
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor complete todos los campos",
                "Campos Vacíos",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Intentar autenticar
        boolean exitoso = loginController.iniciarSesion(usuario, password);
        
        if (exitoso) {
            Usuario usuarioActual = loginController.getUsuarioActual();
            JOptionPane.showMessageDialog(this,
                "Bienvenido, " + usuarioActual.getUsername() + "\n" +
                "Rol: " + usuarioActual.getRol().getNombreRol(),
                "Inicio de Sesión Exitoso",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Abrir ventana principal
            this.dispose();
            MainFrame mainFrame = new MainFrame(loginController);
            mainFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                "Usuario o contraseña incorrectos",
                "Error de Autenticación",
                JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtUsuario.requestFocus();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginView().setVisible(true);
            }
        });
    }
}