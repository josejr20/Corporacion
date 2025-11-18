package com.empresa.inventario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.empresa.inventario.model.Producto;

public class ProductoDAO {

    public boolean insertar(Producto producto) {
        String sql = "INSERT INTO Producto(nombre, descripcion, precio, stock) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getDescripcion());
            ps.setDouble(3, producto.getPrecio());
            ps.setInt(4, producto.getStock());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Producto obtenerPorId(int idProducto) {
        String sql = "SELECT * FROM Producto WHERE idProducto = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new Producto(
                    rs.getInt("idProducto"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getDouble("precio"),
                    rs.getInt("stock")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener producto por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM Producto";
        try (Connection conn = ConexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Producto(
                        rs.getInt("idProducto"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio"),
                        rs.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean actualizar(Producto producto) {
        String sql = "UPDATE Producto SET nombre=?, descripcion=?, precio=?, stock=? WHERE idProducto=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getDescripcion());
            ps.setDouble(3, producto.getPrecio());
            ps.setInt(4, producto.getStock());
            ps.setInt(5, producto.getIdProducto());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int idProducto) {
        String sql = "DELETE FROM Producto WHERE idProducto=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}