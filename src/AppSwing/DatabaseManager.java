package AppSwing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:sqlite/banco.db/";

    private Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error de conexion: " + e.getMessage());
            e.printStackTrace();

            JOptionPane.showMessageDialog(null,
                    "Error al conectar con la Base de Datos SQLite./n" +
                    "Verifica que el archivo 'banco.db' exista y que el driver esté instalado.",
                    "Error de conexion", JOptionPane.ERROR_MESSAGE);
        }
        return conn;
    }


    public User authenticateCliente(String dni, String password) {
        String sql = "SELECT dni, nombre, apellido FROM cliente WHERE dni = ? AND password = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dni);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getString("dni"),
                        null,
                        "CLIENTE",
                        rs.getString("nombre") + " " + rs.getString("apellido")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error en authenticateCliente: " + e.getMessage());
        }
        return null;
    }


    public User authenticateTrabajador(String username, String password) {
        String sql = "SELECT username, nombre, apellido, role "
                   + "FROM trabajador WHERE username = ? AND password = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getString("username"),
                        null,
                        rs.getString("role"),
                        rs.getString("nombre") + " " + rs.getString("apellido")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error en authenticateTrabajador: " + e.getMessage());
        }
        return null;
    }


    public List<Object[]> getClientesConCuenta(String trabajadorUsername) {
        List<Object[]> clientes = new ArrayList<>();

        String sql = "SELECT c.dni, c.nombre, c.apellido, cu.numcuenta, cu.saldo "
                   + "FROM cliente c "
                   + "JOIN cuenta cu ON c.dni = cu.dni "
                   + "WHERE cu.numsucursal = (SELECT t.numsucursal FROM trabajador t WHERE t.username = ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, trabajadorUsername);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                clientes.add(new Object[]{
                        rs.getString("dni"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("numcuenta"),
                        rs.getDouble("saldo")
                });
            }

        } catch (SQLException e) {
            System.err.println("Error en getClientesConCuenta: " + e.getMessage());
            e.printStackTrace();
        }
        return clientes;
    }

    public double getSaldo(String dni) {
        String sql = "SELECT SUM(saldo) AS saldo_total FROM cuenta WHERE dni = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dni);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) return rs.getDouble("saldo_total");

        } catch (SQLException e) {
            System.err.println("Error en getSaldo: " + e.getMessage());
        }
        return 0.0;
    }


    public List<Object[]> getMovimientos(String dni) {
        List<Object[]> movimientos = new ArrayList<>();

        String sql = "SELECT m.fecha, m.cantidad, m.numcuenta "
                   + "FROM movimiento m "
                   + "JOIN cuenta cu ON m.numcuenta = cu.numcuenta "
                   + "WHERE cu.dni = ? "
                   + "ORDER BY m.fecha DESC";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dni);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                movimientos.add(new Object[]{
                        rs.getString("fecha"),
                        rs.getDouble("cantidad"),
                        rs.getString("numcuenta") // ← Añadido. Antes faltaba.
                });
            }

        } catch (SQLException e) {
            System.err.println("Error en getMovimientos: " + e.getMessage());
        }
        return movimientos;
    }


    public String[] getInfoSucursalTrabajador(String username) {
        String sql = "SELECT s.poblacion, s.provincia, s.numsucursal "
                   + "FROM sucursal s "
                   + "JOIN trabajador t ON s.numsucursal = t.numsucursal "
                   + "WHERE t.username = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new String[]{
                        rs.getString("poblacion"),
                        rs.getString("provincia"),
                        rs.getString("numsucursal")
                };
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener info de sucursal: " + e.getMessage());
        }
        return null;
    }

}
