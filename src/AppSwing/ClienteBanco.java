package AppSwing;



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

    
    //queda por definir
	public int  getSaldo() {
		// TODO Auto-generated method stub
		return 100;
	}
	
	//FALTA METODO SETSALDO

	//queda por definir
	public String[] getHistorialTransferencias() {
		// TODO Auto-generated method stub
		return null;
	}
}