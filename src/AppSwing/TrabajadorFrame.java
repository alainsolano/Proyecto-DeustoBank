package AppSwing;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
// Importar ListSelectionListener
import javax.swing.event.ListSelectionListener;
// Importar RowFilter
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
    private JPanel detailPanel; // Panel inferior para detalles

    private DatabaseManager dbManager;
    private String[] sucursalInfo;

    public TrabajadorFrame(User user) {
        this.user = user;
        this.dbManager = new DatabaseManager(); // Inicializar el manager

        // Obtenemos la info de la sucursal ANTES de crear componentes
        this.sucursalInfo = dbManager.getInfoSucursalTrabajador(user.getUsername());

        setupWindow();
        createComponents();
        loadClientData(); // Cargar datos al iniciar
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

        // 1. Cabecera (Norte)
        contentPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Panel Central (con JSplitPane)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6); // La tabla ocupa el 60%

        // 2a. Panel Superior (Búsqueda + Tabla)
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(createSearchPanel(), BorderLayout.NORTH);
        topPanel.add(createClientTable(), BorderLayout.CENTER);

        splitPane.setTopComponent(topPanel);

        // 2b. Panel Inferior (Detalles)
        this.detailPanel = createDetailPanel();
        JScrollPane detailScroll = new JScrollPane(detailPanel);
        detailScroll.setBorder(BorderFactory.createTitledBorder("Detalles del Cliente Seleccionado"));
        splitPane.setBottomComponent(detailScroll);

        contentPanel.add(splitPane, BorderLayout.CENTER);

        // 3. Panel de Acciones (Este)
        contentPanel.add(createActionPanel(), BorderLayout.EAST);

        add(contentPanel);
    }

    /**
     * Crea la cabecera con bienvenida y botón de logout.
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel("Gestión de Clientes");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(title, BorderLayout.WEST);

        // Panel para bienvenida y logout
        JPanel eastHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // --- ¡LÓGICA MODIFICADA! ---
        // Construir el mensaje de bienvenida dinámicamente
        String welcomeMessage = "Bienvenido, " + user.getName();
        if (this.sucursalInfo != null) {
            // sucursalInfo[0] es poblacion, sucursalInfo[1] es provincia
            welcomeMessage += String.format(" | Sucursal: %s (%s)", sucursalInfo[0], sucursalInfo[1]);
        }
        welcomeMessage += " | ";
        // --- FIN DE MODIFICACIÓN ---

        JLabel welcomeLabel = new JLabel(welcomeMessage); // Usar el mensaje dinámico
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton btnLogout = new JButton("Cerrar Sesión");
        btnLogout.addActionListener(e -> logout());

        eastHeaderPanel.add(welcomeLabel);
        eastHeaderPanel.add(btnLogout);
        headerPanel.add(eastHeaderPanel, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Crea el panel de búsqueda.
     */
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

    /**
     * Filtra la tabla basado en el texto del searchField.
     */
    private void filterTable() {
        String text = searchField.getText();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            // Filtra por cualquier columna que contenga el texto (ignorando mayúsculas)
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    /**
     * Crea el JScrollPane que contiene la tabla de clientes.
     */
    private JScrollPane createClientTable() {
        String[] columnNames = {"DNI", "Nombre", "Apellido", "Nº Cuenta", "Saldo"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) { // Columna de Saldo
                    return Double.class;
                }
                return String.class;
            }
        };

        clientTable = new JTable(tableModel);

        sorter = new TableRowSorter<>(tableModel);
        clientTable.setRowSorter(sorter);

        clientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int viewRow = clientTable.getSelectedRow();
                if (viewRow != -1) {
                    int modelRow = clientTable.convertRowIndexToModel(viewRow);
                    updateDetailPanel(modelRow);
                } else {
                    clearDetailPanel();
                }
            }
        });

        return new JScrollPane(clientTable);
    }

    /**
     * Crea el panel inferior para mostrar los detalles del cliente seleccionado.
     */
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

        // Fila 1: DNI
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblId = new JLabel("DNI:");
        lblId.setFont(labelFont);
        panel.add(lblId, gbc);

        gbc.gridx = 1;
        panel.add(detailId, gbc);

        // Fila 2: Nombre
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblNombre = new JLabel("Nombre Completo:");
        lblNombre.setFont(labelFont);
        panel.add(lblNombre, gbc);

        gbc.gridx = 1;
        panel.add(detailNombre, gbc);

        // Fila 3: Nº Cuenta
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblCuenta = new JLabel("Nº Cuenta:");
        lblCuenta.setFont(labelFont);
        panel.add(lblCuenta, gbc);

        gbc.gridx = 1;
        panel.add(detailCuenta, gbc);

        // Fila 4: Saldo
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblSaldo = new JLabel("Saldo:");
        lblSaldo.setFont(labelFont);
        panel.add(lblSaldo, gbc);

        gbc.gridx = 1;
        panel.add(detailSaldo, gbc);

        // Espacio de relleno
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        panel.add(new JLabel(""), gbc); // Relleno

        return panel;
    }

    /**
     * Rellena el panel de detalles con la información de la fila seleccionada.
     */
    private void updateDetailPanel(int modelRow) {
        String id = tableModel.getValueAt(modelRow, 0).toString();
        String nombre = tableModel.getValueAt(modelRow, 1).toString();
        String apellido = tableModel.getValueAt(modelRow, 2).toString();
        String cuenta = tableModel.getValueAt(modelRow, 3).toString();
        double saldo = (Double) tableModel.getValueAt(modelRow, 4);

        detailId.setText(id);
        detailNombre.setText(nombre + " " + apellido);
        detailCuenta.setText(cuenta);
        detailSaldo.setText(String.format("$%,.2f", saldo));
    }

    /**
     * Limpia el panel de detalles cuando no hay nada seleccionado.
     */
    private void clearDetailPanel() {
        detailId.setText("N/A");
        detailNombre.setText("N/A");
        detailCuenta.setText("N/A");
        detailSaldo.setText("N/A");
    }
    private void logout() {
    	dispose();
    	LoginFrame f = new LoginFrame();
    }
    /**
     * Crea el panel de acciones (Añadir, Editar, Borrar).
     */
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

        btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEdit.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDelete.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createRigidArea(new Dimension(0, 5))); // Espacio
        panel.add(btnAdd);
        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Espacio
        panel.add(btnEdit);
        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Espacio
        panel.add(btnDelete);
        panel.add(Box.createVerticalGlue()); // Empuja todo hacia arriba

        // Lógica (de momento con mensajes)
        btnAdd.addActionListener(e -> {
            AñadirClienteFrame add = new AñadirClienteFrame(TrabajadorFrame.this);
            add.setVisible(true);     // Mostrar la nueva ventana
            TrabajadorFrame.this.setVisible(false); // Ocultar la ventana actual
        });

        		

        btnEdit.addActionListener(e -> {
            if (clientTable.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente para editar.");
            } else {
                JOptionPane.showMessageDialog(this, "Funcionalidad 'Editar' en desarrollo.");
            }
        });

        btnDelete.addActionListener(e -> {
            if (clientTable.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente para eliminar.");
            } else {
                int resp = JOptionPane.showConfirmDialog(this,
                        "¿Está seguro de que desea eliminar al cliente seleccionado?",
                        "Confirmar Eliminación",
                        JOptionPane.YES_NO_OPTION);
                if (resp == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(this, "Funcionalidad 'Eliminar' en desarrollo.");
                }
            }
        });

        return panel;
    }

    /**
     * Carga datos de la BBDD en la tabla, filtrados por el trabajador.
     */
    private void loadClientData() {
        tableModel.setRowCount(0); // Limpiar tabla

        List<Object[]> clientes = dbManager.getClientesConCuenta(user.getUsername());

        for (Object[] row : clientes) {
            tableModel.addRow(row);
        }
        
        
    }

}

