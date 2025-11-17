package AppSwing;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private LoginService loginService;
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginFrame(){
        loginService = new LoginService();
        setupWindow();
        createComponents();
        setVisible(true);
    }

    private void setupWindow(){
        setTitle("Deusto Bank - Login");
        setSize(350,200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void createComponents(){
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel( new BorderLayout(10,10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JLabel title = new JLabel("DEUSTO BANK", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel( new GridLayout(3,2, 10, 10));
        formPanel.add(new JLabel("Usuario: "));
        txtUsername = new JTextField();
        formPanel.add(txtUsername);

        formPanel.add(new JLabel("Contraseña: "));
        txtPassword = new JPasswordField();
        formPanel.add(txtPassword);

        JButton btnLogin = new JButton("Entrar");
        JButton btnClear = new JButton("Limpiar");

        formPanel.add(btnLogin);
        formPanel.add(btnClear);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        btnLogin.addActionListener(e -> attemptLogin());
        btnClear.addActionListener(e -> clearFields());
        txtPassword.addActionListener(e -> attemptLogin());
    }

    private void attemptLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete los campos");
            return;
        }

        User user = loginService.authenticate(username, password);

        if (user != null) {
            JOptionPane.showMessageDialog(this, "¡Bienvenido " + user.getName() + "!");
            openUserWindow(user);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos");
        }
    }

    private void openUserWindow(User user) {
        if ("CLIENTE".equals(user.getRole())) {
            new ClienteFrame(user);
        } else if ("TRABAJADOR".equals(user.getRole())) {
            new TrabajadorFrame(user);//falta por crear
        }
    }

    private void clearFields() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtUsername.requestFocus();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new LoginFrame();
    }

}
