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

    public LoginFrame() {
        loginService = new LoginService();
        setupWindow();
        createComponents();
        setVisible(true);
    }

    private void setupWindow() {
        setTitle("Deusto Bank - Login");
        setSize(380, 260);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        
        getContentPane().setBackground(new Color(245, 245, 245));
        setLayout(new GridBagLayout());
    }

    private void createComponents() {

        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setPreferredSize(new Dimension(330, 230));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        
        JLabel title = new JLabel("DEUSTO BANK", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(0, 70, 140));
        card.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

      
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblUser, gbc);

        gbc.gridx = 1;
        txtUsername = new JTextField(12);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtUsername, gbc);

    
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblPass, gbc);

        gbc.gridx = 1;
        txtPassword = new JPasswordField(12);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtPassword, gbc);

        
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);

        btnLogin = new JButton("Entrar");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(0, 90, 180));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

        btnClear = new JButton("Limpiar");
        btnClear.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnClear);

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
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        new LoginFrame();
    }
}



