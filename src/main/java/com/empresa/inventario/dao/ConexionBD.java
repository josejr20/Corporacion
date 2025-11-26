package com.empresa.inventario.dao;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class ConexionBD {
    private static Connection conn = null;

    public static Connection getConnection() {
        if (conn == null) {
            InputStream input = null;
            try {
                // Intentar cargar el archivo db.properties desde el classpath
                input = ConexionBD.class.getClassLoader().getResourceAsStream("db.properties");
                
                if (input == null) {
                    System.err.println("❌ ERROR: No se pudo encontrar el archivo db.properties");
                    System.err.println("📁 Asegúrese de que el archivo esté en src/main/resources/db.properties");
                    throw new RuntimeException("Archivo db.properties no encontrado en el classpath");
                }
                
                Properties props = new Properties();
                props.load(input);

                String url = props.getProperty("db.url");
                String user = props.getProperty("db.user");
                String password = props.getProperty("db.password");
                
                // Validar que las propiedades existan
                if (url == null || user == null || password == null) {
                    throw new RuntimeException("Propiedades de conexión incompletas en db.properties");
                }

                // Cargar el driver de MySQL explícitamente
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                conn = DriverManager.getConnection(url, user, password);
                System.out.println("✅ Conexión exitosa a la base de datos VLAG");
                System.out.println("🔗 URL: " + url);
                
            } catch (SQLException e) {
                System.err.println("❌ ERROR DE SQL al conectar a la base de datos:");
                System.err.println("   Mensaje: " + e.getMessage());
                System.err.println("   Código: " + e.getErrorCode());
                System.err.println("   Estado SQL: " + e.getSQLState());
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.err.println("❌ ERROR: Driver MySQL no encontrado");
                System.err.println("   Verifique que mysql-connector-java esté en el pom.xml");
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("❌ ERROR al leer db.properties:");
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("❌ ERROR GENERAL:");
                e.printStackTrace();
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return conn;
    }
    
    /**
     * Cierra la conexión a la base de datos
     */
    public static void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
                conn = null;
                System.out.println("🔌 Conexión cerrada");
            } catch (SQLException e) {
                System.err.println("❌ Error al cerrar la conexión:");
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Verifica si la conexión está activa
     */
    public static boolean isConnected() {
        try {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}