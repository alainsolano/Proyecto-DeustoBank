package AppSwing;

public class CuentaCorriente {

    private int numCuenta;
    private double saldo;
    private String dni;        
    private int numSucursal;   

    public CuentaCorriente(int numCuenta, double saldo, String dni, int numSucursal) {
        this.numCuenta = numCuenta;
        this.saldo = saldo;
        this.dni = dni;
        this.numSucursal = numSucursal;
    }

    public int getNumCuenta() {
        return numCuenta;
    }

    public void setNumCuenta(int numCuenta) {
        this.numCuenta = numCuenta;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public int getNumSucursal() {
        return numSucursal;
    }

    public void setNumSucursal(int numSucursal) {
        this.numSucursal = numSucursal;
    }
}
