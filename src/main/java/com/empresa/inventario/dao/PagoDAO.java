package com.empresa.inventario.dao;

import com.empresa.inventario.model.Pago;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO {

    public boolean registrarPago(Pago pago) {
        String sql = "INSERT INTO Pago(idFactura, metodoPago, monto, fechaPago) VALUES (?, ?, ?, NOW())";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pago.getFactura().getIdFactura());
            ps.setString(2, pago.getMetodoPago());
            ps.setDouble(3, pago.getMonto());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Pago> listarPorFactura(int idFactura) {
        List<Pago> lista = new ArrayList<>();
        String sql = "SELECT * FROM Pago WHERE idFactura=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idFactura);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Pago(rs.getInt("idPago"), null, rs.getString("metodoPago"), rs.getDouble("monto")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}