package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import database.DatabaseManager;
import domain.ClienteBanco;
import domain.CuentaCorriente;

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
    // Paleta de colores
    private static final Color DARK_BACKGROUND = new Color(28, 28, 30);
    private static final Color FIELD_BACKGROUND = new Color(44, 44, 46);
    private static final Color FIELD_LIGHTER_BACKGROUND = new Color(58, 58, 60);
    private static final Color FOREGROUND_TEXT = new Color(242, 242, 247);
    private static final Color ACCENT_COLOR = new Color(0, 122, 255);

    private static final Color BUTTON_BASE_COLOR = ACCENT_COLOR;
    private static final Color BUTTON_HOVER_COLOR = new Color(50, 150, 255);
    private static final Color BUTTON_PRESSED_COLOR = new Color(0, 92, 204);


    public EditarClienteFrame(TrabajadorFrame parent, ClienteBanco cliente, CuentaCorriente cuenta) {
        this.parent = parent;
        this.cliente = cliente;
        this.cuenta = cuenta;
        this.dbManager = new DatabaseManager();

        setTitle("Editar datos del cliente");
        setSize(400, 450);
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
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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

    private JPasswordField createStyledPasswordField(String password, boolean editable) {
        JPasswordField field = new JPasswordField(password);
        field.setEditable(editable);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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


    private void initComponents() {
        setLayout(new BorderLayout(10,10));

        JPanel principal = new JPanel(new BorderLayout(15,15));
        principal.setBackground(DARK_BACKGROUND);
        principal.setBorder(new EmptyBorder(15, 15, 15, 15));


        JPanel panelCliente = new JPanel(new GridLayout(4, 2, 8, 8));
        panelCliente.setBackground(FIELD_BACKGROUND);
        panelCliente.putClientProperty("JComponent.roundRect", Boolean.TRUE);
        panelCliente.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(FIELD_BACKGROUND, 1),
                "Datos Cliente",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
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
        panelCuenta.setBackground(FIELD_BACKGROUND);
        panelCuenta.putClientProperty("JComponent.roundRect", Boolean.TRUE);
        panelCuenta.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(FIELD_BACKGROUND, 1),
                "Datos Cuenta",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
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
        JButton btnGuardar = new JButton("💾 Guardar");
        JButton btnCancelar = new JButton("↩️ Cancelar");

        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 15));

        btnGuardar.setBorder(new EmptyBorder(10, 15, 10, 15));
        btnCancelar.setBorder(new EmptyBorder(10, 15, 10, 15));

        btnGuardar.putClientProperty("JComponent.roundRect", Boolean.TRUE);
        btnCancelar.putClientProperty("JComponent.roundRect", Boolean.TRUE);


        applyHoverEffect(btnGuardar, BUTTON_BASE_COLOR, BUTTON_HOVER_COLOR, BUTTON_PRESSED_COLOR);
        btnGuardar.setForeground(FOREGROUND_TEXT);
        applyHoverEffect(btnCancelar, FIELD_LIGHTER_BACKGROUND, FIELD_LIGHTER_BACKGROUND.darker(), DARK_BACKGROUND);
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


        principal.add(panelCliente, BorderLayout.NORTH);
        principal.add(panelCuenta, BorderLayout.CENTER);
        principal.add(panelBotones, BorderLayout.SOUTH);

        add(principal, BorderLayout.CENTER);
    }

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