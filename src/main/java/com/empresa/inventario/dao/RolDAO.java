package com.empresa.inventario.dao;

import com.empresa.inventario.model.Rol;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolDAO {

    public List<Rol> listar() {
        List<Rol> lista = new ArrayList<>();
        String sql = "SELECT * FROM Rol";
        try (Connection conn = ConexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Rol(rs.getInt("idRol"), rs.getString("nombreRol")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Rol buscarPorId(int idRol) {
        String sql = "SELECT * FROM Rol WHERE idRol=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idRol);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Rol(rs.getInt("idRol"), rs.getString("nombreRol"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}