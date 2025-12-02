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
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        
        getContentPane().setBackground(new Color(45, 45, 45));
        setLayout(new GridBagLayout());
    }

    private void createComponents() {

        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setPreferredSize(new Dimension(350, 250));
        card.setBackground(new Color(60, 63, 65));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 90, 90)),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        
        JLabel title = new JLabel("DEUSTO BANK", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(135, 206, 250));
        card.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(60, 63, 65));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

      
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setForeground(new Color(200, 200, 200));
        formPanel.add(lblUser, gbc);

        gbc.gridx = 1;
        txtUsername = new JTextField(12);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtUsername.setBackground(new Color(75, 78, 80)); 
        txtUsername.setForeground(Color.WHITE);
        txtUsername.setCaretColor(Color.WHITE);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(135, 206, 250), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));        formPanel.add(txtUsername, gbc);

    
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPass.setForeground(new Color(200, 200, 200));
        formPanel.add(lblPass, gbc);

        gbc.gridx = 1;
        txtPassword = new JPasswordField(12);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtPassword.setBackground(new Color(75, 78, 80)); 
        txtPassword.setForeground(Color.WHITE);
        txtPassword.setCaretColor(Color.WHITE);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(135, 206, 250), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));        formPanel.add(txtPassword, gbc);

        
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(60, 63, 65));

        btnLogin = new JButton("Entrar");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(135, 206, 250));
        btnLogin.setForeground(new Color(45, 45, 45));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));

        btnClear = new JButton("Limpiar");
        btnClear.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btnClear.setBackground(new Color(75, 78, 80)); 
        btnClear.setForeground(Color.WHITE);
        btnClear.setFocusPainted(false);
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnClear);
        btnClear.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
                BorderFactory.createEmptyBorder(7, 17, 7, 17)
            ));
        btnClear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnClear.setBackground(new Color(90, 93, 95)); // Color un poco más claro al pasar el ratón
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnClear.setBackground(new Color(75, 78, 80));
            }
        });
        
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



