package AppSwing;
import java.util.HashMap;
import java.util.Map;

public class LoginService {

    private DatabaseManager dbManager;

    public LoginService() {
        dbManager = new DatabaseManager();
    }

    public User authenticate(String username, String password) {
        User user = dbManager.authenticateTrabajador(username, password);

        if (user != null) {
            return user;
        }

        user  = dbManager.authenticateCliente(username, password);

        if (user != null) {
            return user;
        }

        return null;
    }
}
