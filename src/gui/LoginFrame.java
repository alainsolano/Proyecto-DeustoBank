package gui;

import datos.LoginService;
import objetos.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame implements ActionListener {

    private LoginService loginService;
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    private JButton btnLogin;
    private JButton btnClear;

    // --- Paleta de Colores del Tema Oscuro ---
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45); // Main BG
    private static final Color FIELD_BACKGROUND = new Color(60, 63, 65); // Card BG
    private static final Color FIELD_LIGHTER_BACKGROUND = new Color(75, 78, 80); // Text Field BG
    private static final Color FOREGROUND_TEXT = new Color(200, 200, 200); // Label Text
    private static final Color ACCENT_COLOR = new Color(135, 206, 250); // Title/Border (Sky Blue)

    private static final Color BUTTON_BASE_COLOR = new Color(105, 105, 255); // Primary Button BG (0x6969FF)
    private static final Color BUTTON_HOVER_COLOR = new Color(123, 123, 255); // Lighter Blue (0x7B7BFF)
    private static final Color BUTTON_PRESSED_COLOR = new Color(80, 80, 216); // Darker Blue (0x5050D8)


    public LoginFrame() {
        loginService = new LoginService();
        setupWindow();
        createComponents();
        setVisible(true);
    }

    private void setupWindow() {
        setTitle("Deusto Bank - Login");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);


        getContentPane().setBackground(DARK_BACKGROUND);
        setLayout(new GridBagLayout());
    }

    private void createComponents() {

        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setPreferredSize(new Dimension(350, 250));
        card.setBackground(FIELD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));


        JLabel title = new JLabel("DEUSTO BANK", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(ACCENT_COLOR);
        card.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(FIELD_BACKGROUND);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setForeground(FOREGROUND_TEXT);
        formPanel.add(lblUser, gbc);

        gbc.gridx = 1;
        txtUsername = new JTextField(12);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtUsername.setBackground(FIELD_LIGHTER_BACKGROUND);
        txtUsername.setForeground(Color.WHITE);
        txtUsername.setCaretColor(Color.WHITE);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));        formPanel.add(txtUsername, gbc);


        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPass.setForeground(FOREGROUND_TEXT);
        formPanel.add(lblPass, gbc);

        gbc.gridx = 1;
        txtPassword = new JPasswordField(12);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtPassword.setBackground(FIELD_LIGHTER_BACKGROUND);
        txtPassword.setForeground(Color.WHITE);
        txtPassword.setCaretColor(Color.WHITE);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));        formPanel.add(txtPassword, gbc);



        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(FIELD_BACKGROUND);

        btnLogin = new JButton("Entrar");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(BUTTON_BASE_COLOR);
        btnLogin.setForeground(DARK_BACKGROUND);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));

        applyHoverEffect(btnLogin, BUTTON_BASE_COLOR, BUTTON_HOVER_COLOR, BUTTON_PRESSED_COLOR);

        btnClear = new JButton("Limpiar");
        btnClear.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btnClear.setBackground(FIELD_LIGHTER_BACKGROUND);
        btnClear.setForeground(Color.WHITE);
        btnClear.setFocusPainted(false);
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnClear);
        btnClear.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FOREGROUND_TEXT, 1),
                BorderFactory.createEmptyBorder(7, 17, 7, 17)
        ));

        // Remove existing MouseListener and add custom hover effect for clear button
        for (MouseListener ml : btnClear.getMouseListeners()) {
            if (ml.getClass().getSimpleName().contains("MouseAdapter")) {
                btnClear.removeMouseListener(ml);
            }
        }
        applyHoverEffect(btnClear, FIELD_LIGHTER_BACKGROUND, FIELD_BACKGROUND, FIELD_BACKGROUND.darker());

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        card.add(formPanel, BorderLayout.CENTER);


        add(card);


        btnLogin.addActionListener(this);
        btnClear.addActionListener(this);
        txtPassword.addActionListener(this); //
    }

    private void applyHoverEffect(JButton button, Color normalColor, Color hoverColor, Color pressedColor) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(normalColor);
            }
            @Override
            public void mousePressed(MouseEvent evt) {
                button.setBackground(pressedColor);
            }
            @Override
            public void mouseReleased(MouseEvent evt) {
                if (button.contains(evt.getPoint())) {
                    button.setBackground(hoverColor);
                } else {
                    button.setBackground(normalColor);
                }
            }
        });
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == btnLogin || src == txtPassword) {
            attemptLogin();
        } else if (src == btnClear) {
            clearFields();
        }
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
            new TrabajadorFrame(user);
        }
    }

    private void clearFields() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtUsername.requestFocus();
    }


    public static void main(String[] args) {
        try {
            // Forzar Look and Feel (L&F) Nimbus para una mejor compatibilidad con los colores
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
        }
        new LoginFrame();
    }
}