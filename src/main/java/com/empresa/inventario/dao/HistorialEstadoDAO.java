package com.empresa.inventario.dao;

import com.empresa.inventario.model.HistorialEstadoPedido;
import com.empresa.inventario.model.Pedido;
import com.empresa.inventario.model.Empleado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar el historial de estados de pedidos
 */
public class HistorialEstadoDAO {
    
    /**
     * Registra un cambio de estado en el historial
     */
    public boolean registrar(int idPedido, String estadoAnterior, String estadoNuevo, 
                            int idEmpleado, String observaciones) {
        String sql = "INSERT INTO HistorialEstadoPedido (idPedido, estadoAnterior, estadoNuevo, idEmpleado, observaciones) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idPedido);
            ps.setString(2, estadoAnterior);
            ps.setString(3, estadoNuevo);
            ps.setInt(4, idEmpleado);
            ps.setString(5, observaciones);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al registrar historial: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Lista el historial de estados de un pedido
     */
    public List<HistorialEstadoPedido> listarPorPedido(int idPedido) {
        List<HistorialEstadoPedido> lista = new ArrayList<>();
        String sql = "SELECT h.idHistorial, h.idPedido, h.estadoAnterior, h.estadoNuevo, " +
                     "h.fechaCambio, h.observaciones, " +
                     "e.idEmpleado, e.nombre, e.cargo " +
                     "FROM HistorialEstadoPedido h " +
                     "INNER JOIN Empleado e ON h.idEmpleado = e.idEmpleado " +
                     "WHERE h.idPedido = ? " +
                     "ORDER BY h.fechaCambio DESC";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idPedido);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Empleado empleado = new Empleado(
                    rs.getInt("idEmpleado"),
                    rs.getString("nombre"),
                    rs.getString("cargo")
                );
                
                Pedido pedido = new Pedido();
                pedido.setIdPedido(rs.getInt("idPedido"));
                
                HistorialEstadoPedido historial = new HistorialEstadoPedido();
                historial.setIdHistorial(rs.getInt("idHistorial"));
                historial.setPedido(pedido);
                historial.setEstadoAnterior(rs.getString("estadoAnterior"));
                historial.setEstadoNuevo(rs.getString("estadoNuevo"));
                historial.setEmpleado(empleado);
                historial.setFechaCambio(rs.getTimestamp("fechaCambio"));
                historial.setObservaciones(rs.getString("observaciones"));
                
                lista.add(historial);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar historial: " + e.getMessage());
            e.printStackTrace();
        }
        
        return lista;
    }
    
    /**
     * Obtiene el último cambio de estado de un pedido
     */
    public HistorialEstadoPedido obtenerUltimoCambio(int idPedido) {
        String sql = "SELECT h.idHistorial, h.idPedido, h.estadoAnterior, h.estadoNuevo, " +
                     "h.fechaCambio, h.observaciones, " +
                     "e.idEmpleado, e.nombre, e.cargo " +
                     "FROM HistorialEstadoPedido h " +
                     "INNER JOIN Empleado e ON h.idEmpleado = e.idEmpleado " +
                     "WHERE h.idPedido = ? " +
                     "ORDER BY h.fechaCambio DESC LIMIT 1";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idPedido);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Empleado empleado = new Empleado(
                    rs.getInt("idEmpleado"),
                    rs.getString("nombre"),
                    rs.getString("cargo")
                );
                
                Pedido pedido = new Pedido();
                pedido.setIdPedido(rs.getInt("idPedido"));
                
                HistorialEstadoPedido historial = new HistorialEstadoPedido();
                historial.setIdHistorial(rs.getInt("idHistorial"));
                historial.setPedido(pedido);
                historial.setEstadoAnterior(rs.getString("estadoAnterior"));
                historial.setEstadoNuevo(rs.getString("estadoNuevo"));
                historial.setEmpleado(empleado);
                historial.setFechaCambio(rs.getTimestamp("fechaCambio"));
                historial.setObservaciones(rs.getString("observaciones"));
                
                return historial;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener último cambio: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Cuenta los cambios de estado de un pedido
     */
    public int contarCambios(int idPedido) {
        String sql = "SELECT COUNT(*) as total FROM HistorialEstadoPedido WHERE idPedido = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idPedido);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar cambios: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
}