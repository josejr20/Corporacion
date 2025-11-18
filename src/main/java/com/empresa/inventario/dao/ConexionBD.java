package com.empresa.inventario.dao;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;

public class ConexionBD {
    private static Connection conn = null;

    public static Connection getConnection() {
        if (conn == null) {
            try (InputStream input = ConexionBD.class.getClassLoader().getResourceAsStream("db.properties")) {
                Properties props = new Properties();
                props.load(input);

                String url = props.getProperty("db.url");
                String user = props.getProperty("db.user");
                String password = props.getProperty("db.password");

                conn = DriverManager.getConnection(url, user, password);
                System.out.println("Conexión exitosa a la base de datos VLAG");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return conn;
    }
}