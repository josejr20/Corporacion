package com.empresa.inventario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.empresa.inventario.model.Rol;
import com.empresa.inventario.model.Usuario;

public class UsuarioDAO {

    public boolean insertar(Usuario usuario) {
        String sql = "INSERT INTO Usuario(username, password, idRol, idEmpleado) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getPassword());
            ps.setInt(3, usuario.getRol().getIdRol());
            ps.setInt(4, usuario.getIdEmpleado());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Usuario autenticar(String username, String password) {
        String sql = "SELECT u.idUsuario, u.username, u.password, u.idEmpleado, r.idRol, r.nombreRol " +
                     "FROM Usuario u INNER JOIN Rol r ON u.idRol = r.idRol " +
                     "WHERE u.username=? AND u.password=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Rol rol = new Rol(rs.getInt("idRol"), rs.getString("nombreRol"));
                // CORREGIDO: Agregado el parámetro idEmpleado
                return new Usuario(
                    rs.getInt("idUsuario"), 
                    rs.getString("username"), 
                    rs.getString("password"), 
                    rol,
                    rs.getInt("idEmpleado")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean actualizarPassword(int idUsuario, String nuevaPassword) {
        String sql = "UPDATE Usuario SET password=? WHERE idUsuario=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevaPassword);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}