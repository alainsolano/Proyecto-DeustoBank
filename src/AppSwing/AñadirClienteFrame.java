package AppSwing;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

public class AñadirClienteFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textFieldDni;
	private JTextField textFieldNombre;
	private JTextField textFieldApellido;
	private JTextField textFieldNumeroCuenta;
	private JTextField textFieldSaldo;

	
	public AñadirClienteFrame(TrabajadorFrame parent) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblDni = new JLabel("DNI:");
		lblDni.setBounds(71, 41, 109, 26);
		contentPane.add(lblDni);
		
		JLabel lblNombre = new JLabel("NOMBRE:");
		lblNombre.setBounds(71, 101, 91, 13);
		contentPane.add(lblNombre);
		
		JLabel lblApellido = new JLabel("APELLIDO:");
		lblApellido.setBounds(71, 148, 91, 13);
		contentPane.add(lblApellido);
		
		JLabel lblNCuenta = new JLabel("NUMERO DE CUENTA:");
		lblNCuenta.setBounds(71, 194, 109, 13);
		contentPane.add(lblNCuenta);
		
		JLabel lblSaldo = new JLabel("SALDO:");
		lblSaldo.setBounds(71, 240, 91, 13);
		contentPane.add(lblSaldo);
		
		textFieldDni = new JTextField();
		textFieldDni.setBounds(214, 45, 96, 19);
		contentPane.add(textFieldDni);
		textFieldDni.setColumns(10);
		
		textFieldNombre = new JTextField();
		textFieldNombre.setBounds(214, 98, 96, 19);
		contentPane.add(textFieldNombre);
		textFieldNombre.setColumns(10);
		
		textFieldApellido = new JTextField();
		textFieldApellido.setBounds(214, 145, 96, 19);
		contentPane.add(textFieldApellido);
		textFieldApellido.setColumns(10);
		
		textFieldNumeroCuenta = new JTextField();
		textFieldNumeroCuenta.setBounds(214, 191, 96, 19);
		contentPane.add(textFieldNumeroCuenta);
		textFieldNumeroCuenta.setColumns(10);
		
		textFieldSaldo = new JTextField();
		textFieldSaldo.setBounds(214, 237, 96, 19);
		contentPane.add(textFieldSaldo);
		textFieldSaldo.setColumns(10);
		
		JButton btnAñadir = new JButton("AÑADIR");
		btnAñadir.setBounds(341, 97, 85, 21);
		contentPane.add(btnAñadir);
		
		JButton btnVolver = new JButton("VOLVER");
		btnVolver.setBounds(341, 144, 85, 21);
		contentPane.add(btnVolver);
	}
}
