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

/**
 * DAO extendido para Pedidos con soporte completo para el flujo de estados
 */
public class PedidoDAO {

    /**
     * Inserta un nuevo pedido
     */
    public boolean insertar(Pedido pedido) {
        String sql = "INSERT INTO Pedido(idCliente, idEmpleadoRegistro, fechaRegistro, estadoPedido) " +
                     "VALUES (?, ?, NOW(), ?)";
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

    /**
     * Lista todos los pedidos con información completa
     */
    public List<Pedido> listar() {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT p.idPedido, p.fechaRegistro, p.estadoPedido, " +
                     "p.motivoRechazo, p.motivoAnulacion, p.intentosContacto, " +
                     "p.montoTotal, p.montoPagado, p.deudaPendiente, " +
                     "c.idCliente, c.razonSocial, c.ruc, c.direccion, c.telefono, c.email, " +
                     "e.idEmpleado, e.nombre, e.cargo " +
                     "FROM Pedido p " +
                     "INNER JOIN Cliente c ON p.idCliente=c.idCliente " +
                     "INNER JOIN Empleado e ON p.idEmpleadoRegistro=e.idEmpleado " +
                     "ORDER BY p.fechaRegistro DESC";
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
                
                Empleado empleado = new Empleado(
                    rs.getInt("idEmpleado"), 
                    rs.getString("nombre"), 
                    rs.getString("cargo")
                );
                
                Pedido pedido = new Pedido();
                pedido.setIdPedido(rs.getInt("idPedido"));
                pedido.setCliente(cliente);
                pedido.setEmpleado(empleado);
                pedido.setFecha(rs.getDate("fechaRegistro"));
                pedido.setEstadoPedido(rs.getString("estadoPedido"));
                pedido.setMotivoRechazo(rs.getString("motivoRechazo"));
                pedido.setMotivoAnulacion(rs.getString("motivoAnulacion"));
                pedido.setIntentosContacto(rs.getInt("intentosContacto"));
                pedido.setMontoTotal(rs.getDouble("montoTotal"));
                pedido.setMontoPagado(rs.getDouble("montoPagado"));
                pedido.setDeudaPendiente(rs.getDouble("deudaPendiente"));
                
                lista.add(pedido);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Actualiza el estado de un pedido
     */
    public boolean actualizarEstado(int idPedido, String nuevoEstado) {
        String sql = "UPDATE Pedido SET estadoPedido=?, fechaUltimaActualizacion=NOW() WHERE idPedido=?";
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
    
    /**
     * Rechaza un pedido con motivo
     */
    public boolean rechazarPedido(int idPedido, String estadoRechazo, String motivoRechazo) {
        String sql = "UPDATE Pedido SET estadoPedido=?, motivoRechazo=?, " +
                     "fechaUltimaActualizacion=NOW() WHERE idPedido=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estadoRechazo);
            ps.setString(2, motivoRechazo);
            ps.setInt(3, idPedido);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al rechazar pedido: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Incrementa el contador de intentos de contacto
     */
    public int incrementarIntentosContacto(int idPedido) {
        String sql = "UPDATE Pedido SET intentosContacto = intentosContacto + 1, " +
                     "estadoPedido = 'IntentandoEntrega', " +
                     "fechaUltimaActualizacion = NOW() " +
                     "WHERE idPedido = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            ps.executeUpdate();
            
            // Obtener el número actual de intentos
            return obtenerIntentosContacto(idPedido);
            
        } catch (SQLException e) {
            System.err.println("Error al incrementar intentos: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Obtiene el número de intentos de contacto de un pedido
     */
    public int obtenerIntentosContacto(int idPedido) {
        String sql = "SELECT intentosContacto FROM Pedido WHERE idPedido = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("intentosContacto");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener intentos: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Actualiza los montos de pago de un pedido
     */
    public boolean actualizarMontoPagado(int idPedido, double montoPagado, double deudaPendiente) {
        String sql = "UPDATE Pedido SET montoPagado = ?, deudaPendiente = ?, " +
                     "fechaUltimaActualizacion = NOW() WHERE idPedido = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, montoPagado);
            ps.setDouble(2, deudaPendiente);
            ps.setInt(3, idPedido);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar monto pagado: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtiene un pedido por ID con toda su información
     */
    public Pedido obtenerPorId(int idPedido) {
        String sql = "SELECT p.*, " +
                     "c.idCliente, c.razonSocial, c.ruc, c.direccion, c.telefono, c.email, " +
                     "e.idEmpleado, e.nombre, e.cargo " +
                     "FROM Pedido p " +
                     "INNER JOIN Cliente c ON p.idCliente = c.idCliente " +
                     "INNER JOIN Empleado e ON p.idEmpleadoRegistro = e.idEmpleado " +
                     "WHERE p.idPedido = ?";
        
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
                
                Empleado empleado = new Empleado(
                    rs.getInt("idEmpleado"),
                    rs.getString("nombre"),
                    rs.getString("cargo")
                );
                
                Pedido pedido = new Pedido();
                pedido.setIdPedido(rs.getInt("idPedido"));
                pedido.setCliente(cliente);
                pedido.setEmpleado(empleado);
                pedido.setFecha(rs.getTimestamp("fechaRegistro"));
                pedido.setEstadoPedido(rs.getString("estadoPedido"));
                pedido.setMotivoRechazo(rs.getString("motivoRechazo"));
                pedido.setMotivoAnulacion(rs.getString("motivoAnulacion"));
                pedido.setIntentosContacto(rs.getInt("intentosContacto"));
                pedido.setMontoTotal(rs.getDouble("montoTotal"));
                pedido.setMontoPagado(rs.getDouble("montoPagado"));
                pedido.setDeudaPendiente(rs.getDouble("deudaPendiente"));
                pedido.setFechaUltimaActualizacion(rs.getTimestamp("fechaUltimaActualizacion"));
                
                return pedido;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener pedido por ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Lista pedidos por uno o más estados
     */
    public List<Pedido> listarPorEstados(String... estados) {
        if (estados == null || estados.length == 0) {
            return new ArrayList<>();
        }
        
        StringBuilder sqlBuilder = new StringBuilder(
            "SELECT p.*, " +
            "c.idCliente, c.razonSocial, c.ruc, c.direccion, c.telefono, c.email, " +
            "e.idEmpleado, e.nombre, e.cargo " +
            "FROM Pedido p " +
            "INNER JOIN Cliente c ON p.idCliente = c.idCliente " +
            "INNER JOIN Empleado e ON p.idEmpleadoRegistro = e.idEmpleado " +
            "WHERE p.estadoPedido IN ("
        );
        
        for (int i = 0; i < estados.length; i++) {
            sqlBuilder.append("?");
            if (i < estados.length - 1) {
                sqlBuilder.append(",");
            }
        }
        sqlBuilder.append(") ORDER BY p.fechaRegistro DESC");
        
        List<Pedido> lista = new ArrayList<>();
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString())) {
            
            for (int i = 0; i < estados.length; i++) {
                ps.setString(i + 1, estados[i]);
            }
            
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
                
                Empleado empleado = new Empleado(
                    rs.getInt("idEmpleado"),
                    rs.getString("nombre"),
                    rs.getString("cargo")
                );
                
                Pedido pedido = new Pedido();
                pedido.setIdPedido(rs.getInt("idPedido"));
                pedido.setCliente(cliente);
                pedido.setEmpleado(empleado);
                pedido.setFecha(rs.getTimestamp("fechaRegistro"));
                pedido.setEstadoPedido(rs.getString("estadoPedido"));
                pedido.setMotivoRechazo(rs.getString("motivoRechazo"));
                pedido.setMotivoAnulacion(rs.getString("motivoAnulacion"));
                pedido.setIntentosContacto(rs.getInt("intentosContacto"));
                pedido.setMontoTotal(rs.getDouble("montoTotal"));
                pedido.setMontoPagado(rs.getDouble("montoPagado"));
                pedido.setDeudaPendiente(rs.getDouble("deudaPendiente"));
                
                lista.add(pedido);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar pedidos por estados: " + e.getMessage());
            e.printStackTrace();
        }
        
        return lista;
    }
    
    /**
     * Lista pedidos asignados a un repartidor
     */
    public List<Pedido> listarPorRepartidor(int idRepartidor) {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT p.*, " +
                     "c.idCliente, c.razonSocial, c.ruc, c.direccion, c.telefono, c.email, " +
                     "e.idEmpleado, e.nombre, e.cargo " +
                     "FROM Pedido p " +
                     "INNER JOIN Cliente c ON p.idCliente = c.idCliente " +
                     "INNER JOIN Empleado e ON p.idEmpleadoRegistro = e.idEmpleado " +
                     "INNER JOIN Entrega ent ON p.idPedido = ent.idPedido " +
                     "WHERE ent.idRepartidor = ? " +
                     "AND p.estadoPedido IN ('EnDistribucion', 'EnCamino', 'IntentandoEntrega') " +
                     "ORDER BY p.fechaRegistro DESC";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idRepartidor);
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
                
                Empleado empleado = new Empleado(
                    rs.getInt("idEmpleado"),
                    rs.getString("nombre"),
                    rs.getString("cargo")
                );
                
                Pedido pedido = new Pedido();
                pedido.setIdPedido(rs.getInt("idPedido"));
                pedido.setCliente(cliente);
                pedido.setEmpleado(empleado);
                pedido.setFecha(rs.getTimestamp("fechaRegistro"));
                pedido.setEstadoPedido(rs.getString("estadoPedido"));
                pedido.setIntentosContacto(rs.getInt("intentosContacto"));
                pedido.setMontoTotal(rs.getDouble("montoTotal"));
                pedido.setMontoPagado(rs.getDouble("montoPagado"));
                pedido.setDeudaPendiente(rs.getDouble("deudaPendiente"));
                
                lista.add(pedido);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar pedidos por repartidor: " + e.getMessage());
            e.printStackTrace();
        }
        
        return lista;
    }
    
    /**
     * Lista pedidos con deuda pendiente
     */
    public List<Pedido> listarConDeuda() {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT p.*, " +
                     "c.idCliente, c.razonSocial, c.ruc, c.direccion, c.telefono, c.email, " +
                     "e.idEmpleado, e.nombre, e.cargo " +
                     "FROM Pedido p " +
                     "INNER JOIN Cliente c ON p.idCliente = c.idCliente " +
                     "INNER JOIN Empleado e ON p.idEmpleadoRegistro = e.idEmpleado " +
                     "WHERE p.deudaPendiente > 0 AND p.estadoPedido = 'Entregado' " +
                     "ORDER BY p.deudaPendiente DESC";
        
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
                
                Empleado empleado = new Empleado(
                    rs.getInt("idEmpleado"),
                    rs.getString("nombre"),
                    rs.getString("cargo")
                );
                
                Pedido pedido = new Pedido();
                pedido.setIdPedido(rs.getInt("idPedido"));
                pedido.setCliente(cliente);
                pedido.setEmpleado(empleado);
                pedido.setFecha(rs.getTimestamp("fechaRegistro"));
                pedido.setEstadoPedido(rs.getString("estadoPedido"));
                pedido.setMontoTotal(rs.getDouble("montoTotal"));
                pedido.setMontoPagado(rs.getDouble("montoPagado"));
                pedido.setDeudaPendiente(rs.getDouble("deudaPendiente"));
                
                lista.add(pedido);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar pedidos con deuda: " + e.getMessage());
            e.printStackTrace();
        }
        
        return lista;
    }
}