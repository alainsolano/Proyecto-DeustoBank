package gui;

import database.DatabaseManager;
import domain.ClienteBanco;
import domain.CuentaCorriente;
import domain.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.RowFilter;

public class TrabajadorFrame extends JFrame {

    private User user;
    private JTable clientTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    private JLabel detailId;
    private JLabel detailNombre;
    private JLabel detailCuenta;
    private JLabel detailSaldo;

    private DatabaseManager dbManager;
    private String[] sucursalInfo;

    // Paleta de Colores
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);
    private static final Color FIELD_BACKGROUND = new Color(60, 63, 65);
    private static final Color FOREGROUND_TEXT = new Color(200, 200, 200);
    private static final Color BUTTON_BASE_COLOR = new Color(105, 105, 255);
    private static final Color BUTTON_HOVER_COLOR = new Color(123, 123, 255);
    private static final Color BUTTON_PRESSED_COLOR = new Color(80, 80, 216);

    public domain.User getUser() {
        return this.user;
    }

    public TrabajadorFrame(User user) {
        this.user = user;
        this.dbManager = new DatabaseManager();

        this.sucursalInfo = dbManager.getInfoSucursalTrabajador(user.getUsername());

        setupWindow();
        createComponents();
        loadClientData();
        setVisible(true);
    }

    private void setupWindow() {
        setTitle("Panel de Gestión de Empleados - Deusto Bank");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(DARK_BACKGROUND);
    }

    private void createComponents() {
        setLayout(new BorderLayout(10, 10));
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setBackground(DARK_BACKGROUND);

        contentPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6);

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBackground(DARK_BACKGROUND);
        topPanel.add(createSearchPanel(), BorderLayout.NORTH);
        topPanel.add(createClientTable(), BorderLayout.CENTER);
        splitPane.setTopComponent(topPanel);

        JPanel detailPanel = createDetailPanel();
        JScrollPane detailScroll = new JScrollPane(detailPanel);
        detailScroll.setBackground(DARK_BACKGROUND);
        detailScroll.getViewport().setBackground(DARK_BACKGROUND);
        detailScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(FOREGROUND_TEXT),
                "Detalles del Cliente Seleccionado",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.PLAIN, 12),
                FOREGROUND_TEXT
        ));
        splitPane.setBottomComponent(detailScroll);

        contentPanel.add(splitPane, BorderLayout.CENTER);
        contentPanel.add(createActionPanel(), BorderLayout.EAST);

        add(contentPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(DARK_BACKGROUND);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel("Gestión de Clientes");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(FOREGROUND_TEXT);
        headerPanel.add(title, BorderLayout.WEST);

        JPanel eastHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        eastHeaderPanel.setBackground(DARK_BACKGROUND);

        String welcomeMessage = "Bienvenido, " + user.getName();
        if (sucursalInfo != null) {
            welcomeMessage += String.format(" | Sucursal %s: %s (%s)", sucursalInfo[2], sucursalInfo[0], sucursalInfo[1]);
        }
        welcomeMessage += " | ";

        JLabel welcomeLabel = new JLabel(welcomeMessage);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setForeground(FOREGROUND_TEXT);

        JButton btnLogout = new JButton("Cerrar Sesión");
        applyHoverEffect(btnLogout, FIELD_BACKGROUND, FIELD_BACKGROUND.darker(), DARK_BACKGROUND);
        btnLogout.setForeground(FOREGROUND_TEXT);
        btnLogout.addActionListener(e -> logout());

        eastHeaderPanel.add(welcomeLabel);
        eastHeaderPanel.add(btnLogout);
        headerPanel.add(eastHeaderPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBackground(DARK_BACKGROUND);
        searchPanel.setBorder(new EmptyBorder(0, 0, 5, 0));
        JLabel filterLabel = new JLabel("Filtrar clientes: ");
        filterLabel.setForeground(FOREGROUND_TEXT);
        searchPanel.add(filterLabel, BorderLayout.WEST);

        searchField = new JTextField();
        searchField.setBackground(FIELD_BACKGROUND);
        searchField.setForeground(FOREGROUND_TEXT);
        searchField.setCaretColor(FOREGROUND_TEXT);
        searchPanel.add(searchField, BorderLayout.CENTER);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void insertUpdate(DocumentEvent e) { filterTable(); }
        });

        return searchPanel;
    }

    private void filterTable() {
        String text = searchField.getText();
        if (text.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private JScrollPane createClientTable() {
        String[] columnNames = {"DNI", "Nombre", "Apellido", "Nº Cuenta", "Saldo"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 4) ? Double.class : String.class;
            }
        };

        clientTable = new JTable(tableModel);

        clientTable.setBackground(FIELD_BACKGROUND);
        clientTable.setForeground(FOREGROUND_TEXT);
        clientTable.getTableHeader().setBackground(DARK_BACKGROUND.darker());
        clientTable.getTableHeader().setForeground(FOREGROUND_TEXT);
        clientTable.setGridColor(DARK_BACKGROUND.darker());
        clientTable.setSelectionBackground(BUTTON_BASE_COLOR.darker());
        clientTable.setSelectionForeground(Color.WHITE);

        sorter = new TableRowSorter<>(tableModel);
        clientTable.setRowSorter(sorter);

        clientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = clientTable.getSelectedRow();
                if (row != -1) {
                    updateDetailPanel(clientTable.convertRowIndexToModel(row));
                } else {
                    clearDetailPanel();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(clientTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getViewport().setBackground(FIELD_BACKGROUND);

        return scrollPane;
    }
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(FOREGROUND_TEXT);
        return label;
    }

    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(DARK_BACKGROUND);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Arial", Font.BOLD, 12);
        Font dataFont = new Font("Arial", Font.PLAIN, 12);

        detailId = new JLabel("N/A");
        detailNombre = new JLabel("N/A");
        detailCuenta = new JLabel("N/A");
        detailSaldo = new JLabel("N/A");

        detailId.setFont(dataFont);
        detailNombre.setFont(dataFont);
        detailCuenta.setFont(dataFont);
        detailSaldo.setFont(dataFont);

        detailId.setForeground(FOREGROUND_TEXT);
        detailNombre.setForeground(FOREGROUND_TEXT);
        detailCuenta.setForeground(FOREGROUND_TEXT);
        detailSaldo.setForeground(FOREGROUND_TEXT);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createStyledLabel("DNI:"), gbc);
        gbc.gridx = 1; panel.add(detailId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createStyledLabel("Nombre Completo:"), gbc);
        gbc.gridx = 1; panel.add(detailNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createStyledLabel("Nº Cuenta:"), gbc);
        gbc.gridx = 1; panel.add(detailCuenta, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(createStyledLabel("Saldo:"), gbc);
        gbc.gridx = 1; panel.add(detailSaldo, gbc);

        return panel;
    }

    private void updateDetailPanel(int modelRow) {
        detailId.setText(tableModel.getValueAt(modelRow, 0).toString());
        detailNombre.setText(tableModel.getValueAt(modelRow, 1) + " " + tableModel.getValueAt(modelRow, 2));
        detailCuenta.setText(tableModel.getValueAt(modelRow, 3).toString());
        detailSaldo.setText(String.format("$%,.2f", (Double) tableModel.getValueAt(modelRow, 4)));
    }

    private void clearDetailPanel() {
        detailId.setText("N/A");
        detailNombre.setText("N/A");
        detailCuenta.setText("N/A");
        detailSaldo.setText("N/A");
    }

    private void logout() {
        dispose();
        new LoginFrame();
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(DARK_BACKGROUND);
        panel.setBorder(new EmptyBorder(0, 10, 0, 0));

        JButton btnAccess = new JButton("Acceder como Cliente"); // Nuevo botón
        JButton btnAdd = new JButton("Añadir Cliente");
        JButton btnEdit = new JButton("Editar Cliente");
        JButton btnDelete = new JButton("Eliminar Cliente");

        Dimension btnSize = new Dimension(170, 40); // Ajustado para el nuevo botón
        btnAccess.setMaximumSize(btnSize);
        btnAdd.setMaximumSize(btnSize);
        btnEdit.setMaximumSize(btnSize);
        btnDelete.setMaximumSize(btnSize);

        applyHoverEffect(btnAccess, BUTTON_BASE_COLOR.brighter(), BUTTON_HOVER_COLOR.brighter(), BUTTON_PRESSED_COLOR.brighter());
        btnAccess.setForeground(DARK_BACKGROUND);
        applyHoverEffect(btnAdd, BUTTON_BASE_COLOR, BUTTON_HOVER_COLOR, BUTTON_PRESSED_COLOR);
        btnAdd.setForeground(DARK_BACKGROUND);
        applyHoverEffect(btnEdit, BUTTON_BASE_COLOR, BUTTON_HOVER_COLOR, BUTTON_PRESSED_COLOR);
        btnEdit.setForeground(DARK_BACKGROUND);
        applyHoverEffect(btnDelete, BUTTON_BASE_COLOR.darker().darker(), BUTTON_HOVER_COLOR.darker(), BUTTON_PRESSED_COLOR.darker().darker());
        btnDelete.setForeground(Color.WHITE);

        btnAccess.addActionListener(e -> {
            int viewRow = clientTable.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente para acceder a su panel.");
                return;
            }

            int modelRow = clientTable.convertRowIndexToModel(viewRow);
            String dni = tableModel.getValueAt(modelRow, 0).toString();
            String nombre = tableModel.getValueAt(modelRow, 1).toString();

            
            User clienteUser = dbManager.getUserPorDNI(dni); 
            if (clienteUser != null) {
                
                new ClienteFrame(clienteUser);
                
            } else {
                JOptionPane.showMessageDialog(this, "Error: No se pudo cargar la información de usuario del cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnAdd.addActionListener(e -> {
            AñadirClienteFrame add = new AñadirClienteFrame(TrabajadorFrame.this);
            add.setVisible(true);
            setVisible(false);
        });

        btnEdit.addActionListener(e -> {
            int viewRow = clientTable.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente para editar.");
                return;
            }

            int modelRow = clientTable.convertRowIndexToModel(viewRow);
            String dni = tableModel.getValueAt(modelRow, 0).toString();
            String numCuenta = tableModel.getValueAt(modelRow, 3).toString();

            ClienteBanco clienteSeleccionado = dbManager.getClientePorDNI(dni);
            CuentaCorriente cuentaSeleccionada = dbManager.getCuentaPorNumero(numCuenta);

            if (clienteSeleccionado == null || cuentaSeleccionada == null) {
                JOptionPane.showMessageDialog(this, "No se pudo encontrar el cliente o la cuenta seleccionada.");
                return;
            }

            EditarClienteFrame ventanaEditar = new EditarClienteFrame(TrabajadorFrame.this, clienteSeleccionado, cuentaSeleccionada);
            ventanaEditar.setVisible(true);
            TrabajadorFrame.this.setVisible(false);
        });

        btnDelete.addActionListener(e -> {
            int viewRow = clientTable.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un cliente para eliminar.");
                return;
            }

            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro de eliminar al cliente y su(s) cuenta(s)?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION
            );

            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }

            int modelRow = clientTable.convertRowIndexToModel(viewRow);
            String dni = tableModel.getValueAt(modelRow, 0).toString();
            String numCuenta = tableModel.getValueAt(modelRow, 3).toString();

            boolean okCuenta = dbManager.eliminarCuentaPorNumero(numCuenta);
            boolean okCliente = dbManager.eliminarClientePorDni(dni);

            if (okCuenta && okCliente) {
                tableModel.removeRow(modelRow);
                clearDetailPanel();
                JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente.");
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el cliente o su cuenta en la BBDD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(btnAccess); 
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnAdd);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnEdit);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btnDelete);

        return panel;
    }

    private void loadClientData() {
        tableModel.setRowCount(0);
        List<Object[]> clientes = dbManager.getClientesConCuenta(user.getUsername());

        for (Object[] row : clientes) {
            tableModel.addRow(row);
        }
    }
    public void actualizarTablaClientes() {
        tableModel.setRowCount(0);

        List<Object[]> clientes = dbManager.getClientesConCuenta(user.getUsername());

        for (Object[] fila : clientes) {
            tableModel.addRow(fila);
        }
    }

    public void actualizarListaClientes() {
        if (tableModel.getRowCount() > 0) {
            tableModel.setRowCount(0);
        }
        List<Object[]> clientesActualizados = dbManager.getClientesConCuenta(user.getUsername());
        for (Object[] clienteData : clientesActualizados) {
            tableModel.addRow(clienteData);
        }
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