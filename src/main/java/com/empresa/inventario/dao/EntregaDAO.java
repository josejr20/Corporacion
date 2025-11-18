package com.empresa.inventario.dao;

import com.empresa.inventario.model.Entrega;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntregaDAO {

    public boolean insertar(Entrega entrega) {
        String sql = "INSERT INTO Entrega(idPedido, fechaEntrega, estadoEntrega) VALUES (?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entrega.getPedido().getIdPedido());
            ps.setDate(2, new java.sql.Date(entrega.getFechaEntrega().getTime()));
            ps.setString(3, entrega.getEstado());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Entrega> listar() {
        List<Entrega> lista = new ArrayList<>();
        String sql = "SELECT * FROM Entrega";
        try (Connection conn = ConexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Entrega entrega = new Entrega(rs.getInt("idEntrega"), null, rs.getDate("fechaEntrega"), rs.getString("estadoEntrega"));
                lista.add(entrega);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean actualizarEstado(int idEntrega, String nuevoEstado) {
        String sql = "UPDATE Entrega SET estadoEntrega=? WHERE idEntrega=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idEntrega);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}