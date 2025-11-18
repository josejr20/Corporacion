package com.empresa.inventario.dao;

import com.empresa.inventario.model.Empleado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO {

    public boolean insertar(Empleado empleado) {
        String sql = "INSERT INTO Empleado(nombre, cargo) VALUES (?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getCargo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Empleado> listar() {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT * FROM Empleado";
        try (Connection conn = ConexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Empleado(
                        rs.getInt("idEmpleado"),
                        rs.getString("nombre"),
                        rs.getString("cargo")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean actualizar(Empleado empleado) {
        String sql = "UPDATE Empleado SET nombre=?, cargo=? WHERE idEmpleado=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getCargo());
            ps.setInt(3, empleado.getIdEmpleado());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int idEmpleado) {
        String sql = "DELETE FROM Empleado WHERE idEmpleado=?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}