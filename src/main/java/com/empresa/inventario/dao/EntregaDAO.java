package com.empresa.inventario.dao;

import com.empresa.inventario.model.Entrega;
import com.empresa.inventario.model.Pedido;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * DAO extendido para gestión de entregas
 */
public class EntregaDAO {

    /**
     * Inserta una nueva entrega
     */
    public boolean insertar(Entrega entrega) {
        String sql = "INSERT INTO Entrega(idPedido, fechaEntrega, estadoEntrega) VALUES (?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entrega.getPedido().getIdPedido());
            ps.setDate(2, new java.sql.Date(entrega.getFechaEntrega().getTime()));
            ps.setString(3, entrega.getEstado());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar entrega: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lista todas las entregas
     */
    public List<Entrega> listar() {
        List<Entrega> lista = new ArrayList<>();
        String sql = "SELECT * FROM Entrega ORDER BY fechaEntrega DESC";
        try (Connection conn = ConexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setIdPedido(rs.getInt("idPedido"));
                
                Entrega entrega = new Entrega(
                    rs.getInt("idEntrega"),
                    pedido,
                    rs.getDate("fechaEntrega"),
                    rs.getString("estadoEntrega")
                );
                lista.add(entrega);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar entregas: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Actualiza el estado de una entrega
     */
    public boolean actualizarEstado(int idPedido, String nuevoEstado) {
        String sql = "UPDATE Entrega SET estadoEntrega=? WHERE idPedido=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idPedido);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado de entrega: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Asigna un repartidor a una entrega
     */
    public boolean asignarRepartidor(int idPedido, int idRepartidor) {
        // Primero verificar si existe la entrega
        String sqlCheck = "SELECT idEntrega FROM Entrega WHERE idPedido = ?";
        String sqlUpdate = "UPDATE Entrega SET idRepartidor = ?, estadoEntrega = 'EnCamino' WHERE idPedido = ?";
        String sqlInsert = "INSERT INTO Entrega (idPedido, idRepartidor, fechaProgramada, estadoEntrega) " +
                          "VALUES (?, ?, NOW(), 'Programada')";
        
        try (Connection conn = ConexionBD.getConnection()) {
            // Verificar si existe
            try (PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
                psCheck.setInt(1, idPedido);
                ResultSet rs = psCheck.executeQuery();
                
                if (rs.next()) {
                    // Ya existe, actualizar
                    try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
                        psUpdate.setInt(1, idRepartidor);
                        psUpdate.setInt(2, idPedido);
                        return psUpdate.executeUpdate() > 0;
                    }
                } else {
                    // No existe, insertar
                    try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
                        psInsert.setInt(1, idPedido);
                        psInsert.setInt(2, idRepartidor);
                        return psInsert.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al asignar repartidor: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Confirma la entrega de un pedido
     */
    public boolean confirmarEntrega(int idPedido, Date fechaEntregaReal) {
        String sql = "UPDATE Entrega SET " +
                     "estadoEntrega = 'Entregada', " +
                     "fechaEntregaReal = ? " +
                     "WHERE idPedido = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, new Timestamp(fechaEntregaReal.getTime()));
            ps.setInt(2, idPedido);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al confirmar entrega: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtiene una entrega por ID de pedido
     */
    public Entrega obtenerPorPedido(int idPedido) {
        String sql = "SELECT * FROM Entrega WHERE idPedido = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setIdPedido(rs.getInt("idPedido"));
                
                return new Entrega(
                    rs.getInt("idEntrega"),
                    pedido,
                    rs.getDate("fechaEntrega"),
                    rs.getString("estadoEntrega")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener entrega por pedido: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Lista entregas por repartidor
     */
    public List<Entrega> listarPorRepartidor(int idRepartidor) {
        List<Entrega> lista = new ArrayList<>();
        String sql = "SELECT * FROM Entrega " +
                     "WHERE idRepartidor = ? " +
                     "AND estadoEntrega IN ('Programada', 'EnCamino', 'IntentandoContacto1', 'IntentandoContacto2', 'IntentandoContacto3') " +
                     "ORDER BY fechaProgramada ASC";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idRepartidor);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setIdPedido(rs.getInt("idPedido"));
                
                Entrega entrega = new Entrega(
                    rs.getInt("idEntrega"),
                    pedido,
                    rs.getDate("fechaProgramada"),
                    rs.getString("estadoEntrega")
                );
                lista.add(entrega);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar entregas por repartidor: " + e.getMessage());
            e.printStackTrace();
        }
        
        return lista;
    }
    
    /**
     * Registra observaciones de intento de entrega
     */
    public boolean registrarObservaciones(int idPedido, String observaciones) {
        String sql = "UPDATE Entrega SET observaciones = ? WHERE idPedido = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, observaciones);
            ps.setInt(2, idPedido);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar observaciones: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Anula una entrega
     */
    public boolean anularEntrega(int idPedido) {
        String sql = "UPDATE Entrega SET estadoEntrega = 'Anulada' WHERE idPedido = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al anular entrega: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}