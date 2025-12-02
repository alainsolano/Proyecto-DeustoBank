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
import java.awt.event.*;
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

    // --- Paleta de Colores del Tema Oscuro (Inspiración iOS Dark Mode) ---
    private static final Color DARK_BACKGROUND = new Color(28, 28, 30); // iOS System Background
    private static final Color FIELD_BACKGROUND = new Color(44, 44, 46); // iOS Secondary System Background (Card BG)
    private static final Color FIELD_LIGHTER_BACKGROUND = new Color(58, 58, 60); // Text Field BG
    private static final Color FOREGROUND_TEXT = new Color(242, 242, 247); // iOS Label Color
    private static final Color ACCENT_COLOR = new Color(0, 122, 255); // iOS System Blue

    private static final Color BUTTON_BASE_COLOR = ACCENT_COLOR;
    private static final Color BUTTON_HOVER_COLOR = new Color(50, 150, 255);
    private static final Color BUTTON_PRESSED_COLOR = new Color(0, 92, 204);
    private static final Color WIN_COLOR = new Color(48, 209, 88); // iOS System Green
    private static final Color LOSS_COLOR = new Color(255, 69, 58); // iOS System Red

    private static final int BORDER_RADIUS = 12;

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
        setLayout(new BorderLayout(0, 0)); // Remove vertical gap
        add(createHeaderPanel(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);
        mainCardPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Padding
        mainCardPanel.setBackground(DARK_BACKGROUND);
        mainCardPanel.add(createMovimientosPanel(), MOVIMIENTOS);
        mainCardPanel.add(createTransferenciasPanel(), TRANSFERENCIAS);

        add(mainCardPanel, BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        // Simular una Navigation Bar de iOS
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 15, 10, 15)); // Large padding for modern feel

        JLabel title = new JLabel("Cuentas");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(FOREGROUND_TEXT);

        JLabel welcomeLabel = new JLabel("Bienvenido, " + user.getName());
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        welcomeLabel.setForeground(FOREGROUND_TEXT);

        JButton btnLogout = new JButton("🚪"); // Logout icon
        btnLogout.setFont(new Font("Arial", Font.PLAIN, 20));
        btnLogout.setBackground(DARK_BACKGROUND);
        btnLogout.setForeground(ACCENT_COLOR);
        btnLogout.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Remove default hover effect
        for (MouseListener ml : btnLogout.getMouseListeners()) {
            if (ml.getClass().getSimpleName().contains("MouseAdapter")) {
                btnLogout.removeMouseListener(ml);
            }
        }

        btnLogout.addActionListener(e -> logout());

        panel.add(title, BorderLayout.WEST);
        panel.add(btnLogout, BorderLayout.EAST);

        return panel;
    }

    private JPanel createMovimientosPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(DARK_BACKGROUND);

        // 1. Saldo Card (Rounded Panel)
        JPanel saldoCard = new JPanel(new BorderLayout(10, 10));
        saldoCard.setBackground(FIELD_BACKGROUND);
        saldoCard.setBorder(new EmptyBorder(20, 15, 20, 15));
        saldoCard.putClientProperty("JComponent.roundRect", Boolean.TRUE);

        JLabel titleLabel = new JLabel("Saldo Total");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setForeground(FOREGROUND_TEXT.darker());

        saldoLabel = new JLabel("Cargando...");
        saldoLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        saldoLabel.setForeground(WIN_COLOR);

        saldoCard.add(titleLabel, BorderLayout.NORTH);
        saldoCard.add(saldoLabel, BorderLayout.CENTER);

        panel.add(saldoCard, BorderLayout.NORTH);

        // 2. Movimientos Table
        String[] columnNames = {"Fecha", "Cantidad (€)", "Nº Cuenta"};
        movementsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 1 ? Double.class : String.class;
            }
        };
        JTable movementsTable = new JTable(movementsTableModel);

        // iOS Table Style
        movementsTable.setBackground(FIELD_BACKGROUND);
        movementsTable.setForeground(FOREGROUND_TEXT);
        movementsTable.setShowGrid(false); // No grid lines
        movementsTable.setIntercellSpacing(new Dimension(0, 1));
        movementsTable.setRowHeight(30);

        // Header style (flat)
        movementsTable.getTableHeader().setBackground(FIELD_BACKGROUND);
        movementsTable.getTableHeader().setForeground(FOREGROUND_TEXT.darker());
        movementsTable.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 14));
        movementsTable.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        movementsTable.setSelectionBackground(FIELD_LIGHTER_BACKGROUND);
        movementsTable.setSelectionForeground(FOREGROUND_TEXT);

        movementsTable.setRowSorter(new TableRowSorter<>(movementsTableModel));
        JScrollPane scrollPane = new JScrollPane(movementsTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBackground(DARK_BACKGROUND);
        scrollPane.getViewport().setBackground(FIELD_BACKGROUND);

        // Remove scrollpane border and apply rounded corners to the whole table panel
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 0, 0),
                BorderFactory.createLineBorder(FIELD_BACKGROUND, 1) // Base for rounding
        ));
        scrollPane.putClientProperty("JComponent.roundRect", Boolean.TRUE);


        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Helper method for iOS-like text fields
    private JTextField createiOSField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBackground(FIELD_LIGHTER_BACKGROUND);
        field.setForeground(FOREGROUND_TEXT);
        field.setCaretColor(ACCENT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_LIGHTER_BACKGROUND.darker(), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));
        field.putClientProperty("JComponent.roundRect", Boolean.TRUE);
        return field;
    }

    // Helper method for styled labels
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(FOREGROUND_TEXT.darker());
        return label;
    }


    private JPanel createTransferenciasPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(DARK_BACKGROUND);

        JLabel title = new JLabel("Nueva Transferencia", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(FOREGROUND_TEXT);
        panel.add(title, BorderLayout.NORTH);

        // Center Panel with Form (Grouped List Style)
        JPanel formPanel = new JPanel(new GridLayout(3, 1, 0, 1));
        formPanel.setBackground(FIELD_BACKGROUND);
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        formPanel.putClientProperty("JComponent.roundRect", Boolean.TRUE);

        // Input fields simulate cells in a grouped list
        JTextField txtCuentaDestino = createiOSField(20);
        txtCuentaDestino.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // No border on component
        txtCuentaDestino.putClientProperty("JComponent.roundRect", Boolean.FALSE); // Ensure no rounding here

        JTextField txtCantidad = createiOSField(20);
        txtCantidad.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        txtCantidad.putClientProperty("JComponent.roundRect", Boolean.FALSE);

        JTextField txtConcepto = createiOSField(20);
        txtConcepto.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        txtConcepto.putClientProperty("JComponent.roundRect", Boolean.FALSE);

        // Field containers to show iOS-like labels
        formPanel.add(createTransferRow("Cuenta IBAN:", txtCuentaDestino));
        formPanel.add(createTransferRow("Cantidad (€):", txtCantidad));
        formPanel.add(createTransferRow("Concepto:", txtConcepto));

        panel.add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        buttonContainer.setBackground(DARK_BACKGROUND);

        JButton transferButton = new JButton("Realizar Transferencia");
        transferButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        transferButton.setBorder(new EmptyBorder(12, 20, 12, 20));
        transferButton.putClientProperty("JComponent.roundRect", Boolean.TRUE);

        applyHoverEffect(transferButton, BUTTON_BASE_COLOR, BUTTON_HOVER_COLOR, BUTTON_PRESSED_COLOR);
        transferButton.setForeground(FOREGROUND_TEXT);

        buttonContainer.add(transferButton);
        panel.add(buttonContainer, BorderLayout.SOUTH);

        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
                cardLayout.show(mainCardPanel, MOVIMIENTOS);
            }
        });

        return panel;
    }

    // Helper method to simulate an iOS table cell row
    private JPanel createTransferRow(String labelText, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setBackground(FIELD_LIGHTER_BACKGROUND); // Simulates the field background
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, DARK_BACKGROUND), // Thin separator
                new EmptyBorder(0, 15, 0, 15) // Horizontal padding
        ));

        JLabel label = createStyledLabel(labelText);
        label.setForeground(FOREGROUND_TEXT); // Force bright text color
        label.setPreferredSize(new Dimension(150, 40));

        row.add(label, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);

        return row;
    }

    private JPanel createFooterPanel() {
        // Simular una Tab Bar de iOS
        JPanel panel = new JPanel(new GridLayout(1, 3));
        panel.setBackground(FIELD_BACKGROUND);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, DARK_BACKGROUND)
        ); // Clean separator

        JButton btnMovimientos = createTabBarButton("📊 Movimientos", ACCENT_COLOR);
        JButton btnTransferir = createTabBarButton("💸 Transferir", FOREGROUND_TEXT);
        JButton btnInvertir = createTabBarButton("💰 Invertir", FOREGROUND_TEXT);

        btnMovimientos.addActionListener(e -> {
            cardLayout.show(mainCardPanel, MOVIMIENTOS);
            updateTabBarSelection(btnMovimientos, btnTransferir, btnInvertir);
        });
        btnTransferir.addActionListener(e -> {
            cardLayout.show(mainCardPanel, TRANSFERENCIAS);
            updateTabBarSelection(btnTransferir, btnMovimientos, btnInvertir);
        });
        btnInvertir.addActionListener(e -> {
            cardLayout.show(mainCardPanel, MOVIMIENTOS);
            String dni = user.getUsername();
            ClienteBanco c = dbManager.getClientePorDNI(dni);
            InvertirFrame ventanaInv = new InvertirFrame(ClienteFrame.this, c);
            ventanaInv.setVisible(true);
            ClienteFrame.this.setVisible(false);
            updateTabBarSelection(btnInvertir, btnMovimientos, btnTransferir);
        });

        panel.add(btnMovimientos);
        panel.add(btnTransferir);
        panel.add(btnInvertir);
        return panel;
    }

    private JButton createTabBarButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(FIELD_BACKGROUND);
        button.setForeground(color);
        button.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return button;
    }

    private void updateTabBarSelection(JButton selected, JButton unselected1, JButton unselected2) {
        selected.setForeground(ACCENT_COLOR);
        unselected1.setForeground(FOREGROUND_TEXT);
        unselected2.setForeground(FOREGROUND_TEXT);
    }


    private void loadMovimientosData() {
        String cuentaPrincipal = dbManager.getCuentaPrincipal(user.getUsername());
        double saldo;
        if (cuentaPrincipal != null) {
            saldo = dbManager.getSaldoCuenta(cuentaPrincipal);
        } else {
            saldo = dbManager.getSaldoTotalPorDni(user.getUsername());
        }
        saldoLabel.setText(String.format("€%,.2f", saldo));

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
        saldoLabel.setText(String.format("€%,.2f", saldoReal));

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