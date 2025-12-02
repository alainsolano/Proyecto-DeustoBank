package gui;

import objetos.ClienteBanco;
import database.DatabaseManager;
import objetos.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ClienteFrame extends JFrame {
    private User user;
    private DatabaseManager dbManager;

    private JPanel mainCardPanel;
    private CardLayout cardLayout;
    private DefaultTableModel movementsTableModel;
    private JLabel saldoLabel;

    private static final String MOVIMIENTOS = "Card con Movimientos";
    private static final String TRANSFERENCIAS = "Card con Transferencias";

    // --- Paleta de Colores del Tema Oscuro ---
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45); // Main BG
    private static final Color FIELD_BACKGROUND = new Color(60, 63, 65); // Lighter Panel BG
    private static final Color FOREGROUND_TEXT = new Color(200, 200, 200); // Light gray text
    private static final Color BUTTON_BASE_COLOR = new Color(105, 105, 255); // Primary Button BG (0x6969FF)
    private static final Color BUTTON_HOVER_COLOR = new Color(123, 123, 255); // Lighter Blue (0x7B7BFF)
    private static final Color BUTTON_PRESSED_COLOR = new Color(80, 80, 216); // Darker Blue (0x5050D8)
    private static final Color WIN_COLOR = new Color(46, 139, 87); // Sea Green (Para Ganancia/Saldo)
    private static final Color LOSS_COLOR = new Color(220, 20, 60); // Crimson (Para Pérdida)

    public ClienteFrame(User user) {
        this.user = user;
        this.dbManager = new DatabaseManager();

        setupWindow();
        createComponents();
        loadMovimientosData();
        setVisible(true);
    }

    private void setupWindow() {
        setTitle("Deusto Bank - Cliente: " + user.getName());
        setSize(500, 889); // 9:16 aspect ratio (500x889)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(DARK_BACKGROUND);
    }

    private void createComponents() {
        setLayout(new BorderLayout(10, 10));
        add(createHeaderPanel(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);
        mainCardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainCardPanel.setBackground(DARK_BACKGROUND);
        mainCardPanel.add(createMovimientosPanel(), MOVIMIENTOS);
        mainCardPanel.add(createTransferenciasPanel(), TRANSFERENCIAS);

        add(mainCardPanel, BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        panel.setBackground(DARK_BACKGROUND);
        JLabel welcomeLabel = new JLabel("Bienvenido a tu banca, " + user.getName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(FOREGROUND_TEXT);
        panel.add(welcomeLabel);
        return panel;
    }

    private JPanel createMovimientosPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(DARK_BACKGROUND);
        JPanel saldoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        saldoPanel.setBackground(FIELD_BACKGROUND);
        saldoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(FOREGROUND_TEXT),
                "Saldo Total (todas las cuentas)",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.PLAIN, 12),
                FOREGROUND_TEXT
        ));
        saldoLabel = new JLabel("Cargando...");
        saldoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        saldoLabel.setForeground(WIN_COLOR);
        saldoPanel.add(saldoLabel);
        panel.add(saldoPanel, BorderLayout.NORTH);

        String[] columnNames = {"Fecha", "Cantidad ($)", "Nº Cuenta"};
        movementsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 1 ? Double.class : String.class;
            }
        };
        JTable movementsTable = new JTable(movementsTableModel);

        // Estilo de la tabla
        movementsTable.setBackground(FIELD_BACKGROUND);
        movementsTable.setForeground(FOREGROUND_TEXT);
        movementsTable.getTableHeader().setBackground(DARK_BACKGROUND.darker());
        movementsTable.getTableHeader().setForeground(FOREGROUND_TEXT);
        movementsTable.setGridColor(DARK_BACKGROUND.darker());
        movementsTable.setSelectionBackground(BUTTON_BASE_COLOR.darker());
        movementsTable.setSelectionForeground(Color.WHITE);

        movementsTable.setRowSorter(new TableRowSorter<>(movementsTableModel));
        JScrollPane scrollPane = new JScrollPane(movementsTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBackground(DARK_BACKGROUND);
        scrollPane.getViewport().setBackground(FIELD_BACKGROUND);


        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Helper method for styled text fields
    private JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setBackground(FIELD_BACKGROUND);
        field.setForeground(FOREGROUND_TEXT);
        field.setCaretColor(FOREGROUND_TEXT);
        field.setBorder(BorderFactory.createLineBorder(FIELD_BACKGROUND.darker()));
        return field;
    }

    // Helper method for styled labels
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(FOREGROUND_TEXT);
        return label;
    }


    private JPanel createTransferenciasPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(DARK_BACKGROUND);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(FOREGROUND_TEXT),
                "Realizar Transferencia",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.PLAIN, 12),
                FOREGROUND_TEXT
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Cuenta de destino
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(createStyledLabel("Cuenta de destino (IBAN):"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0;
        panel.add(createStyledTextField(20), gbc);

        // Cantidad
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(createStyledLabel("Cantidad ($):"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        panel.add(createStyledTextField(20), gbc);

        // Concepto
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(createStyledLabel("Concepto:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.weightx = 1.0;
        panel.add(createStyledTextField(20), gbc);

        // Botones
        JButton btnVolver = new JButton("Volver");
        JButton transferButton = new JButton("Realizar Transferencia");

        applyHoverEffect(transferButton, BUTTON_BASE_COLOR, BUTTON_HOVER_COLOR, BUTTON_PRESSED_COLOR);
        transferButton.setForeground(DARK_BACKGROUND);
        applyHoverEffect(btnVolver, FIELD_BACKGROUND, FIELD_BACKGROUND.darker(), DARK_BACKGROUND);
        btnVolver.setForeground(FOREGROUND_TEXT);

        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Se asume el orden de los componentes añadidos (Label/Field/Label/Field/...)
                JTextField txtCuentaDestino = (JTextField) panel.getComponent(1);
                JTextField txtCantidad = (JTextField) panel.getComponent(3);
                JTextField txtConcepto = (JTextField) panel.getComponent(5);
                String cuentaDestino = txtCuentaDestino.getText().trim();
                String cantidadTxt = txtCantidad.getText().trim();
                String concepto = txtConcepto.getText().trim();
                if (cuentaDestino.isEmpty() || cantidadTxt.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Rellena todos los campos.");
                    return;
                }
                double cantidad;
                try {
                    cantidad = Double.parseDouble(cantidadTxt);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Cantidad no válida.");
                    return;
                }
                if (cantidad <= 0) {
                    JOptionPane.showMessageDialog(panel, "La cantidad debe ser mayor que 0.");
                    return;
                }
                String dniCliente = user.getUsername();
                String cuentaOrigen = dbManager.getCuentaPrincipal(dniCliente);
                if (cuentaOrigen == null) {
                    JOptionPane.showMessageDialog(panel, "No se ha encontrado tu cuenta principal.");
                    return;
                }
                boolean ok = dbManager.realizarTransferencia(
                        cuentaOrigen,
                        cuentaDestino,
                        cantidad,
                        concepto
                );
                if (!ok) {
                    JOptionPane.showMessageDialog(panel, "Error realizando la transferencia.");
                    return;
                }
                JOptionPane.showMessageDialog(panel, "Transferencia realizada con éxito.");
                actualizarSaldoEnPantalla(0);
                txtCuentaDestino.setText("");
                txtCantidad.setText("");
                txtConcepto.setText("");
                cardLayout.show(mainCardPanel, MOVIMIENTOS); // Vuelve a la vista de movimientos
            }
        });

        btnVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Se asume el orden de los componentes añadidos
                JTextField txtCuentaDestino = (JTextField) panel.getComponent(1);
                JTextField txtCantidad = (JTextField) panel.getComponent(3);
                JTextField txtConcepto = (JTextField) panel.getComponent(5);

                txtCuentaDestino.setText("");
                txtCantidad.setText("");
                txtConcepto.setText("");

                cardLayout.show(mainCardPanel, MOVIMIENTOS);
            }
        });

        // Contenedor de botones
        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonContainer.setBackground(DARK_BACKGROUND);
        buttonContainer.add(transferButton);

        // Añadir botones al panel principal
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(buttonContainer, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(btnVolver, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        panel.add(new JLabel(""), gbc);
        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(DARK_BACKGROUND);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        JButton btnInvertir = new JButton("Invertir");
        JButton btnTransferir = new JButton("Transferir");
        JButton btnLogout = new JButton("Cerrar Sesión");

        applyHoverEffect(btnInvertir, BUTTON_BASE_COLOR, BUTTON_HOVER_COLOR, BUTTON_PRESSED_COLOR);
        btnInvertir.setForeground(DARK_BACKGROUND);
        applyHoverEffect(btnTransferir, BUTTON_BASE_COLOR, BUTTON_HOVER_COLOR, BUTTON_PRESSED_COLOR);
        btnTransferir.setForeground(DARK_BACKGROUND);
        applyHoverEffect(btnLogout, FIELD_BACKGROUND, FIELD_BACKGROUND.darker(), DARK_BACKGROUND);
        btnLogout.setForeground(FOREGROUND_TEXT);


        btnInvertir.addActionListener(e -> {
            cardLayout.show(mainCardPanel, MOVIMIENTOS);
            String dni = user.getUsername();
            ClienteBanco c = dbManager.getClientePorDNI(dni);
            InvertirFrame ventanaInv = new InvertirFrame(ClienteFrame.this, c);
            ventanaInv.setVisible(true);
            ClienteFrame.this.setVisible(false);
        });
        btnTransferir.addActionListener(e -> cardLayout.show(mainCardPanel, TRANSFERENCIAS));
        btnLogout.addActionListener(e -> logout());

        panel.add(btnInvertir);
        panel.add(btnTransferir);
        panel.add(btnLogout);
        return panel;
    }

    private void loadMovimientosData() {
        String cuentaPrincipal = dbManager.getCuentaPrincipal(user.getUsername());
        double saldo;
        if (cuentaPrincipal != null) {
            saldo = dbManager.getSaldoCuenta(cuentaPrincipal);
        } else {
            saldo = dbManager.getSaldoTotalPorDni(user.getUsername());
        }
        saldoLabel.setText(String.format("$%,.2f", saldo));

        movementsTableModel.setRowCount(0);

        List<Object[]> movimientos = dbManager.getMovimientos(user.getUsername());
        for (Object[] row : movimientos) {
            movementsTableModel.addRow(row);
        }
    }

    private void logout() {
        dispose();
        new LoginFrame();
    }

    public void actualizarSaldoEnPantalla(double nuevoSaldo) {
        String cuentaPrincipal = dbManager.getCuentaPrincipal(user.getUsername());
        double saldoReal;
        if (cuentaPrincipal != null) {
            saldoReal = dbManager.getSaldoCuenta(cuentaPrincipal);
        } else {
            saldoReal = dbManager.getSaldoTotalPorDni(user.getUsername());
        }
        saldoLabel.setText(String.format("$%,.2f", saldoReal));

        movementsTableModel.setRowCount(0);
        List<Object[]> movimientos = dbManager.getMovimientos(user.getUsername());
        for (Object[] row : movimientos) {
            movementsTableModel.addRow(row);
        }
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