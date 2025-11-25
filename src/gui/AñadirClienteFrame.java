package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.awt.event.ActionEvent;

public class AñadirClienteFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtDNI;
	private JTextField txtNombre;
	private JTextField txtApellido;
	private JTextField txtContraseña;
	private JTextField txtNumSucursal;

	
	public AñadirClienteFrame(TrabajadorFrame parent) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
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
		
		JLabel lblNewLabel_1 = new JLabel("NUMERO SUCURSAL:");
		lblNewLabel_1.setBounds(66, 148, 118, 19);
		contentPane.add(lblNewLabel_1);
		
		txtNumSucursal = new JTextField();
		txtNumSucursal.setBounds(214, 150, 96, 19);
		contentPane.add(txtNumSucursal);
		txtNumSucursal.setColumns(10);
		btnAñadir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				crearCliente();
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
			    txtNumSucursal.setText("");
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

    public void crearCliente() {
	    String dni = txtDNI.getText().trim();
	    String nombre = txtNombre.getText().trim();
	    String apellido = txtApellido.getText().trim();
	    String password = txtContraseña.getText().trim();
	    String numSucursal = txtNumSucursal.getText().trim();
	    double saldo;

	    if (dni.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || password.isEmpty() || numSucursal.isEmpty()) {
	        JOptionPane.showMessageDialog(null, "Rellena todos los campos.");
	        return;
	    }
	    saldo = 0;
	    Connection conn = null;
	    PreparedStatement psCliente = null;
	    PreparedStatement psCuenta = null;

	    try {
	        conn = DriverManager.getConnection("jdbc:sqlite:sqlite/banco.db/");
	        conn.setAutoCommit(false); // IMPORTANTE

	        String sqlCliente = "INSERT INTO cliente(dni, nombre, apellido, password) VALUES (?, ?, ?, ?)";
	        psCliente = conn.prepareStatement(sqlCliente);
	        psCliente.setString(1, dni);
	        psCliente.setString(2, nombre);
	        psCliente.setString(3, apellido);
	        psCliente.setString(4, password);
	        psCliente.executeUpdate();

	        String numCuenta = generarNumeroCuentaAleatorio();

	        String sqlCuenta = "INSERT INTO cuenta(numcuenta, saldo, dni, numsucursal) VALUES (?, ?, ?, ?)";
	        psCuenta = conn.prepareStatement(sqlCuenta);
	        psCuenta.setString(1, numCuenta);
	        psCuenta.setDouble(2, saldo);
	        psCuenta.setString(3, dni);
	        psCuenta.setString(4, numSucursal);
	        psCuenta.executeUpdate();

	        conn.commit();
	        JOptionPane.showMessageDialog(null, "Cliente y cuenta creados correctamente.\nNº Cuenta: " + numCuenta);


	    } catch (Exception e) {
	        try {
	            if (conn != null) conn.rollback();
	        } catch (SQLException ignored) {}

	        JOptionPane.showMessageDialog(null, "Error al crear el cliente: " + e.getMessage());

	    } finally {
	        try {
	            if (psCliente != null) psCliente.close();
	            if (psCuenta != null) psCuenta.close();
	            if (conn != null) conn.close();
	        } catch (SQLException ignored) {}
	    }
	}
}
