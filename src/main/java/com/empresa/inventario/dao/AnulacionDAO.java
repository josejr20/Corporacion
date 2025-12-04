package com.empresa.inventario.dao;

import com.empresa.inventario.model.AnulacionPedido;
import com.empresa.inventario.model.Pedido;
import com.empresa.inventario.model.Empleado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar anulaciones de pedidos
 */
public class AnulacionDAO {
    
    /**
     * Registra una anulación de pedido
     */
    public boolean registrarAnulacion(int idPedido, String motivoAnulacion, 
                                     String tipoAnulacion, int idEmpleadoAnula) {
        String sql = "INSERT INTO AnulacionPedido (idPedido, motivoAnulacion, tipoAnulacion, " +
                     "idEmpleadoAnula, estadoEvaluacion) VALUES (?, ?, ?, ?, 'Pendiente')";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, idPedido);
            ps.setString(2, motivoAnulacion);
            ps.setString(3, tipoAnulacion);
            ps.setInt(4, idEmpleadoAnula);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    System.out.println("✓ Anulación registrada con ID: " + rs.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al registrar anulación: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Evalúa una anulación (realizada por logística)
     */
    public boolean evaluarAnulacion(int idPedido, int idEmpleadoEvalua, 
                                   String observaciones, boolean aprobar) {
        String sql = "UPDATE AnulacionPedido SET " +
                     "estadoEvaluacion = ?, " +
                     "idEmpleadoEvalua = ?, " +
                     "fechaEvaluacion = NOW(), " +
                     "observacionesEvaluacion = ? " +
                     "WHERE idPedido = ? AND estadoEvaluacion = 'Pendiente'";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, aprobar ? "Aprobada" : "Rechazada");
            ps.setInt(2, idEmpleadoEvalua);
            ps.setString(3, observaciones);
            ps.setInt(4, idPedido);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al evaluar anulación: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Marca la restauración de stock como completada
     */
    public boolean completarRestauracion(int idPedido) {
        String sql = "UPDATE AnulacionPedido SET estadoEvaluacion = 'Completada' " +
                     "WHERE idPedido = ? AND estadoEvaluacion = 'EnRestauracion'";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idPedido);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al completar restauración: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Obtiene una anulación por ID de pedido
     */
    public AnulacionPedido obtenerPorPedido(int idPedido) {
        String sql = "SELECT a.*, " +
                     "ea.nombre as nombreEmpleadoAnula, ea.cargo as cargoEmpleadoAnula, " +
                     "ee.nombre as nombreEmpleadoEvalua, ee.cargo as cargoEmpleadoEvalua " +
                     "FROM AnulacionPedido a " +
                     "INNER JOIN Empleado ea ON a.idEmpleadoAnula = ea.idEmpleado " +
                     "LEFT JOIN Empleado ee ON a.idEmpleadoEvalua = ee.idEmpleado " +
                     "WHERE a.idPedido = ? " +
                     "ORDER BY a.fechaAnulacion DESC LIMIT 1";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idPedido);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setIdPedido(idPedido);
                
                Empleado empleadoAnula = new Empleado(
                    rs.getInt("idEmpleadoAnula"),
                    rs.getString("nombreEmpleadoAnula"),
                    rs.getString("cargoEmpleadoAnula")
                );
                
                Empleado empleadoEvalua = null;
                if (rs.getInt("idEmpleadoEvalua") > 0) {
                    empleadoEvalua = new Empleado(
                        rs.getInt("idEmpleadoEvalua"),
                        rs.getString("nombreEmpleadoEvalua"),
                        rs.getString("cargoEmpleadoEvalua")
                    );
                }
                
                AnulacionPedido anulacion = new AnulacionPedido();
                anulacion.setIdAnulacion(rs.getInt("idAnulacion"));
                anulacion.setPedido(pedido);
                anulacion.setMotivoAnulacion(rs.getString("motivoAnulacion"));
                anulacion.setTipoAnulacion(rs.getString("tipoAnulacion"));
                anulacion.setEmpleadoAnula(empleadoAnula);
                anulacion.setFechaAnulacion(rs.getTimestamp("fechaAnulacion"));
                anulacion.setEstadoEvaluacion(rs.getString("estadoEvaluacion"));
                anulacion.setEmpleadoEvalua(empleadoEvalua);
                anulacion.setFechaEvaluacion(rs.getTimestamp("fechaEvaluacion"));
                anulacion.setObservacionesEvaluacion(rs.getString("observacionesEvaluacion"));
                
                return anulacion;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener anulación: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Lista todas las anulaciones pendientes de evaluación
     */
    public List<AnulacionPedido> listarPendientes() {
        List<AnulacionPedido> lista = new ArrayList<>();
        String sql = "SELECT a.*, " +
                     "p.idCliente, " +
                     "ea.nombre as nombreEmpleadoAnula, ea.cargo as cargoEmpleadoAnula " +
                     "FROM AnulacionPedido a " +
                     "INNER JOIN Pedido p ON a.idPedido = p.idPedido " +
                     "INNER JOIN Empleado ea ON a.idEmpleadoAnula = ea.idEmpleado " +
                     "WHERE a.estadoEvaluacion = 'Pendiente' " +
                     "ORDER BY a.fechaAnulacion DESC";
        
        try (Connection conn = ConexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setIdPedido(rs.getInt("idPedido"));
                
                Empleado empleadoAnula = new Empleado(
                    rs.getInt("idEmpleadoAnula"),
                    rs.getString("nombreEmpleadoAnula"),
                    rs.getString("cargoEmpleadoAnula")
                );
                
                AnulacionPedido anulacion = new AnulacionPedido();
                anulacion.setIdAnulacion(rs.getInt("idAnulacion"));
                anulacion.setPedido(pedido);
                anulacion.setMotivoAnulacion(rs.getString("motivoAnulacion"));
                anulacion.setTipoAnulacion(rs.getString("tipoAnulacion"));
                anulacion.setEmpleadoAnula(empleadoAnula);
                anulacion.setFechaAnulacion(rs.getTimestamp("fechaAnulacion"));
                anulacion.setEstadoEvaluacion(rs.getString("estadoEvaluacion"));
                
                lista.add(anulacion);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar anulaciones pendientes: " + e.getMessage());
            e.printStackTrace();
        }
        
        return lista;
    }
    
    /**
     * Lista anulaciones aprobadas pendientes de restauración
     */
    public List<AnulacionPedido> listarParaRestaurar() {
        List<AnulacionPedido> lista = new ArrayList<>();
        String sql = "SELECT a.*, " +
                     "ea.nombre as nombreEmpleadoAnula, ea.cargo as cargoEmpleadoAnula, " +
                     "ee.nombre as nombreEmpleadoEvalua, ee.cargo as cargoEmpleadoEvalua " +
                     "FROM AnulacionPedido a " +
                     "INNER JOIN Empleado ea ON a.idEmpleadoAnula = ea.idEmpleado " +
                     "LEFT JOIN Empleado ee ON a.idEmpleadoEvalua = ee.idEmpleado " +
                     "WHERE a.estadoEvaluacion = 'EnRestauracion' " +
                     "ORDER BY a.fechaEvaluacion DESC";
        
        try (Connection conn = ConexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setIdPedido(rs.getInt("idPedido"));
                
                Empleado empleadoAnula = new Empleado(
                    rs.getInt("idEmpleadoAnula"),
                    rs.getString("nombreEmpleadoAnula"),
                    rs.getString("cargoEmpleadoAnula")
                );
                
                Empleado empleadoEvalua = new Empleado(
                    rs.getInt("idEmpleadoEvalua"),
                    rs.getString("nombreEmpleadoEvalua"),
                    rs.getString("cargoEmpleadoEvalua")
                );
                
                AnulacionPedido anulacion = new AnulacionPedido();
                anulacion.setIdAnulacion(rs.getInt("idAnulacion"));
                anulacion.setPedido(pedido);
                anulacion.setMotivoAnulacion(rs.getString("motivoAnulacion"));
                anulacion.setTipoAnulacion(rs.getString("tipoAnulacion"));
                anulacion.setEmpleadoAnula(empleadoAnula);
                anulacion.setFechaAnulacion(rs.getTimestamp("fechaAnulacion"));
                anulacion.setEstadoEvaluacion(rs.getString("estadoEvaluacion"));
                anulacion.setEmpleadoEvalua(empleadoEvalua);
                anulacion.setFechaEvaluacion(rs.getTimestamp("fechaEvaluacion"));
                anulacion.setObservacionesEvaluacion(rs.getString("observacionesEvaluacion"));
                
                lista.add(anulacion);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar anulaciones para restaurar: " + e.getMessage());
            e.printStackTrace();
        }
        
        return lista;
    }
}