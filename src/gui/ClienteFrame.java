package gui;

import objetos.ClienteBanco;
import database.DatabaseManager;
import objetos.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void createComponents() {
        setLayout(new BorderLayout(10, 10));
        add(createHeaderPanel(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);
        mainCardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainCardPanel.add(createMovimientosPanel(), MOVIMIENTOS);
        mainCardPanel.add(createTransferenciasPanel(), TRANSFERENCIAS);

        add(mainCardPanel, BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        JLabel welcomeLabel = new JLabel("Bienvenido a tu banca, " + user.getName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(welcomeLabel);
        return panel;
    }

    // ======= MODIFICADO SOLO ESTE METODO =======
    private JPanel createMovimientosPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel saldoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        saldoPanel.setBorder(BorderFactory.createTitledBorder("Saldo Total (todas las cuentas)"));
        saldoLabel = new JLabel("Cargando...");
        saldoLabel.setFont(new Font("Arial", Font.BOLD, 24));
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
        movementsTable.setRowSorter(new TableRowSorter<>(movementsTableModel));
        JScrollPane scrollPane = new JScrollPane(movementsTable);

        // Scroll vertical SIEMPRE visible
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    // ===========================================

    private JPanel createTransferenciasPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Realizar Transferencia"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Cuenta de destino (IBAN):"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0;
        panel.add(new JTextField(20), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Cantidad ($):"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        panel.add(new JTextField(20), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Concepto:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.weightx = 1.0;
        panel.add(new JTextField(20), gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        JButton transferButton = new JButton("Realizar Transferencia");
        transferButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Funcionalidad 'Transferir' en desarrollo.")
        );
        panel.add(transferButton, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.weighty = 1.0;
        panel.add(new JLabel(""), gbc);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        JButton btnInvertir = new JButton("Invertir");
        JButton btnTransferir = new JButton("Transferir");
        JButton btnLogout = new JButton("Cerrar Sesión");

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
}
