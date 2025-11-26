package gui;

import objetos.ClienteBanco;
import objetos.CuentaCorriente;

import javax.swing.*;

import database.DatabaseManager;

import java.awt.*;

public class EditarClienteFrame extends JFrame {

    private ClienteBanco cliente;
    private CuentaCorriente cuenta;
    private TrabajadorFrame parent;

    private JTextField fieldNombre;
    private JTextField fieldApellido;
    private JPasswordField fieldPassword;
    private DatabaseManager dbManager;

    public EditarClienteFrame(TrabajadorFrame parent, ClienteBanco cliente, CuentaCorriente cuenta) {
        this.parent = parent;
        this.cliente = cliente;
        this.cuenta = cuenta;
        this.dbManager = new DatabaseManager();

        setTitle("Editar datos del cliente");
        setSize(400, 330);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10,10));

        JPanel panelCliente = new JPanel(new GridLayout(4, 2, 8, 8));
        panelCliente.setBorder(BorderFactory.createTitledBorder("Datos Cliente"));

        panelCliente.add(new JLabel("DNI:"));
        JTextField fieldDni = new JTextField(cliente.getDni());
        fieldDni.setEditable(false);
        panelCliente.add(fieldDni);

        panelCliente.add(new JLabel("Nombre:"));
        fieldNombre = new JTextField(cliente.getNombre());
        panelCliente.add(fieldNombre);

        panelCliente.add(new JLabel("Apellido:"));
        fieldApellido = new JTextField(cliente.getApellido());
        panelCliente.add(fieldApellido);

        panelCliente.add(new JLabel("Password:"));
        fieldPassword = new JPasswordField(cliente.getPassword());
        panelCliente.add(fieldPassword);

        JPanel panelCuenta = new JPanel(new GridLayout(3, 2, 8,8));
        panelCuenta.setBorder(BorderFactory.createTitledBorder("Datos Cuenta"));

        panelCuenta.add(new JLabel("Número de cuenta:"));
        JTextField fieldNumCuenta = new JTextField(cuenta.getNumCuenta());
        fieldNumCuenta.setEditable(false);
        panelCuenta.add(fieldNumCuenta);

        panelCuenta.add(new JLabel("Saldo:"));
        JTextField fieldSaldo = new JTextField(String.format("%.2f", cuenta.getSaldo()));
        fieldSaldo.setEditable(false);
        panelCuenta.add(fieldSaldo);

        panelCuenta.add(new JLabel("Sucursal:"));
        JTextField fieldSucursal = new JTextField(String.valueOf(cuenta.getNumSucursal()));
        fieldSucursal.setEditable(false);
        panelCuenta.add(fieldSucursal);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

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
        principal.add(panelCliente, BorderLayout.NORTH);
        principal.add(panelCuenta, BorderLayout.CENTER);
        principal.add(panelBotones, BorderLayout.SOUTH);

        add(principal, BorderLayout.CENTER);
    }
}
