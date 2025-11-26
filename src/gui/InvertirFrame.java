package gui;

import objetos.ClienteBanco;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.border.EmptyBorder;

import database.DatabaseManager;

public class InvertirFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private ClienteFrame parent;

    private JLabel lblNombre, lblSaldo, lblMonto, lblRiesgo, lblContador, lblResultado;
    private JTextField txtMonto;
    private JComboBox<String> comboRiesgo;
    private JButton btnInvertir, btnVolver;
    private double saldo;
    private ClienteBanco cliente;

    public InvertirFrame(ClienteFrame parent, ClienteBanco cliente) {
        this.parent = parent;
        this.cliente = cliente;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 400, 360);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new GridLayout(9, 1, 5, 5)); 
        setLocationRelativeTo(null);
        setContentPane(contentPane);

        String nombreCliente = cliente.getNombre();
        saldo = cliente.getSaldo();

        lblNombre = new JLabel("Cliente: " + nombreCliente);
        lblSaldo = new JLabel("Saldo disponible: " + String.format("%.2f €", saldo));
        lblMonto = new JLabel("¿Cuánto quieres invertir?");
        txtMonto = new JTextField();
        lblRiesgo = new JLabel("¿Cómo quieres invertir?");
        comboRiesgo = new JComboBox<>(new String[]{"Bajo riesgo", "Riesgo medio", "Alto riesgo"});
        btnInvertir = new JButton("Invertir");
        lblContador = new JLabel("", SwingConstants.CENTER);
        lblResultado = new JLabel("", SwingConstants.CENTER);
        btnVolver = new JButton("Volver atrás");

        contentPane.add(lblNombre);
        contentPane.add(lblSaldo);
        contentPane.add(lblMonto);
        contentPane.add(txtMonto);
        contentPane.add(lblRiesgo);
        contentPane.add(comboRiesgo);
        contentPane.add(btnInvertir);
        contentPane.add(lblContador);
        contentPane.add(lblResultado);
        contentPane.add(btnVolver);

        btnInvertir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                invertir();
       
                DatabaseManager db = new DatabaseManager(); 
                String cuenta = new DatabaseManager().getCuentaPrincipal(cliente.getDni());
                db.actualizarSaldoCuenta(cuenta, saldo);         
                parent.actualizarSaldoEnPantalla(saldo);        

            }
        });

        btnVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               
                parent.setVisible(true); 
                dispose();               
            }
        });
    }

    private void invertir() {
        double monto;

        try {
            monto = Double.parseDouble(txtMonto.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Introduce un monto válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (monto <= 0 || monto > saldo) {
            JOptionPane.showMessageDialog(this, "Monto fuera del saldo disponible.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int riesgo = comboRiesgo.getSelectedIndex();

        new Thread(() -> {
            try {
                for (int i = 5; i >= 1; i--) {
                    lblContador.setText("Invertiendo en... " + i);
                    Thread.sleep(700);
                }
            } catch (InterruptedException ignored) {}

            // Calcular resultado de inversión
            Random rnd = new Random();
            double multiplicador;

            switch (riesgo) {
                case 0: multiplicador = 0.95 + 0.15 * rnd.nextDouble(); break;
                case 1: multiplicador = 0.80 + 0.50 * rnd.nextDouble(); break;
                default: multiplicador = 0.40 + 1.40 * rnd.nextDouble(); break;
            }

            double resultado = monto * multiplicador;
            double diferencia = resultado - monto;

            // Actualizar saldo
            saldo = saldo - monto + resultado;

            // Obtener cuenta principal
            DatabaseManager db = new DatabaseManager();
            String cuenta = db.getCuentaPrincipal(cliente.getDni());

            if (cuenta == null) {
                JOptionPane.showMessageDialog(this, "No se encontró la cuenta del cliente.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Guardar nuevo saldo
            boolean ok = db.actualizarSaldoCuenta(cuenta, saldo);
            if (!ok) {
                JOptionPane.showMessageDialog(this, "No se pudo guardar el saldo en la BD.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            // ⭐ REGISTRAR MOVIMIENTO ⭐
            String tipoMovimiento = (diferencia >= 0)
                    ? "Inversión - Ganancia"
                    : "Inversión - Pérdida";

            DatabaseManager.insertarMovimiento(cuenta, diferencia);

            // Actualizar pantalla
            SwingUtilities.invokeLater(() -> {
                lblContador.setText("");
                lblSaldo.setText("Saldo disponible: " + String.format("%.2f €", saldo));

                if (diferencia >= 0) {
                    lblResultado.setText("¡Has GANADO " + String.format("%.2f €", diferencia) + "!");
                    lblResultado.setForeground(Color.GREEN.darker());
                } else {
                    lblResultado.setText("Has PERDIDO " + String.format("%.2f €", -diferencia) + "...");
                    lblResultado.setForeground(Color.RED.darker());
                }

                // Actualizar saldo en el panel principal
                parent.actualizarSaldoEnPantalla(saldo);
            });

        }).start();
    }

}
