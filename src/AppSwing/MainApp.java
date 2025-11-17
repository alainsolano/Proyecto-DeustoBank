package AppSwing;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MainApp extends JFrame {

    public MainApp(){
        initComponents();
    }


    public void initComponents() {
        setTitle("Deusto Banco");
        setSize(500, 840 );
        setLayout(null);
        setIconImage(new ImageIcon("Media\\Logos\\monedas.png").getImage());
        GridLayout gridLayout = new GridLayout(1, 1);
        setLayout(gridLayout);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Añadir componentes al JFrame

        add(mainPanel());
    }

    public JPanel mainPanel(){
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(10,20,30));

        // Añadir componentes al panel principal
        panel.add(appBar());
        panel.add(loginPanel());

        return panel;
    }

    public JPanel appBar(){
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(40,50,60));
        panel.setBounds(0,0,480 ,60);

        JLabel title = new JLabel("Deusto Banco");
        title.setForeground(new Color(222,222,222));
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel appLogo = new JLabel(
                Defaults.getResizeImage("Media\\Logos\\circulo-usd.png",
                        35,
                        35)
        );

        appLogo.setBounds(15,12,35,35);
        title.setBounds(60,15,200,30);

        // Añadiendo elementos a la App Bar
        panel.add(appLogo);
        panel.add(title);

        return panel;
    }

    public JPanel loginPanel(){
        JPanel panel = new JPanel(null);
        panel.setBounds(40, 150, 400, 400);
        panel.setBackground(new Color(25, 35, 45));

        // Crear borde redondeado para el panel principal
        panel.setBorder(new RoundedBorder(20, new Color(60, 70, 80)));

//        // Título del login
//        JLabel loginTitle = new JLabel("Iniciar Sesión");
//        loginTitle.setForeground(Color.WHITE);
//        loginTitle.setFont(new Font("Arial", Font.BOLD, 24));
//        loginTitle.setBounds(120, 30, 200, 40);
//        panel.add(loginTitle);

        // Campo de usuario
        JLabel userLabel = new JLabel("Usuario:");
        userLabel.setForeground(new Color(200, 200, 200));
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setBounds(50, 75, 100, 25);
        panel.add(userLabel);

        JTextField userField = new JTextField();
        userField.setBounds(50, 100, 300, 40);
        userField.setBackground(new Color(50, 60, 70));
        userField.setForeground(Color.WHITE);
        userField.setCaretColor(Color.WHITE);
        // Borde redondeado para el campo de texto
        userField.setBorder(new RoundedBorder(15, new Color(80, 90, 100)));
        panel.add(userField);

        // Campo de contraseña
        JLabel passLabel = new JLabel("Contraseña:");
        passLabel.setForeground(new Color(200, 200, 200));
        passLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passLabel.setBounds(50, 160, 100, 25);
        panel.add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(50, 185, 300, 40);
        passField.setBackground(new Color(50, 60, 70));
        passField.setForeground(Color.WHITE);
        passField.setCaretColor(Color.WHITE);
        // Borde redondeado para el campo de contraseña
        passField.setBorder(new RoundedBorder(15, new Color(80, 90, 100)));
        panel.add(passField);

        // Botón de login
        JButton loginButton = new JButton("Acceder");
        loginButton.setBounds(50, 280, 300, 45);
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        // Borde redondeado para el botón
        loginButton.setBorder(new RoundedBorder(20, new Color(50, 100, 150)));

        // Quitar el borde por defecto del botón y hacerlo completamente redondeado
        loginButton.setContentAreaFilled(false);
        loginButton.setOpaque(true);

        // Efecto hover para el botón (mejorado para bordes redondeados)
        loginButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(90, 150, 200));
                loginButton.setBorder(new RoundedBorder(20, new Color(70, 120, 170)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(new Color(70, 130, 180));
                loginButton.setBorder(new RoundedBorder(20, new Color(50, 100, 150)));
            }
        });

        // Acción del botón
        loginButton.addActionListener(e -> {
            String usuario = userField.getText();
            String password = new String(passField.getPassword());

            if(validarLogin(usuario, password)) {
                JOptionPane.showMessageDialog(this, "¡Bienvenido a Deusto Banco!", "Login Exitoso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos", "Error de Login", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(loginButton);

        // Enlace de "Olvidé mi contraseña"
        JLabel forgotPass = new JLabel("¿Olvidaste tu contraseña?");
        forgotPass.setForeground(new Color(150, 180, 220));
        forgotPass.setFont(new Font("Arial", Font.PLAIN, 12));
        forgotPass.setBounds(130, 340, 150, 20);

        // Efecto hover para el enlace
        forgotPass.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(MainApp.this, "Contacta con soporte técnico", "Recuperar Contraseña", JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {
                forgotPass.setForeground(new Color(180, 210, 250));
                forgotPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                forgotPass.setForeground(new Color(150, 180, 220));
            }
        });

        panel.add(forgotPass);

        return panel;
    }

    private boolean validarLogin(String usuario, String password) {
        // Credenciales de ejemplo - en una app real esto vendría de una base de datos
        return "admin".equals(usuario) && "1234".equals(password);
    }

    public JPanel sideBar(String[] values){
        JPanel panel = new JPanel();
        FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER,5, 5);

        panel.setLayout(flowLayout);
        panel.setBounds(0, 50, 300, 700);
        panel.setBackground(new Color(30, 40, 50)); // Tonos grises que combinan

        for (int i = 0; i < values.length; i++) {
            JPanel panel1 = new JPanel(new GridLayout(1,1));
            panel1.setPreferredSize(new Dimension(280, 50));
            panel1.setBackground(new Color(50, 60, 70));

            JLabel value = new JLabel(" " + values[i]);
            value.setForeground(Color.WHITE);
            value.setFont(new Font("Arial", Font.PLAIN, 14));
            panel1.add(value);

            panel1.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {}

                @Override
                public void mousePressed(MouseEvent e) {}

                @Override
                public void mouseReleased(MouseEvent e) {}

                @Override
                public void mouseEntered(MouseEvent e) {
                    panel1.setBackground(new Color(70, 80, 90));
                    value.setForeground(new Color(255,200,100));
                    panel1.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    panel1.setBackground(new Color(50, 60, 70));
                    value.setForeground(Color.WHITE);
                }
            });

            panel.add(panel1);
        }

        return panel;
    }


    public static void main(String[] args) {
        try{
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        new MainApp().setVisible(true);
    }
}
