package database;

import objetos.User;

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

    /**
     * Inserta un nuevo cliente y le asigna una cuenta bancaria con saldo 0.
     * Utiliza una transacción para asegurar que ambos INSERTs se completen o ninguno lo haga.
     * @return El número de cuenta generado si la operación fue exitosa, o null en caso de error.
     */
    public String crearClienteConCuenta(String dni, String nombre, String apellido, String password, String numSucursal, String numCuenta) {
        Connection conn = null;
        PreparedStatement psCliente = null;
        PreparedStatement psCuenta = null;

        try {
            conn = connect(); // 1. Abrir conexión
            if (conn == null) return null;

            conn.setAutoCommit(false); // 2. Iniciar Transacción

            // --- 2.1 INSERT en la tabla cliente ---
            String sqlCliente = "INSERT INTO cliente(dni, nombre, apellido, password) VALUES (?, ?, ?, ?)";
            psCliente = conn.prepareStatement(sqlCliente);
            psCliente.setString(1, dni);
            psCliente.setString(2, nombre);
            psCliente.setString(3, apellido);
            psCliente.setString(4, password);
            psCliente.executeUpdate();

            // --- 2.2 INSERT en la tabla cuenta ---
            String sqlCuenta = "INSERT INTO cuenta(numcuenta, saldo, dni, numsucursal) VALUES (?, ?, ?, ?)";
            psCuenta = conn.prepareStatement(sqlCuenta);
            psCuenta.setString(1, numCuenta);
            psCuenta.setDouble(2, 0.0); // Saldo inicial a 0.0
            psCuenta.setString(3, dni);
            psCuenta.setString(4, numSucursal);
            psCuenta.executeUpdate();

            conn.commit(); // 3. Confirmar (Guardar) la Transacción
            return numCuenta; // Indica éxito

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback(); // 4. Deshacer si hay error
            } catch (SQLException ignored) {}
            System.err.println("Error al crear el cliente en BBDD: " + e.getMessage());
            return null; // Indica fallo

        } finally {
            // 5. Cerrar recursos
            try {
                if (psCliente != null) psCliente.close();
                if (psCuenta != null) psCuenta.close();
                if (conn != null) conn.close();
            } catch (SQLException ignored) {}
        }
    }

}
