package gui;

import objetos.ClienteBanco;
import objetos.CuentaCorriente;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import database.DatabaseManager;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EditarClienteFrame extends JFrame {

    private ClienteBanco cliente;
    private CuentaCorriente cuenta;
    private TrabajadorFrame parent;

    private JTextField fieldNombre;
    private JTextField fieldApellido;
    private JPasswordField fieldPassword;
    private DatabaseManager dbManager;

    // --- Paleta de Colores del Tema Oscuro ---
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45); // Main BG
    private static final Color FIELD_BACKGROUND = new Color(60, 63, 65); // Lighter Panel/Field BG
    private static final Color FOREGROUND_TEXT = new Color(200, 200, 200); // Light gray text
    private static final Color BUTTON_BASE_COLOR = new Color(105, 105, 255); // Primary Button BG (0x6969FF)
    private static final Color BUTTON_HOVER_COLOR = new Color(123, 123, 255); // Lighter Blue (0x7B7BFF)
    private static final Color BUTTON_PRESSED_COLOR = new Color(80, 80, 216); // Darker Blue (0x5050D8)

    public EditarClienteFrame(TrabajadorFrame parent, ClienteBanco cliente, CuentaCorriente cuenta) {
        this.parent = parent;
        this.cliente = cliente;
        this.cuenta = cuenta;
        this.dbManager = new DatabaseManager();

        setTitle("Editar datos del cliente");
        setSize(400, 330);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(DARK_BACKGROUND);

        initComponents();
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(FOREGROUND_TEXT);
        return label;
    }

    private JTextField createStyledField(String text, boolean editable) {
        JTextField field = new JTextField(text);
        field.setEditable(editable);
        field.setBackground(FIELD_BACKGROUND);
        field.setForeground(FOREGROUND_TEXT);
        field.setCaretColor(FOREGROUND_TEXT);
        field.setBorder(BorderFactory.createLineBorder(FIELD_BACKGROUND.darker()));
        return field;
    }

    private JPasswordField createStyledPasswordField(String password, boolean editable) {
        JPasswordField field = new JPasswordField(password);
        field.setEditable(editable);
        field.setBackground(FIELD_BACKGROUND);
        field.setForeground(FOREGROUND_TEXT);
        field.setCaretColor(FOREGROUND_TEXT);
        field.setBorder(BorderFactory.createLineBorder(FIELD_BACKGROUND.darker()));
        return field;
    }


    private void initComponents() {
        setLayout(new BorderLayout(10,10));

        JPanel panelCliente = new JPanel(new GridLayout(4, 2, 8, 8));
        panelCliente.setBackground(DARK_BACKGROUND);
        panelCliente.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(FOREGROUND_TEXT),
                "Datos Cliente",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                FOREGROUND_TEXT
        ));

        panelCliente.add(createStyledLabel("DNI:"));
        JTextField fieldDni = createStyledField(cliente.getDni(), false);
        panelCliente.add(fieldDni);

        panelCliente.add(createStyledLabel("Nombre:"));
        fieldNombre = createStyledField(cliente.getNombre(), true);
        panelCliente.add(fieldNombre);

        panelCliente.add(createStyledLabel("Apellido:"));
        fieldApellido = createStyledField(cliente.getApellido(), true);
        panelCliente.add(fieldApellido);

        panelCliente.add(createStyledLabel("Password:"));
        fieldPassword = createStyledPasswordField(cliente.getPassword(), true);
        panelCliente.add(fieldPassword);

        JPanel panelCuenta = new JPanel(new GridLayout(3, 2, 8,8));
        panelCuenta.setBackground(DARK_BACKGROUND);
        panelCuenta.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(FOREGROUND_TEXT),
                "Datos Cuenta",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                FOREGROUND_TEXT
        ));

        panelCuenta.add(createStyledLabel("Número de cuenta:"));
        JTextField fieldNumCuenta = createStyledField(cuenta.getNumCuenta(), false);
        panelCuenta.add(fieldNumCuenta);

        panelCuenta.add(createStyledLabel("Saldo:"));
        JTextField fieldSaldo = createStyledField(String.format("%.2f", cuenta.getSaldo()), false);
        panelCuenta.add(fieldSaldo);

        panelCuenta.add(createStyledLabel("Sucursal:"));
        JTextField fieldSucursal = createStyledField(String.valueOf(cuenta.getNumSucursal()), false);
        panelCuenta.add(fieldSucursal);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(DARK_BACKGROUND);
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        applyHoverEffect(btnGuardar, BUTTON_BASE_COLOR, BUTTON_HOVER_COLOR, BUTTON_PRESSED_COLOR);
        btnGuardar.setForeground(DARK_BACKGROUND);
        applyHoverEffect(btnCancelar, FIELD_BACKGROUND, FIELD_BACKGROUND.darker(), DARK_BACKGROUND);
        btnCancelar.setForeground(FOREGROUND_TEXT);

        btnGuardar.addActionListener(e -> {
            cliente.setNombre(fieldNombre.getText());
            cliente.setApellido(fieldApellido.getText());
            cliente.setPassword(new String(fieldPassword.getPassword()));

            boolean exito = dbManager.modificarCliente(cliente);

            if (exito) {
                JOptionPane.showMessageDialog(this, "Datos actualizados con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                if (parent != null) {
                    parent.actualizarListaClientes();
                    parent.setVisible(true);
                    parent.toFront();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar los datos del cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> {
            dispose();
            if (parent != null) {
                parent.setVisible(true);
                parent.toFront();
            }
        });

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        JPanel principal = new JPanel(new BorderLayout(10,8));
        principal.setBackground(DARK_BACKGROUND);
        principal.add(panelCliente, BorderLayout.NORTH);
        principal.add(panelCuenta, BorderLayout.CENTER);
        principal.add(panelBotones, BorderLayout.SOUTH);

        add(principal, BorderLayout.CENTER);
    }

    // Método auxiliar para el efecto hover de los botones
    private void applyHoverEffect(JButton button, Color normalColor, Color hoverColor, Color pressedColor) {
        button.setBackground(normalColor);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) { button.setBackground(hoverColor); }
            @Override
            public void mouseExited(MouseEvent evt) { button.setBackground(normalColor); }
            @Override
            public void mousePressed(MouseEvent evt) { button.setBackground(pressedColor); }
            @Override
            public void mouseReleased(MouseEvent evt) {
                if (button.contains(evt.getPoint())) { button.setBackground(hoverColor); }
                else { button.setBackground(normalColor); }
            }
        });
    }
}