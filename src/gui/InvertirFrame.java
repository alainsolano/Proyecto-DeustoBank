package gui;

import objetos.ClienteBanco;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.border.EmptyBorder;

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
        contentPane.setLayout(new GridLayout(9, 1, 5, 5)); // espacio para el botón extra
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
            }
        });

        btnVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Actualiza datos en parent si lo necesitas aquí
                parent.setVisible(true); // vuelve a mostrar la ventana cliente
                dispose();               // cierra la ventana de inversión
            }
        });
    }

    private void invertir() {
        double monto;
        try {
            monto = Double.parseDouble(txtMonto.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Introduce un monto válido.","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (monto <= 0 || monto > saldo) {
            JOptionPane.showMessageDialog(this, "Monto fuera del saldo disponible.","Error",JOptionPane.ERROR_MESSAGE);
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
            double multiplicador;
            Random rnd = new Random();
            switch(riesgo) {
                case 0: multiplicador = 0.95 + 0.15 * rnd.nextDouble(); break;
                case 1: multiplicador = 0.8 + 0.5 * rnd.nextDouble(); break;
                default: multiplicador = 0.4 + 1.4 * rnd.nextDouble(); break;
            }
            double resultado = monto * multiplicador;
            double diferencia = resultado - monto;
            saldo -= monto;
            saldo += resultado;

            // Actualizaría el saldo en el objeto cliente y la ventana parent   
           //cliente.setSaldo(saldo);
            
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
                //parent.actualizarSaldoEnPantalla(saldo); // LLama a este método si existe en ClienteFrame
            });
        }).start();
    }
}
