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

    private JLabel lblNombre, lblSaldo, lblMonto, lblRiesgo, lblContador, lblResultado, lblMontoValue;
    private JSlider sliderMonto;
    private JComboBox<String> comboRiesgo;
    private JButton btnInvertir, btnVolver;
    private double saldo;
    private ClienteBanco cliente;
    private DatabaseManager dbManager;

    // --- Paleta de Colores del Tema Oscuro ---
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45); // Main BG
    private static final Color FIELD_BACKGROUND = new Color(60, 63, 65); // Lighter Panel BG
    private static final Color FOREGROUND_TEXT = new Color(200, 200, 200); // Light gray text
    private static final Color BUTTON_BASE_COLOR = new Color(105, 105, 255); // Primary Button BG (0x6969FF)
    private static final Color BUTTON_HOVER_COLOR = new Color(123, 123, 255); // Lighter Blue (0x7B7BFF)
    private static final Color BUTTON_PRESSED_COLOR = new Color(80, 80, 216); // Darker Blue (0x5050D8)
    private static final Color WIN_COLOR = new Color(46, 139, 87); // Sea Green (Para Ganancia/Saldo)
    private static final Color LOSS_COLOR = new Color(220, 20, 60); // Crimson (Para Pérdida)
    private static final Color COUNTDOWN_BG = new Color(220, 220, 220); // Light Gray for counter BG

    public InvertirFrame(ClienteFrame parent, ClienteBanco cliente) {
        this.parent = parent;
        this.cliente = cliente;
        this.dbManager = new DatabaseManager();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 430, 375);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new GridBagLayout());
        contentPane.setBackground(DARK_BACKGROUND);
        setLocationRelativeTo(null);
        setContentPane(contentPane);

        String nombreCliente = cliente.getNombre();
        saldo = cliente.getSaldo();

        lblNombre = new JLabel("Cliente: " + nombreCliente);
        lblNombre.setForeground(FOREGROUND_TEXT);
        lblSaldo = new JLabel("Saldo disponible: " + String.format("%.2f €", saldo));
        lblSaldo.setForeground(FOREGROUND_TEXT);
        lblMonto = new JLabel("¿Cuánto quieres invertir?");
        lblMonto.setForeground(FOREGROUND_TEXT);
        lblMontoValue = new JLabel("(0.00 €)");
        lblMontoValue.setFont(new Font("Arial", Font.BOLD, 17));
        lblMontoValue.setForeground(WIN_COLOR);

        sliderMonto = new JSlider(0, (int)saldo, 0);
        sliderMonto.setBackground(DARK_BACKGROUND);
        sliderMonto.setForeground(FOREGROUND_TEXT); // Para las etiquetas del slider
        sliderMonto.setMajorTickSpacing(Math.max(1, (int)saldo / 5));
        sliderMonto.setPaintTicks(true);
        sliderMonto.setPaintLabels(true);
        sliderMonto.setPreferredSize(new Dimension(200, 50));

        sliderMonto.addChangeListener(e -> {
            lblMontoValue.setText("(" + String.format("%.2f €", sliderMonto.getValue() * 1.0) + ")");
        });

        lblRiesgo = new JLabel("¿Cómo quieres invertir?");
        lblRiesgo.setForeground(FOREGROUND_TEXT);
        comboRiesgo = new JComboBox<>(new String[]{"Bajo riesgo", "Riesgo medio", "Alto riesgo"});
        comboRiesgo.setBackground(FIELD_BACKGROUND);
        comboRiesgo.setForeground(FOREGROUND_TEXT);

        btnInvertir = new JButton("¡Invertir Ahora!");
        btnInvertir.setFont(new Font("Arial", Font.BOLD, 16));
        applyHoverEffect(btnInvertir, BUTTON_BASE_COLOR, BUTTON_HOVER_COLOR, BUTTON_PRESSED_COLOR);
        btnInvertir.setForeground(DARK_BACKGROUND);

        lblContador = new JLabel("", SwingConstants.CENTER);
        lblContador.setFont(new Font("Arial", Font.BOLD, 26));
        lblContador.setOpaque(true);
        lblContador.setBackground(COUNTDOWN_BG);
        lblContador.setForeground(DARK_BACKGROUND);
        lblContador.setPreferredSize(new Dimension(70, 70));
        lblContador.setHorizontalAlignment(SwingConstants.CENTER);
        lblContador.setBorder(BorderFactory.createLineBorder(new Color(70,130,180), 3));

        lblResultado = new JLabel("", SwingConstants.CENTER);
        lblResultado.setForeground(FOREGROUND_TEXT);

        btnVolver = new JButton("Volver atrás");
        applyHoverEffect(btnVolver, FIELD_BACKGROUND, FIELD_BACKGROUND.darker(), DARK_BACKGROUND);
        btnVolver.setForeground(FOREGROUND_TEXT);


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7,7,7,7);
        gbc.gridx = 0;  gbc.gridy = 0;  gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
        contentPane.add(lblNombre, gbc);

        gbc.gridy++;
        contentPane.add(lblSaldo, gbc);

        gbc.gridy++; gbc.gridwidth = 1;
        contentPane.add(lblMonto, gbc);
        gbc.gridx = 1;
        contentPane.add(lblMontoValue, gbc);

        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add(sliderMonto, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        contentPane.add(lblRiesgo, gbc);
        gbc.gridx = 1;
        contentPane.add(comboRiesgo, gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add(btnInvertir, gbc);

        gbc.gridy++;
        contentPane.add(lblContador, gbc);

        gbc.gridy++;
        contentPane.add(lblResultado, gbc);

        gbc.gridy++;
        contentPane.add(btnVolver, gbc);

        btnInvertir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                invertir();
                String cuenta = dbManager.getCuentaPrincipal(cliente.getDni());
                dbManager.actualizarSaldoCuenta(cuenta, saldo);
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
        double monto = sliderMonto.getValue();

        if (monto <= 0 || monto > saldo) {
            JOptionPane.showMessageDialog(this, "Monto fuera del saldo disponible.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int riesgo = comboRiesgo.getSelectedIndex();

        new Thread(() -> {
            try {
                for (int i = 5; i >= 1; i--) {
                    Color color = COUNTDOWN_BG;
                    if (i == 5) color = WIN_COLOR;
                    else if (i == 3) color = new Color(255,215,0);
                    else if (i == 1) color = LOSS_COLOR;
                    lblContador.setBackground(color);
                    lblContador.setText("" + i);
                    Thread.sleep(700);
                }
            } catch (InterruptedException ignored) {}

            Random rnd = new Random();
            double multiplicador;

            switch (riesgo) {
                case 0: multiplicador = 0.95 + 0.15 * rnd.nextDouble(); break;
                case 1: multiplicador = 0.80 + 0.50 * rnd.nextDouble(); break;
                default: multiplicador = 0.40 + 1.40 * rnd.nextDouble(); break;
            }

            double resultado = monto * multiplicador;
            double diferencia = resultado - monto;

            saldo = saldo - monto + resultado;

            String cuenta = dbManager.getCuentaPrincipal(cliente.getDni());

            if (cuenta == null) {
                JOptionPane.showMessageDialog(this, "No se encontró la cuenta del cliente.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean ok = dbManager.actualizarSaldoCuenta(cuenta, saldo);
            if (!ok) {
                JOptionPane.showMessageDialog(this, "No se pudo guardar el saldo en la BD.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            dbManager.insertarMovimiento(cuenta, diferencia);

            SwingUtilities.invokeLater(() -> {
                lblContador.setText("");
                lblContador.setBackground(COUNTDOWN_BG);
                lblSaldo.setText("Saldo disponible: " + String.format("%.2f €", saldo));

                if (diferencia >= 0) {
                    lblResultado.setText("¡Has GANADO " + String.format("%.2f €", diferencia) + "!");
                    lblResultado.setForeground(WIN_COLOR);
                } else {
                    lblResultado.setText("Has PERDIDO " + String.format("%.2f €", -diferencia) + "...");
                    lblResultado.setForeground(LOSS_COLOR);
                }
                parent.actualizarSaldoEnPantalla(saldo);
            });

        }).start();
    }

    // Método auxiliar para el efecto hover de los botones
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