package gui;

import database.DatabaseManager;
import objetos.ClienteBanco;
import objetos.CuentaCorriente;
import objetos.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
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

    public objetos.User getUser() {
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
    }

    private void createComponents() {
        setLayout(new BorderLayout(10, 10));
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        contentPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6);

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(createSearchPanel(), BorderLayout.NORTH);
        topPanel.add(createClientTable(), BorderLayout.CENTER);
        splitPane.setTopComponent(topPanel);

        JPanel detailPanel = createDetailPanel();
        JScrollPane detailScroll = new JScrollPane(detailPanel);
        detailScroll.setBorder(BorderFactory.createTitledBorder("Detalles del Cliente Seleccionado"));
        splitPane.setBottomComponent(detailScroll);

        contentPanel.add(splitPane, BorderLayout.CENTER);
        contentPanel.add(createActionPanel(), BorderLayout.EAST);

        add(contentPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel("Gestión de Clientes");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(title, BorderLayout.WEST);

        JPanel eastHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        String welcomeMessage = "Bienvenido, " + user.getName();
        if (sucursalInfo != null) {
            welcomeMessage += String.format(" | Sucursal %s: %s (%s)", sucursalInfo[2], sucursalInfo[0], sucursalInfo[1]);
        }
        welcomeMessage += " | ";

        JLabel welcomeLabel = new JLabel(welcomeMessage);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton btnLogout = new JButton("Cerrar Sesión");
        btnLogout.addActionListener(e -> logout());

        eastHeaderPanel.add(welcomeLabel);
        eastHeaderPanel.add(btnLogout);
        headerPanel.add(eastHeaderPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(new EmptyBorder(0, 0, 5, 0));
        searchPanel.add(new JLabel("Filtrar clientes: "), BorderLayout.WEST);

        searchField = new JTextField();
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

        return scrollPane;
    }

    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
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

        // Fila 1
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("DNI:"), gbc);
        gbc.gridx = 1; panel.add(detailId, gbc);

        // Fila 2
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Nombre Completo:"), gbc);
        gbc.gridx = 1; panel.add(detailNombre, gbc);

        // Fila 3
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Nº Cuenta:"), gbc);
        gbc.gridx = 1; panel.add(detailCuenta, gbc);

        // Fila 4
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Saldo:"), gbc);
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
        panel.setBorder(new EmptyBorder(0, 10, 0, 0));

        JButton btnAdd = new JButton("Añadir Cliente");
        JButton btnEdit = new JButton("Editar Cliente");
        JButton btnDelete = new JButton("Eliminar Cliente");

        Dimension btnSize = new Dimension(150, 40);
        btnAdd.setMaximumSize(btnSize);
        btnEdit.setMaximumSize(btnSize);
        btnDelete.setMaximumSize(btnSize);

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
    
    public void actualizarListaClientes() {
        if (tableModel.getRowCount() > 0) {
            tableModel.setRowCount(0); 
        }
        List<Object[]> clientesActualizados = dbManager.getClientesConCuenta(user.getUsername());
        for (Object[] clienteData : clientesActualizados) {
            tableModel.addRow(clienteData);
        }
    }
}
