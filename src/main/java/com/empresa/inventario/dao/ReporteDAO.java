package com.empresa.inventario.dao;

import com.empresa.inventario.model.Reporte;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {

    // Método para generar un nuevo reporte
    public boolean generarReporte(Reporte reporte) {
        String sql = "INSERT INTO Reporte(tipo, fechaGeneracion) VALUES (?, NOW())";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reporte.getTipo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para listar todos los reportes
    public List<Reporte> listar() {
        List<Reporte> lista = new ArrayList<>();
        String sql = "SELECT * FROM Reporte ORDER BY fechaGeneracion DESC";
        try (Connection conn = ConexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Reporte(
                        rs.getInt("idReporte"),
                        rs.getString("tipo"),
                        rs.getTimestamp("fechaGeneracion")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Método para buscar reporte por ID
    public Reporte buscarPorId(int idReporte) {
        String sql = "SELECT * FROM Reporte WHERE idReporte=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idReporte);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Reporte(
                        rs.getInt("idReporte"),
                        rs.getString("tipo"),
                        rs.getTimestamp("fechaGeneracion")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}