package AppSwing;


public class Trabajador {
    private String id;
    private String nombre;
    private String password;

    public Trabajador(String id, String nombre, String password) {
        this.id = id;
        this.nombre = nombre;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPassword() {
        return password;
    }

    // Puedes añadir métodos para acciones propias del trabajador según crezcas la app
}
