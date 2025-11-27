package database;

import objetos.ClienteBanco;
import objetos.CuentaCorriente;
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

    public double getSaldoTotalPorDni(String dni) {
        String sql = "SELECT SUM(saldo) AS saldo_total FROM cuenta WHERE dni = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dni);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("saldo_total");
            }

        } catch (SQLException e) {
            System.err.println("Error en getSaldoTotalPorDni: " + e.getMessage());
        }
        return 0.0;
    }

    public double getSaldoCuenta(String numCuenta) {
        String sql = "SELECT saldo FROM cuenta WHERE numcuenta = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, numCuenta);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("saldo");
            }

        } catch (SQLException e) {
            System.err.println("Error en getSaldoCuenta: " + e.getMessage());
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
                        rs.getString("numcuenta") 
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

    public String crearClienteConCuenta(String dni, String nombre, String apellido, String password, String numSucursal, String numCuenta) {
        Connection conn = null;
        PreparedStatement psCliente = null;
        PreparedStatement psCuenta = null;

        try {
            conn = connect(); 
            if (conn == null) return null;

            conn.setAutoCommit(false);

            String sqlCliente = "INSERT INTO cliente(dni, nombre, apellido, password) VALUES (?, ?, ?, ?)";
            psCliente = conn.prepareStatement(sqlCliente);
            psCliente.setString(1, dni);
            psCliente.setString(2, nombre);
            psCliente.setString(3, apellido);
            psCliente.setString(4, password);
            psCliente.executeUpdate();

            String sqlCuenta = "INSERT INTO cuenta(numcuenta, saldo, dni, numsucursal) VALUES (?, ?, ?, ?)";
            psCuenta = conn.prepareStatement(sqlCuenta);
            psCuenta.setString(1, numCuenta);
            psCuenta.setDouble(2, 0.0); 
            psCuenta.setString(3, dni);
            psCuenta.setString(4, numSucursal);
            psCuenta.executeUpdate();

            conn.commit(); 
            return numCuenta; 

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback(); 
            } catch (SQLException ignored) {}
            System.err.println("Error al crear el cliente en BBDD: " + e.getMessage());
            return null; 

        } finally {
           
            try {
                if (psCliente != null) psCliente.close();
                if (psCuenta != null) psCuenta.close();
                if (conn != null) conn.close();
            } catch (SQLException ignored) {}
        }
    }

    public ClienteBanco getClientePorDNI(String dni) {
        String sql = "SELECT nombre, apellido, password FROM cliente WHERE dni = ?";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String password = rs.getString("password");

                return new ClienteBanco(dni, nombre, apellido, password);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public CuentaCorriente getCuentaPorNumero(String numCuenta) {
        String sql = "SELECT saldo, dni, numsucursal FROM cuenta WHERE numcuenta = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, numCuenta);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double saldo = rs.getDouble("saldo");
                String dni = rs.getString("dni");
                int numSucursal = rs.getInt("numsucursal");

                return new CuentaCorriente(numCuenta, saldo, dni, numSucursal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean modificarCliente(ClienteBanco cliente) {
        String sql = "UPDATE cliente SET nombre=?, apellido=?, password=? WHERE dni=?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = connect(); 
            if (conn == null) return false; 

            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getApellido());
            pstmt.setString(3, cliente.getPassword());
            pstmt.setString(4, cliente.getDni()); 

            int filasAfectadas = pstmt.executeUpdate();

            return filasAfectadas > 0; 
            
        } catch (SQLException e) {
            System.err.println("Error SQL al modificar el cliente: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ignored) {}
        }
    }

    public boolean actualizarSaldoCuenta(String numCuenta, double nuevoSaldo) {
        String sql = "UPDATE cuenta SET saldo = ? WHERE numcuenta = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, nuevoSaldo);
            pstmt.setString(2, numCuenta);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error en actualizarSaldoCuenta: " + e.getMessage());
            return false;
        }
    }

    public boolean insertarMovimiento(String numCuenta, double cantidad) {
        String sql = "INSERT INTO movimiento (cantidad, fecha, numcuenta) " +
                     "VALUES (?, datetime('now'), ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, cantidad);
            pstmt.setString(2, numCuenta);

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getCuentaPrincipal(String dni) {
        String sql = "SELECT numcuenta FROM cuenta WHERE dni = ? LIMIT 1";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getString("numcuenta");
        } catch (Exception e) { e.printStackTrace(); }

        return null;
    }
    public boolean realizarTransferencia(String cuentaOrigen, String cuentaDestino, double cantidad, String concepto) {
        String sqlInsert = "INSERT INTO movimiento (cantidad, fecha, numcuenta) VALUES (?, datetime('now'), ?)";
        String sqlUpdate = "UPDATE cuenta SET saldo = saldo + ? WHERE numcuenta = ?";

        try (Connection conn = connect()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(sqlInsert);
                 PreparedStatement ps2 = conn.prepareStatement(sqlInsert);
                 PreparedStatement ps3 = conn.prepareStatement(sqlUpdate);
                 PreparedStatement ps4 = conn.prepareStatement(sqlUpdate)) {

                ps1.setDouble(1, -cantidad);
                ps1.setString(2, cuentaOrigen);
                ps1.executeUpdate();

                ps2.setDouble(1, cantidad);
                ps2.setString(2, cuentaDestino);
                ps2.executeUpdate();

                ps3.setDouble(1, -cantidad);
                ps3.setString(2, cuentaOrigen);
                ps3.executeUpdate();

                ps4.setDouble(1, cantidad);
                ps4.setString(2, cuentaDestino);
                ps4.executeUpdate();

                conn.commit();
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
