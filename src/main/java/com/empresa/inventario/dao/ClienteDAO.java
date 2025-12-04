package com.empresa.inventario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.empresa.inventario.model.Cliente;

public class ClienteDAO {

    // Método para insertar un cliente
    public boolean insertarCliente(Cliente cliente) {
        String sql = "INSERT INTO Cliente (razonSocial, ruc, direccion, telefono, email) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cliente.getRazonSocial());
            ps.setString(2, cliente.getRuc());
            ps.setString(3, cliente.getDireccion());
            ps.setString(4, cliente.getTelefono());
            ps.setString(5, cliente.getEmail());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para actualizar un cliente
    public boolean actualizarCliente(Cliente cliente) {
        String sql = "UPDATE Cliente SET razonSocial=?, ruc=?, direccion=?, telefono=?, email=? WHERE idCliente=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cliente.getRazonSocial());
            ps.setString(2, cliente.getRuc());
            ps.setString(3, cliente.getDireccion());
            ps.setString(4, cliente.getTelefono());
            ps.setString(5, cliente.getEmail());
            ps.setInt(6, cliente.getIdCliente());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para eliminar un cliente
    public boolean eliminarCliente(int idCliente) {
        String sql = "DELETE FROM Cliente WHERE idCliente=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para obtener un cliente por ID
    public Cliente obtenerClientePorId(int idCliente) {
        String sql = "SELECT * FROM Cliente WHERE idCliente=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Cliente(
                    rs.getInt("idCliente"),
                    rs.getString("razonSocial"),
                    rs.getString("ruc"),
                    rs.getString("direccion"),
                    rs.getString("telefono"),
                    rs.getString("email")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Método para listar todos los clientes
    public List<Cliente> listarClientes() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM Cliente";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cliente cliente = new Cliente(
                    rs.getInt("idCliente"),
                    rs.getString("razonSocial"),
                    rs.getString("ruc"),
                    rs.getString("direccion"),
                    rs.getString("telefono"),
                    rs.getString("email")
                );
                lista.add(cliente);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}