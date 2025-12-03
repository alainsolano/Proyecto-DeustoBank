package gui;

import database.DatabaseManager;
import objetos.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AñadirClienteFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtDNI;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JPasswordField txtContraseña;

    private static final Color DARK_BACKGROUND = new Color(28, 28, 30); // iOS System Background
    private static final Color FIELD_BACKGROUND = new Color(44, 44, 46); // iOS Secondary System Background (Card BG)
    private static final Color FIELD_LIGHTER_BACKGROUND = new Color(58, 58, 60); // Text Field BG
    private static final Color FOREGROUND_TEXT = new Color(242, 242, 247); // iOS Label Color
    private static final Color ACCENT_COLOR = new Color(0, 122, 255); // iOS System Blue

    private static final Color BUTTON_BASE_COLOR = ACCENT_COLOR;
    private static final Color BUTTON_HOVER_COLOR = new Color(50, 150, 255);
    private static final Color BUTTON_PRESSED_COLOR = new Color(0, 92, 204);

    private final DatabaseManager dbManager = new DatabaseManager();

    public AñadirClienteFrame(TrabajadorFrame parent) {
        String username = parent.getUser().getUsername();
        String[] infoSucursal = dbManager.getInfoSucursalTrabajador(username);
        String numSucursal = infoSucursal[2];

        setTitle("👤 Añadir Nuevo Cliente - Sucursal: " + numSucursal);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 450); // Adjusted size
        setLocationRelativeTo(null);
        getContentPane().setBackground(DARK_BACKGROUND);

        contentPane = new JPanel(new BorderLayout(15, 15));
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPane.setBackground(DARK_BACKGROUND);
        setContentPane(contentPane);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(FIELD_BACKGROUND);
        formPanel.putClientProperty("JComponent.roundRect", Boolean.TRUE);


        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(FOREGROUND_TEXT),
                "Datos del Nuevo Cliente",
                TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                FOREGROUND_TEXT
        );
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                titledBorder,
                new EmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtDNI = createStyledTextField(15);
        txtNombre = createStyledTextField(15);
        txtApellido = createStyledTextField(15);
        txtContraseña = createStyledPasswordField(15);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formPanel.add(createStyledLabel("DNI:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(txtDNI, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formPanel.add(createStyledLabel("NOMBRE:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        formPanel.add(createStyledLabel("APELLIDO:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(txtApellido, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        formPanel.add(createStyledLabel("CONTRASEÑA:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(txtContraseña, gbc);

        contentPane.add(formPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        southPanel.setBackground(DARK_BACKGROUND);

        JLabel lblSucursal = createStyledLabel("Sucursal: " + numSucursal);
        lblSucursal.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSucursal.setForeground(FOREGROUND_TEXT);
        lblSucursal.setBorder(new EmptyBorder(0, 10, 0, 0));
        southPanel.add(lblSucursal, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        buttonPanel.setBackground(DARK_BACKGROUND);

        JButton btnAñadir = new JButton("➕ AÑADIR CLIENTE");
        JButton btnVolver = new JButton("↩️ VOLVER");

        btnAñadir.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 15));

        btnAñadir.setBorder(new EmptyBorder(10, 15, 10, 15));
        btnVolver.setBorder(new EmptyBorder(10, 15, 10, 15));

        btnAñadir.putClientProperty("JComponent.roundRect", Boolean.TRUE);
        btnVolver.putClientProperty("JComponent.roundRect", Boolean.TRUE);


        applyHoverEffect(btnAñadir, BUTTON_BASE_COLOR, BUTTON_HOVER_COLOR, BUTTON_PRESSED_COLOR);
        btnAñadir.setForeground(FOREGROUND_TEXT);

        applyHoverEffect(btnVolver, FIELD_LIGHTER_BACKGROUND, FIELD_LIGHTER_BACKGROUND.darker(), DARK_BACKGROUND);
        btnVolver.setForeground(FOREGROUND_TEXT);

        buttonPanel.add(btnAñadir);
        buttonPanel.add(btnVolver);

        southPanel.add(buttonPanel, BorderLayout.EAST);

        contentPane.add(southPanel, BorderLayout.SOUTH);

        btnAñadir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                crearCliente(parent);
                parent.actualizarTablaClientes();
                parent.setVisible(true);
                dispose();
            }
        });

        btnVolver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                txtDNI.setText("");
                txtNombre.setText("");
                txtApellido.setText("");
                txtContraseña.setText("");
                parent.setVisible(true);
                dispose();
            }
        });

        pack();
    }

    private void applyHoverEffect(JButton button, Color normalColor, Color hoverColor, Color pressedColor) {
        button.setBackground(normalColor);

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

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(FOREGROUND_TEXT);
        return label;
    }

    private JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBackground(FIELD_LIGHTER_BACKGROUND);
        field.setForeground(FOREGROUND_TEXT);
        field.setCaretColor(ACCENT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_LIGHTER_BACKGROUND.darker(), 1),
                new EmptyBorder(8, 8, 8, 8)
        ));
        field.putClientProperty("JComponent.roundRect", Boolean.TRUE);
        return field;
    }

    private JPasswordField createStyledPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBackground(FIELD_LIGHTER_BACKGROUND);
        field.setForeground(FOREGROUND_TEXT);
        field.setCaretColor(ACCENT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_LIGHTER_BACKGROUND.darker(), 1),
                new EmptyBorder(8, 8, 8, 8)
        ));
        field.putClientProperty("JComponent.roundRect", Boolean.TRUE);
        return field;
    }

    public String generarNumeroCuentaAleatorio() {
        String prefijo = "ES10";
        String banco = "2100";
        StringBuilder restante = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            restante.append((int)(Math.random() * 10));
        }
        return prefijo + banco + restante.toString();
    }

    public void crearCliente(TrabajadorFrame parent) {
        String username = parent.getUser().getUsername();
        String[] infoSucursal = dbManager.getInfoSucursalTrabajador(username);
        String numSucursal = infoSucursal[2];
        String dni = txtDNI.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String password = new String(txtContraseña.getPassword()).trim();

        if (dni.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Rellena todos los campos.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String numCuenta = generarNumeroCuentaAleatorio();
        String cuentaCreada = dbManager.crearClienteConCuenta(
                dni, nombre, apellido, password, numSucursal, numCuenta
        );

        if (cuentaCreada != null) {
            JOptionPane.showMessageDialog(this, "Cliente y cuenta creados correctamente.\nSe han añadido 100.00 € de bienvenida.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error al crear el cliente. Comprueba que el DNI no este duplicado o que no haya problemas de conexión a la base de datos.",
                    "Error de Creación", JOptionPane.ERROR_MESSAGE
            );
        }
    }
}