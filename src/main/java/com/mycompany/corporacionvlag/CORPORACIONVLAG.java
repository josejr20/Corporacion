package com.mycompany.corporacionvlag;

import com.empresa.inventario.view.LoginView;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Clase principal de la aplicación
 */
public class CORPORACIONVLAG {
    
    public static void main(String[] args) {
        // Establecer Look and Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Iniciar la aplicación en el Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Mostrar la ventana de login
                LoginView loginView = new LoginView();
                loginView.setVisible(true);
                
                System.out.println("========================================");
                System.out.println("  CORPORACIÓN VLAG S.A.C.");
                System.out.println("  Sistema de Gestión de Pedidos");
                System.out.println("========================================");
            }
        });
    }
}