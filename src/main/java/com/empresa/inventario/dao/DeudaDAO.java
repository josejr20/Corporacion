package com.empresa.inventario.dao;

import com.empresa.inventario.model.DeudaCliente;
import com.empresa.inventario.model.Cliente;
import com.empresa.inventario.model.Pedido;
import com.empresa.inventario.model.Entrega;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * DAO para gestionar deudas de clientes
 */
public class DeudaDAO {
    
    /**
     * Registra una deuda de cliente
     */
    public boolean registrarDeuda(int idCliente, int idPedido, double montoDeuda, String motivoDeuda) {
        String sql = "INSERT INTO DeudaCliente (idCliente, idPedido, montoDeuda, motivoDeuda, estadoDeuda) " +
                     "VALUES (?, ?, ?, ?, 'Pendiente')";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, idCliente);
            ps.setInt(2, idPedido);
            ps.setDouble(3, montoDeuda);
            ps.setString(4, motivoDeuda);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    System.out.println("✓ Deuda registrada con ID: " + rs.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al registrar deuda: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Registra un pago hacia una deuda
     */
    public boolean registrarPago(int idPedido, double montoPago, String metodoPago) {
        // Primero obtener la deuda
        String sqlSelect = "SELECT idDeuda, montoDeuda, montoPagado FROM DeudaCliente " +
                          "WHERE idPedido = ? AND estadoDeuda != 'Pagada'";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement psSelect = conn.prepareStatement(sqlSelect)) {
            
            psSelect.setInt(1, idPedido);
            ResultSet rs = psSelect.executeQuery();
            
            if (rs.next()) {
                int idDeuda = rs.getInt("idDeuda");
                double montoDeuda = rs.getDouble("montoDeuda");
                double montoPagadoAnterior = rs.getDouble("montoPagado");
                double nuevoMontoPagado = montoPagadoAnterior + montoPago;
                
                // Determinar nuevo estado
                String nuevoEstado;
                if (nuevoMontoPagado >= montoDeuda) {
                    nuevoEstado = "Pagada";
                } else if (nuevoMontoPagado > 0) {
                    nuevoEstado = "Parcial";
                } else {
                    nuevoEstado = "Pendiente";
                }
                
                // Actualizar la deuda
                String sqlUpdate = "UPDATE DeudaCliente SET montoPagado = ?, estadoDeuda = ? " +
                                  "WHERE idDeuda = ?";
                
                try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
                    psUpdate.setDouble(1, nuevoMontoPagado);
                    psUpdate.setString(2, nuevoEstado);
                    psUpdate.setInt(3, idDeuda);
                    
                    boolean resultado = psUpdate.executeUpdate() > 0;
                    
                    if (resultado) {
                        System.out.println(String.format(
                            "✓ Pago registrado: S/ %.2f | Total pagado: S/ %.2f de S/ %.2f | Estado: %s",
                            montoPago, nuevoMontoPagado, montoDeuda, nuevoEstado
                        ));
                    }
                    
                    return resultado;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al registrar pago de deuda: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Lista las deudas de un cliente
     */
    public List<DeudaCliente> listarPorCliente(int idCliente) {
        List<DeudaCliente> lista = new ArrayList<>();
        String sql = "SELECT d.*, " +
                     "c.razonSocial, c.ruc, c.direccion, c.telefono, c.email, " +
                     "p.idPedido, p.fechaRegistro, p.montoTotal " +
                     "FROM DeudaCliente d " +
                     "INNER JOIN Cliente c ON d.idCliente = c.idCliente " +
                     "INNER JOIN Pedido p ON d.idPedido = p.idPedido " +
                     "WHERE d.idCliente = ? " +
                     "ORDER BY d.fechaRegistro DESC";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Cliente cliente = new Cliente(
                    rs.getInt("idCliente"),
                    rs.getString("razonSocial"),
                    rs.getString("ruc"),
                    rs.getString("direccion"),
                    rs.getString("telefono"),
                    rs.getString("email")
                );
                
                Pedido pedido = new Pedido();
                pedido.setIdPedido(rs.getInt("idPedido"));
                pedido.setFecha(rs.getTimestamp("fechaRegistro"));
                pedido.setMontoTotal(rs.getDouble("montoTotal"));
                
                DeudaCliente deuda = new DeudaCliente();
                deuda.setIdDeuda(rs.getInt("idDeuda"));
                deuda.setCliente(cliente);
                deuda.setPedido(pedido);
                deuda.setMontoDeuda(rs.getDouble("montoDeuda"));
                deuda.setMotivoDeuda(rs.getString("motivoDeuda"));
                deuda.setFechaRegistro(rs.getTimestamp("fechaRegistro"));
                deuda.setEstadoDeuda(rs.getString("estadoDeuda"));
                deuda.setMontoPagado(rs.getDouble("montoPagado"));
                
                lista.add(deuda);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar deudas por cliente: " + e.getMessage());
            e.printStackTrace();
        }
        
        return lista;
    }
    
    /**
     * Lista todas las deudas pendientes
     */
    public List<DeudaCliente> listarPendientes() {
        List<DeudaCliente> lista = new ArrayList<>();
        String sql = "SELECT d.*, " +
                     "c.razonSocial, c.ruc, c.direccion, c.telefono, c.email, " +
                     "p.idPedido, p.fechaRegistro, p.montoTotal " +
                     "FROM DeudaCliente d " +
                     "INNER JOIN Cliente c ON d.idCliente = c.idCliente " +
                     "INNER JOIN Pedido p ON d.idPedido = p.idPedido " +
                     "WHERE d.estadoDeuda IN ('Pendiente', 'Parcial') " +
                     "ORDER BY d.fechaRegistro DESC";
        
        try (Connection conn = ConexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                Cliente cliente = new Cliente(
                    rs.getInt("idCliente"),
                    rs.getString("razonSocial"),
                    rs.getString("ruc"),
                    rs.getString("direccion"),
                    rs.getString("telefono"),
                    rs.getString("email")
                );
                
                Pedido pedido = new Pedido();
                pedido.setIdPedido(rs.getInt("idPedido"));
                pedido.setFecha(rs.getTimestamp("fechaRegistro"));
                pedido.setMontoTotal(rs.getDouble("montoTotal"));
                
                DeudaCliente deuda = new DeudaCliente();
                deuda.setIdDeuda(rs.getInt("idDeuda"));
                deuda.setCliente(cliente);
                deuda.setPedido(pedido);
                deuda.setMontoDeuda(rs.getDouble("montoDeuda"));
                deuda.setMotivoDeuda(rs.getString("motivoDeuda"));
                deuda.setFechaRegistro(rs.getTimestamp("fechaRegistro"));
                deuda.setEstadoDeuda(rs.getString("estadoDeuda"));
                deuda.setMontoPagado(rs.getDouble("montoPagado"));
                
                lista.add(deuda);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar deudas pendientes: " + e.getMessage());
            e.printStackTrace();
        }
        
        return lista;
    }
    
    /**
     * Obtiene la deuda de un pedido específico
     */
    public DeudaCliente obtenerPorPedido(int idPedido) {
        String sql = "SELECT d.*, " +
                     "c.razonSocial, c.ruc, c.direccion, c.telefono, c.email, " +
                     "p.idPedido, p.fechaRegistro, p.montoTotal " +
                     "FROM DeudaCliente d " +
                     "INNER JOIN Cliente c ON d.idCliente = c.idCliente " +
                     "INNER JOIN Pedido p ON d.idPedido = p.idPedido " +
                     "WHERE d.idPedido = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idPedido);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Cliente cliente = new Cliente(
                    rs.getInt("idCliente"),
                    rs.getString("razonSocial"),
                    rs.getString("ruc"),
                    rs.getString("direccion"),
                    rs.getString("telefono"),
                    rs.getString("email")
                );
                
                Pedido pedido = new Pedido();
                pedido.setIdPedido(rs.getInt("idPedido"));
                pedido.setFecha(rs.getTimestamp("fechaRegistro"));
                pedido.setMontoTotal(rs.getDouble("montoTotal"));
                
                DeudaCliente deuda = new DeudaCliente();
                deuda.setIdDeuda(rs.getInt("idDeuda"));
                deuda.setCliente(cliente);
                deuda.setPedido(pedido);
                deuda.setMontoDeuda(rs.getDouble("montoDeuda"));
                deuda.setMotivoDeuda(rs.getString("motivoDeuda"));
                deuda.setFechaRegistro(rs.getTimestamp("fechaRegistro"));
                deuda.setEstadoDeuda(rs.getString("estadoDeuda"));
                deuda.setMontoPagado(rs.getDouble("montoPagado"));
                
                return deuda;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener deuda por pedido: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Calcula el total de deuda de un cliente
     */
    public double calcularTotalDeudaCliente(int idCliente) {
        String sql = "SELECT SUM(montoDeuda - montoPagado) as totalDeuda " +
                     "FROM DeudaCliente " +
                     "WHERE idCliente = ? AND estadoDeuda != 'Pagada'";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("totalDeuda");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al calcular total deuda: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0.0;
    }
}