package com.empresa.inventario.dao;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * Gestor de conexiones a la base de datos
 * CORREGIDO: Ahora crea una nueva conexión cada vez en lugar de reutilizar una cerrada
 */
public class ConexionBD {
    
    // Propiedades de conexión (cargadas una sola vez)
    private static String DB_URL = null;
    private static String DB_USER = null;
    private static String DB_PASSWORD = null;
    private static boolean propiedadesCargadas = false;

    /**
     * Carga las propiedades de conexión desde db.properties
     * Solo se ejecuta una vez
     */
    private static synchronized void cargarPropiedades() {
        if (propiedadesCargadas) {
            return;
        }

        InputStream input = null;
        try {
            input = ConexionBD.class.getClassLoader().getResourceAsStream("db.properties");
            
            if (input == null) {
                System.err.println("❌ ERROR: No se pudo encontrar el archivo db.properties");
                System.err.println("📁 Asegúrese de que el archivo esté en src/main/resources/db.properties");
                throw new RuntimeException("Archivo db.properties no encontrado en el classpath");
            }
            
            Properties props = new Properties();
            props.load(input);

            DB_URL = props.getProperty("db.url");
            DB_USER = props.getProperty("db.user");
            DB_PASSWORD = props.getProperty("db.password");
            
            // Validar que las propiedades existan
            if (DB_URL == null || DB_USER == null || DB_PASSWORD == null) {
                throw new RuntimeException("Propiedades de conexión incompletas en db.properties");
            }

            // Cargar el driver de MySQL explícitamente
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            propiedadesCargadas = true;
            System.out.println("✅ Propiedades de conexión cargadas correctamente");
            System.out.println("🔗 URL: " + DB_URL);
            
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

    /**
     * Obtiene una NUEVA conexión a la base de datos
     * IMPORTANTE: Cada llamada crea una nueva conexión
     * 
     * @return Nueva conexión a la base de datos
     */
    public static Connection getConnection() {
        // Cargar propiedades si no están cargadas
        if (!propiedadesCargadas) {
            cargarPropiedades();
        }

        Connection conn = null;
        try {
            // Crear una NUEVA conexión cada vez
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
        } catch (SQLException e) {
            System.err.println("❌ ERROR al crear conexión a la base de datos:");
            System.err.println("   Mensaje: " + e.getMessage());
            System.err.println("   Código: " + e.getErrorCode());
            System.err.println("   Estado SQL: " + e.getSQLState());
            e.printStackTrace();
        }
        
        return conn;
    }
    
    /**
     * Cierra una conexión de forma segura
     * @param conn Conexión a cerrar
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("❌ Error al cerrar la conexión:");
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Cierra un Statement de forma segura
     * @param stmt Statement a cerrar
     */
    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("❌ Error al cerrar el Statement:");
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Cierra un ResultSet de forma segura
     * @param rs ResultSet a cerrar
     */
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("❌ Error al cerrar el ResultSet:");
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Verifica si se puede establecer conexión con la base de datos
     * @return true si la conexión es exitosa
     */
    public static boolean testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        } finally {
            closeConnection(conn);
        }
    }
}