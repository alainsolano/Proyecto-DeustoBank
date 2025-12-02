package gui;

import datos.LoginService;
import objetos.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame implements ActionListener {

    private LoginService loginService;
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    private JButton btnLogin;
    private JButton btnClear;

    // --- Paleta de Colores del Tema Oscuro (Inspiración iOS Dark Mode) ---
    private static final Color DARK_BACKGROUND = new Color(28, 28, 30); // iOS System Background
    private static final Color FIELD_BACKGROUND = new Color(44, 44, 46); // iOS Secondary System Background (Card BG)
    private static final Color FIELD_LIGHTER_BACKGROUND = new Color(58, 58, 60); // Text Field BG
    private static final Color FOREGROUND_TEXT = new Color(242, 242, 247); // iOS Label Color
    private static final Color ACCENT_COLOR = new Color(0, 122, 255); // iOS System Blue

    private static final Color BUTTON_BASE_COLOR = ACCENT_COLOR;
    private static final Color BUTTON_HOVER_COLOR = new Color(50, 150, 255);
    private static final Color BUTTON_PRESSED_COLOR = new Color(0, 92, 204);


    public LoginFrame() {
        loginService = new LoginService();
        setupWindow();
        createComponents();
        setVisible(true);
    }

    private void setupWindow() {
        setTitle("Deusto Bank - Login");
        setSize(400, 711); // 9:16 aspect ratio (400x711)
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);


        getContentPane().setBackground(DARK_BACKGROUND);
        setLayout(new GridBagLayout());
    }

    private void createComponents() {

        JPanel card = new JPanel(new BorderLayout(25, 25));
        card.setPreferredSize(new Dimension(350, 650)); // Adjusted to fit new window size
        card.setBackground(FIELD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BACKGROUND, 1), // Invisible line to leverage Nimbus rounding
                BorderFactory.createEmptyBorder(50, 25, 50, 25)
        ));

        // Simular redondeo con Nimbus L&F (si está activo)
        card.putClientProperty("JComponent.roundRect", Boolean.TRUE);



        JLabel title = new JLabel("DEUSTO BANK", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(ACCENT_COLOR);
        card.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(FIELD_BACKGROUND);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 5, 15, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2; // Fields take full width


        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblUser = new JLabel("Usuario");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblUser.setForeground(FOREGROUND_TEXT);
        formPanel.add(lblUser, gbc);

        gbc.gridy = 1;
        txtUsername = new JTextField(12);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtUsername.setBackground(FIELD_LIGHTER_BACKGROUND);
        txtUsername.setForeground(FOREGROUND_TEXT);
        txtUsername.setCaretColor(ACCENT_COLOR);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        txtUsername.putClientProperty("JTextField.variant", "search");
        txtUsername.putClientProperty("JComponent.roundRect", Boolean.TRUE);
        formPanel.add(txtUsername, gbc);


        gbc.gridy = 2;
        JLabel lblPass = new JLabel("Contraseña");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblPass.setForeground(FOREGROUND_TEXT);
        formPanel.add(lblPass, gbc);

        gbc.gridy = 3;
        txtPassword = new JPasswordField(12);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtPassword.setBackground(FIELD_LIGHTER_BACKGROUND);
        txtPassword.setForeground(FOREGROUND_TEXT);
        txtPassword.setCaretColor(ACCENT_COLOR);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        txtPassword.putClientProperty("JPasswordField.variant", "search");
        txtPassword.putClientProperty("JComponent.roundRect", Boolean.TRUE);
        formPanel.add(txtPassword, gbc);



        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setBackground(FIELD_BACKGROUND);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        btnLogin = new JButton("Entrar");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnLogin.setBackground(BUTTON_BASE_COLOR);
        btnLogin.setForeground(DARK_BACKGROUND);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(new EmptyBorder(12, 10, 12, 10));
        btnLogin.putClientProperty("JComponent.roundRect", Boolean.TRUE);

        applyHoverEffect(btnLogin, BUTTON_BASE_COLOR, BUTTON_HOVER_COLOR, BUTTON_PRESSED_COLOR);

        btnClear = new JButton("Limpiar");
        btnClear.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btnClear.setBackground(FIELD_LIGHTER_BACKGROUND);
        btnClear.setForeground(FOREGROUND_TEXT);
        btnClear.setFocusPainted(false);
        btnClear.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BACKGROUND.darker(), 1),
                new EmptyBorder(12, 10, 12, 10)
        ));
        btnClear.putClientProperty("JComponent.roundRect", Boolean.TRUE);

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnClear);

        // Remove existing MouseListener and add custom hover effect for clear button
        for (MouseListener ml : btnClear.getMouseListeners()) {
            if (ml.getClass().getSimpleName().contains("MouseAdapter")) {
                btnClear.removeMouseListener(ml);
            }
        }
        applyHoverEffect(btnClear, FIELD_LIGHTER_BACKGROUND, FIELD_BACKGROUND, FIELD_BACKGROUND.darker());

        gbc.gridy = 4;
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