package com.empresa.inventario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.empresa.inventario.model.Cliente;
import com.empresa.inventario.model.Empleado;
import com.empresa.inventario.model.Pedido;

public class PedidoDAO {

    public boolean insertar(Pedido pedido) {
        String sql = "INSERT INTO Pedido(idCliente, idEmpleado, fechaRegistro, estadoPedido) VALUES (?, ?, NOW(), ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, pedido.getCliente().getIdCliente());
            ps.setInt(2, pedido.getEmpleado().getIdEmpleado());
            ps.setString(3, pedido.getEstadoPedido());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    pedido.setIdPedido(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Pedido> listar() {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT p.idPedido, p.fechaRegistro, p.estadoPedido, " +
                     "c.idCliente, c.razonSocial, c.ruc, c.direccion, c.telefono, c.email, " +
                     "e.idEmpleado, e.nombre, e.cargo " +
                     "FROM Pedido p INNER JOIN Cliente c ON p.idCliente=c.idCliente " +
                     "INNER JOIN Empleado e ON p.idEmpleado=e.idEmpleado";
        try (Connection conn = ConexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                // CORREGIDO: Cliente ahora tiene 6 parámetros
                Cliente cliente = new Cliente(
                    rs.getInt("idCliente"), 
                    rs.getString("razonSocial"), 
                    rs.getString("ruc"),
                    rs.getString("direccion"),
                    rs.getString("telefono"),
                    rs.getString("email")
                );
                
                // CORREGIDO: Empleado ahora tiene 3 parámetros
                Empleado empleado = new Empleado(
                    rs.getInt("idEmpleado"), 
                    rs.getString("nombre"), 
                    rs.getString("cargo")
                );
                
                // CORREGIDO: Pedido ahora tiene 6 parámetros
                Pedido pedido = new Pedido(
                    rs.getInt("idPedido"), 
                    cliente, 
                    empleado, 
                    rs.getDate("fechaRegistro"), 
                    null,  // detalles (se cargan aparte si es necesario)
                    rs.getString("estadoPedido")
                );
                
                lista.add(pedido);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean actualizarEstado(int idPedido, String nuevoEstado) {
        String sql = "UPDATE Pedido SET estadoPedido=? WHERE idPedido=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idPedido);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}