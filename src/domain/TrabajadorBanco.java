package domain;

public class TrabajadorBanco {
    private String username;
    private String nombre;
    private String apellido;
    private String password;
    private String role;
    private int numsucursal;

    public TrabajadorBanco(String username, String nombre, String apellido, String password, String role, int numsucursal) {
        this.username = username;
        this.nombre = nombre;
        this.apellido = apellido;
        this.password = password;
        this.role = role;
        this.numsucursal = numsucursal;
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public int getNumsucursal() {
		return numsucursal;
	}

	public void setNumsucursal(int numsucursal) {
		this.numsucursal = numsucursal;
	}

}