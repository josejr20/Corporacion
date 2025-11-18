package com.empresa.inventario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.empresa.inventario.model.DetallePedido;
import com.empresa.inventario.model.Producto;

public class DetallePedidoDAO {

    public boolean insertar(DetallePedido detalle) {
        String sql = "INSERT INTO DetallePedido(idPedido, idProducto, cantidad, subtotal) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, detalle.getIdPedido());
            ps.setInt(2, detalle.getProducto().getIdProducto());
            ps.setInt(3, detalle.getCantidad());
            ps.setDouble(4, detalle.getSubtotal());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<DetallePedido> listarPorPedido(int idPedido) {
        List<DetallePedido> lista = new ArrayList<>();
        String sql = "SELECT d.idDetalle, d.idPedido, d.cantidad, d.subtotal, " +
                     "p.idProducto, p.nombre, p.descripcion, p.precio, p.stock " +
                     "FROM DetallePedido d INNER JOIN Producto p ON d.idProducto = p.idProducto " +
                     "WHERE d.idPedido=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // CORREGIDO: Producto ahora tiene 5 parámetros
                Producto producto = new Producto(
                    rs.getInt("idProducto"), 
                    rs.getString("nombre"), 
                    rs.getString("descripcion"),
                    rs.getDouble("precio"), 
                    rs.getInt("stock")
                );
                
                // CORREGIDO: DetallePedido ahora tiene 5 parámetros
                DetallePedido detalle = new DetallePedido(
                    rs.getInt("idDetalle"),
                    rs.getInt("idPedido"),
                    producto,
                    rs.getInt("cantidad"),
                    rs.getDouble("subtotal")
                );
                
                lista.add(detalle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}