package objetos;

import java.util.List;

import database.DatabaseManager;

public class ClienteBanco {

    private String dni;
    private String nombre;
    private String apellido;
    private String password;

    public ClienteBanco(String dni, String nombre, String apellido, String password) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.password = password;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public double getSaldo() {
        DatabaseManager db = new DatabaseManager();
        String cuenta = db.getCuentaPrincipal(this.dni);
        if (cuenta == null) return 0.0;
        return db.getSaldoCuenta(cuenta);
    }


    public String[] getHistorialTransferencias() {

        DatabaseManager db = new DatabaseManager();
        List<Object[]> movimientos = db.getMovimientos(this.dni);

        if (movimientos == null || movimientos.isEmpty()) {
            return new String[]{"No hay movimientos registrados."};
        }

        String[] historial = new String[movimientos.size()];

        for (int i = 0; i < movimientos.size(); i++) {
            Object[] mov = movimientos.get(i);
            String fecha = mov[0].toString();
            String cantidad = mov[1].toString();
            String numCuenta = mov[2].toString();

            historial[i] = fecha + " | " + cantidad + "€ | Cuenta: " + numCuenta;
        }

        return historial;
    }
    public double getSaldoCuentaPrincipal() {
        DatabaseManager db = new DatabaseManager();
        String cuenta = db.getCuentaPrincipal(this.dni);
        return db.getSaldoCuenta(cuenta);
    }



}