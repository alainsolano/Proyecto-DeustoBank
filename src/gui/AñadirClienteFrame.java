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

    // --- Paleta de Colores del Tema Oscuro ---
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);
    private static final Color FOREGROUND_TEXT = new Color(200, 200, 200);
    private static final Color FIELD_BACKGROUND = new Color(60, 63, 65);

    // --- Paleta de Colores para Botones (Fondo Azul Brillante) ---
    private static final Color BUTTON_BASE_COLOR = new Color(0x6969FF); // Azul Brillante (Fondo base)
    private static final Color BUTTON_HOVER_COLOR = new Color(0x7B7BFF); // Azul más claro para HOVER
    private static final Color BUTTON_PRESSED_COLOR = new Color(0x5050D8); // Azul más oscuro para PRESSED

    private final DatabaseManager dbManager = new DatabaseManager();

    public AñadirClienteFrame(TrabajadorFrame parent) {
        // ... (Tu lógica de inicialización) ...
        String username = parent.getUser().getUsername();
        String[] infoSucursal = dbManager.getInfoSucursalTrabajador(username);
        String numSucursal = infoSucursal[2];

        setTitle("🌑 Añadir Nuevo Cliente - Sucursal: " + numSucursal);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null);

        // --- 3. Panel Principal (contentPane) ---
        contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPane.setBackground(DARK_BACKGROUND);
        setContentPane(contentPane);

        // --- 4. Panel Central: Formulario (GridBagLayout) ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(DARK_BACKGROUND);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(FOREGROUND_TEXT),
                "Datos del Nuevo Cliente",
                TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                FOREGROUND_TEXT
        );
        formPanel.setBorder(titledBorder);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Inicialización de campos y aplicación de estilo
        txtDNI = createStyledTextField(15);
        txtNombre = createStyledTextField(15);
        txtApellido = createStyledTextField(15);
        txtContraseña = createStyledPasswordField(15);

        // ... (Añadir etiquetas y campos al formPanel - no modificado) ...
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createStyledLabel("DNI:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtDNI, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createStyledLabel("NOMBRE:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createStyledLabel("APELLIDO:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtApellido, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createStyledLabel("CONTRASEÑA:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtContraseña, gbc);

        contentPane.add(formPanel, BorderLayout.CENTER);

        // --- 5. Panel Inferior: Botones y Sucursal ---
        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        southPanel.setBackground(DARK_BACKGROUND);

        JLabel lblSucursal = createStyledLabel("  NUMERO SUCURSAL: " + numSucursal);
        lblSucursal.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSucursal.setBorder(new EmptyBorder(0, 10, 0, 0));
        southPanel.add(lblSucursal, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        buttonPanel.setBackground(DARK_BACKGROUND);

        JButton btnAñadir = new JButton("➕ AÑADIR CLIENTE");
        JButton btnVolver = new JButton("↩️ VOLVER");

        // 1. Aplicar el color de texto gris claro a ambos botones
        btnAñadir.setForeground(FOREGROUND_TEXT);
        btnVolver.setForeground(FOREGROUND_TEXT);

        // 3. Aplicar el efecto hover
        applyHoverEffect(btnAñadir, BUTTON_BASE_COLOR, BUTTON_HOVER_COLOR, BUTTON_PRESSED_COLOR);
        applyHoverEffect(btnVolver, BUTTON_BASE_COLOR, BUTTON_HOVER_COLOR, BUTTON_PRESSED_COLOR);

        buttonPanel.add(btnAñadir);
        buttonPanel.add(btnVolver);

        southPanel.add(buttonPanel, BorderLayout.EAST);

        contentPane.add(southPanel, BorderLayout.SOUTH);

        // ... (Listeners - no modificado) ...
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

    // ... (Métodos applyHoverEffect, createStyledLabel, etc. - no modificado) ...
    private void applyHoverEffect(JButton button, Color normalColor, Color hoverColor, Color pressedColor) {
        // Establecer el color de fondo inicial
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
        field.setBackground(FIELD_BACKGROUND);
        field.setForeground(FOREGROUND_TEXT);
        field.setCaretColor(FOREGROUND_TEXT);
        field.setBorder(BorderFactory.createLineBorder(FIELD_BACKGROUND.darker()));
        return field;
    }

    private JPasswordField createStyledPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setBackground(FIELD_BACKGROUND);
        field.setForeground(FOREGROUND_TEXT);
        field.setCaretColor(FOREGROUND_TEXT);
        field.setBorder(BorderFactory.createLineBorder(FIELD_BACKGROUND.darker()));
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
    // =================================================================
    // === MÉTODO MAIN CORREGIDO PARA FORZAR EL L&F ===
    // =================================================================
    public static void main(String[] args) {
        // --- Solución para problemas de estilo de botones ---
        try {
            // Intenta usar Nimbus, que es un L&F moderno y respeta mejor los colores
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            // Si Nimbus no funciona, puedes probar con Metal o la línea nativa:
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si el L&F falla, usa el predeterminado de Java (Metal)
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Aquí deberías pasar la instancia de TrabajadorFrame o simularla
                // Esto es solo para la prueba:
                // new AñadirClienteFrame(new TrabajadorFrame(...)).setVisible(true);
            }
        });
    }
}