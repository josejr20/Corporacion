package com.empresa.inventario.dao;

import com.empresa.inventario.model.Factura;
import java.sql.*;

public class FacturaDAO {

    public boolean generarFactura(Factura factura) {
        String sql = "INSERT INTO Factura(idPedido, total, fechaEmision) VALUES (?, ?, NOW())";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, factura.getPedido().getIdPedido());
            ps.setDouble(2, factura.getTotal());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    factura.setIdFactura(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Factura buscarPorPedido(int idPedido) {
        String sql = "SELECT * FROM Factura WHERE idPedido=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Factura(rs.getInt("idFactura"), null, rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}