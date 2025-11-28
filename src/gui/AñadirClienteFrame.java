package gui;

import database.DatabaseManager;
import objetos.User;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AñadirClienteFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtDNI;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtContraseña;

    private final DatabaseManager dbManager = new DatabaseManager();
    
    public AñadirClienteFrame(TrabajadorFrame parent) {

        String username = parent.getUser().getUsername();
        String[] infoSucursal = dbManager.getInfoSucursalTrabajador(username);
        String numSucursal = infoSucursal[2];

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        setLocationRelativeTo(null);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        JLabel lblDni = new JLabel("DNI:");
        lblDni.setBounds(80, 42, 109, 26);
        contentPane.add(lblDni);
        
        JLabel lblNombre = new JLabel("NOMBRE:");
        lblNombre.setBounds(80, 78, 91, 13);
        contentPane.add(lblNombre);
        
        JLabel lblApellido = new JLabel("APELLIDO:");
        lblApellido.setBounds(80, 101, 91, 13);
        contentPane.add(lblApellido);
        
        txtDNI = new JTextField();
        txtDNI.setBounds(214, 46, 96, 19);
        contentPane.add(txtDNI);
        txtDNI.setColumns(10);
        
        txtNombre = new JTextField();
        txtNombre.setBounds(214, 75, 96, 19);
        contentPane.add(txtNombre);
        txtNombre.setColumns(10);
        
        txtApellido = new JTextField();
        txtApellido.setBounds(214, 98, 96, 19);
        contentPane.add(txtApellido);
        txtApellido.setColumns(10);
        
        JButton btnAñadir = new JButton("AÑADIR");
        btnAñadir.setBounds(341, 97, 85, 21);
        contentPane.add(btnAñadir);
        
        JButton btnVolver = new JButton("VOLVER");
        btnVolver.setBounds(341, 144, 85, 21);
        contentPane.add(btnVolver);
        
        JLabel lblNewLabel = new JLabel("CONTRASEÑA:");
        lblNewLabel.setBounds(80, 130, 91, 13);
        contentPane.add(lblNewLabel);
        
        txtContraseña = new JTextField();
        txtContraseña.setBounds(214, 127, 96, 19);
        contentPane.add(txtContraseña);
        txtContraseña.setColumns(10);
        
        JLabel lblNewLabel_1 = new JLabel("NUMERO SUCURSAL: " + numSucursal);
        lblNewLabel_1.setBounds(66, 148, 200, 19);
        contentPane.add(lblNewLabel_1);

        btnAñadir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                crearCliente(parent);
                parent.setVisible(true);
                parent.repaint();
                dispose();
            }
        });
        btnVolver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                txtDNI.setText("");
                txtNombre.setText("");
                txtApellido.setText("");
                txtContraseña.setText("");
                parent.setVisible(true);
                parent.repaint();
                dispose();
            }
        });
    }

    public String generarNumeroCuentaAleatorio() {
        String prefijo = "ES10";
        String banco = "2100";

        StringBuilder restante = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            restante.append((int)(Math.random() * 10));
        }

        return prefijo + banco + restante.toString();
    }

    public void crearCliente(TrabajadorFrame parent) {

        String username = parent.getUser().getUsername();
        String[] infoSucursal = dbManager.getInfoSucursalTrabajador(username);
        String numSucursal = infoSucursal[2];

        String dni = txtDNI.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String password = txtContraseña.getText().trim();

        if (dni.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Rellena todos los campos y verifica la sucursal del trabajador.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String numCuenta = generarNumeroCuentaAleatorio();

        String cuentaCreada = dbManager.crearClienteConCuenta(
                dni, nombre, apellido, password, numSucursal, numCuenta
        );

        if (cuentaCreada != null) {
            JOptionPane.showMessageDialog(null, "Cliente y cuenta creados correctamente.\nSe han añadido 100.00 € de bienvenida.");
        } else {
            JOptionPane.showMessageDialog(null,
                    "Error al crear el cliente. Comprueba que el DNI no este duplicado.",
                    "Error de Creacion", JOptionPane.ERROR_MESSAGE
            );
        }

    }
}
