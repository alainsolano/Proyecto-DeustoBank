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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * Esta es la ventana para el Cliente.
 * Muestra el saldo total y los movimientos de todas sus cuentas,
 * y permite cambiar a una pestaña de transferencias.
 * Se conecta a la BBDD a través del DatabaseManager.
 */
public class ClienteFrame extends JFrame {
    private User user;
    private DatabaseManager dbManager;

    // Componentes principales
    private JPanel mainCardPanel; // El panel que cambia
    private CardLayout cardLayout; // El gestor del panel

    // Componentes para la vista de movimientos
    private DefaultTableModel movementsTableModel;
    private JLabel saldoLabel;

    // Nombres de las "vistas"
    private static final String MOVIMIENTOS = "Card con Movimientos";
    private static final String TRANSFERENCIAS = "Card con Transferencias";

    public ClienteFrame(User user) {
        this.user = user;
        this.dbManager = new DatabaseManager(); // Inicializar el manager

        setupWindow();
        createComponents();

        // Cargar los datos de la BBDD DESPUÉS de crear los componentes
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

        // 1. Cabecera (Norte)
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Panel Central (CardLayout)
        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);
        mainCardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Añadir las dos "vistas" (paneles) al panel de CardLayout
        mainCardPanel.add(createMovimientosPanel(), MOVIMIENTOS);
        mainCardPanel.add(createTransferenciasPanel(), TRANSFERENCIAS);

        add(mainCardPanel, BorderLayout.CENTER);

        // 3. Footer (Sur)
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    /**
     * Crea la cabecera simple con el mensaje de bienvenida.
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        JLabel welcomeLabel = new JLabel("Bienvenido a tu banca, " + user.getName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(welcomeLabel);
        return panel;
    }

    /**
     * Crea el panel que muestra el saldo y la tabla de movimientos.
     */
    private JPanel createMovimientosPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Panel de Saldo (Arriba)
        JPanel saldoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        saldoPanel.setBorder(BorderFactory.createTitledBorder("Saldo Total (todas las cuentas)"));

        // Inicializamos la etiqueta. se rellenará en loadMovimientosData()
        saldoLabel = new JLabel("Cargando...");
        saldoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        saldoPanel.add(saldoLabel);
        panel.add(saldoPanel, BorderLayout.NORTH);

        // Tabla de Movimientos (Centro)
        String[] columnNames = {"Fecha", "Cantidad ($)", "Nº Cuenta"};
        movementsTableModel = new DefaultTableModel(columnNames, 0) {
            // Hacer que las celdas no sean editables
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            // Definir tipos de columna para ordenamiento correcto
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) { // Columna de Cantidad
                    return Double.class;
                }
                return String.class;
            }
        };
        JTable movementsTable = new JTable(movementsTableModel);
        movementsTable.setRowSorter(new TableRowSorter<>(movementsTableModel)); // Permite ordenar

        panel.add(new JScrollPane(movementsTable), BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crea el panel de formulario para realizar transferencias.
     */
    private JPanel createTransferenciasPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Realizar Transferencia"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 1: Cuenta Destino
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Cuenta de destino (IBAN):"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0;
        panel.add(new JTextField(20), gbc);

        // Fila 2: Cantidad
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Cantidad ($):"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        panel.add(new JTextField(20), gbc);

        // Fila 3: Concepto
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Concepto:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.weightx = 1.0;
        panel.add(new JTextField(20), gbc);

        // Fila 4: Botón
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        JButton transferButton = new JButton("Realizar Transferencia");
        transferButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Funcionalidad 'Transferir' en desarrollo.")
        );
        panel.add(transferButton, gbc);

        // Relleno para empujar todo hacia arriba
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


        btnInvertir.addActionListener((ActionListener) new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainCardPanel, MOVIMIENTOS);
                String dni = user.getUsername();
                ClienteBanco c = DatabaseManager.cargarDesdeBD(dni);

                InvertirFrame ventanaInv = new InvertirFrame(ClienteFrame.this, c);
                ventanaInv.setVisible(true);
                ClienteFrame.this.setVisible(false);
                	
            }
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


        movementsTableModel.setRowCount(0); // Limpiar tabla

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