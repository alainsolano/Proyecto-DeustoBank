
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
    private JLabel lblImagenResultado;
    private JPanel contentPane;
    private ClienteFrame parent;
    private JLabel lblNombre, lblSaldo, lblMonto, lblRiesgo, lblContador, lblResultado, lblMontoValue;
    private JSlider sliderMonto;
    private JComboBox<String> comboRiesgo;
    private JButton btnInvertir, btnVolver;
    private double saldo;
    private ClienteBanco cliente;
    private DatabaseManager dbManager;
    private static final Color DARK_BACKGROUND = new Color(28, 28, 30);
    private static final Color FIELD_BACKGROUND = new Color(44, 44, 46);
    private static final Color FIELD_LIGHTER_BACKGROUND = new Color(58, 58, 60);
    private static final Color FOREGROUND_TEXT = new Color(242, 242, 247);
    private static final Color ACCENT_COLOR = new Color(0, 122, 255);
    private static final Color BUTTON_BASE_COLOR = ACCENT_COLOR;
    private static final Color BUTTON_HOVER_COLOR = new Color(50, 150, 255);
    private static final Color BUTTON_PRESSED_COLOR = new Color(0, 92, 204);
    private static final Color WIN_COLOR = new Color(48, 209, 88);
    private static final Color LOSS_COLOR = new Color(255, 69, 58);
    private static final Color COUNTDOWN_BG = FIELD_LIGHTER_BACKGROUND;

    public InvertirFrame(ClienteFrame parent, ClienteBanco cliente) {
        this.parent = parent;
        this.cliente = cliente;
        this.dbManager = new DatabaseManager();
        setTitle("Invertir en DeustoBank");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 530, 850);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPane.setLayout(new GridBagLayout());
        contentPane.setBackground(DARK_BACKGROUND);
        setLocationRelativeTo(null);

        String nombreCliente = cliente.getNombre();
        saldo = cliente.getSaldo();
        lblNombre = new JLabel("Cliente: " + nombreCliente, SwingConstants.CENTER);
        lblNombre.setForeground(FOREGROUND_TEXT);
        lblSaldo = new JLabel("Saldo disponible: " + String.format("€%.2f", saldo), SwingConstants.CENTER);
        lblSaldo.setForeground(WIN_COLOR);
        lblSaldo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblMonto = new JLabel("¿Cuánto quieres invertir?", SwingConstants.CENTER);
        lblMonto.setForeground(FOREGROUND_TEXT);
        lblMonto.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblMontoValue = new JLabel("(€0.00)", SwingConstants.CENTER);
        lblMontoValue.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblMontoValue.setForeground(WIN_COLOR);
        sliderMonto = new JSlider(0, (int)saldo, 0);
        sliderMonto.setBackground(DARK_BACKGROUND);
        sliderMonto.setForeground(FOREGROUND_TEXT);
        sliderMonto.setMajorTickSpacing(Math.max(1, (int)saldo / 5));
        sliderMonto.setPaintTicks(true);
        sliderMonto.setPaintLabels(true);
        sliderMonto.setPreferredSize(new Dimension(300, 80));
        sliderMonto.putClientProperty("JComponent.roundRect", Boolean.TRUE);
        sliderMonto.addChangeListener(e -> {
            lblMontoValue.setText("(" + String.format("€%.2f", sliderMonto.getValue() * 1.0) + ")");
        });
        lblRiesgo = new JLabel("Nivel de Riesgo", SwingConstants.CENTER);
        lblRiesgo.setForeground(FOREGROUND_TEXT);
        lblRiesgo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        comboRiesgo = new JComboBox<>(new String[]{"Bajo riesgo (5%)", "Riesgo medio (20%)", "Alto riesgo (50%)"});
        comboRiesgo.setBackground(FIELD_BACKGROUND);
        comboRiesgo.setForeground(FOREGROUND_TEXT);
        comboRiesgo.setBorder(new EmptyBorder(5, 5, 5, 5));
        comboRiesgo.putClientProperty("JComponent.roundRect", Boolean.TRUE);
        btnInvertir = new JButton("¡Invertir Ahora!");
        btnInvertir.setFont(new Font("Segoe UI", Font.BOLD, 18));
        applyHoverEffect(btnInvertir, BUTTON_BASE_COLOR, BUTTON_HOVER_COLOR, BUTTON_PRESSED_COLOR);
        btnInvertir.setForeground(FOREGROUND_TEXT);
        btnInvertir.setBorder(new EmptyBorder(15, 20, 15, 20));
        btnInvertir.putClientProperty("JComponent.roundRect", Boolean.TRUE);
        lblContador = new JLabel("", SwingConstants.CENTER);
        lblContador.setFont(new Font("Segoe UI", Font.BOLD, 40));
        lblContador.setOpaque(true);
        lblContador.setBackground(DARK_BACKGROUND);
        lblContador.setPreferredSize(new Dimension(70, 70));
        lblContador.setHorizontalAlignment(SwingConstants.CENTER);
        lblContador.setBorder(BorderFactory.createEmptyBorder());
        lblContador.putClientProperty("JComponent.roundRect", Boolean.TRUE);
        lblResultado = new JLabel("", SwingConstants.CENTER);
        lblResultado.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblResultado.setForeground(FOREGROUND_TEXT);
        btnVolver = new JButton("Cerrar");
        applyHoverEffect(btnVolver, FIELD_BACKGROUND, FIELD_BACKGROUND.darker(), DARK_BACKGROUND);
        btnVolver.setForeground(FOREGROUND_TEXT);
        btnVolver.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnVolver.putClientProperty("JComponent.roundRect", Boolean.TRUE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add(lblNombre, gbc);
        gbc.gridy++;
        contentPane.add(lblSaldo, gbc);
        gbc.gridy++;
        contentPane.add(Box.createVerticalStrut(20), gbc);
        gbc.gridy++;
        contentPane.add(lblMonto, gbc);
        gbc.gridy++;
        contentPane.add(lblMontoValue, gbc);
        gbc.gridy++; gbc.fill = GridBagConstraints.NONE;
        contentPane.add(sliderMonto, gbc);
        gbc.gridy++; gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add(lblRiesgo, gbc);
        gbc.gridy++;
        contentPane.add(comboRiesgo, gbc);
        gbc.gridy++; gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add(Box.createVerticalStrut(20), gbc);
        gbc.gridy++;
        contentPane.add(btnInvertir, gbc);
        gbc.gridy++; gbc.fill = GridBagConstraints.NONE;
        contentPane.add(Box.createVerticalStrut(20), gbc);
        gbc.gridy++;
        contentPane.add(lblContador, gbc);
        gbc.gridy++;
        contentPane.add(lblResultado, gbc);
        lblImagenResultado = new JLabel("", SwingConstants.CENTER);
        lblImagenResultado.setPreferredSize(new Dimension(300, 200));
        lblImagenResultado.setOpaque(false);
        gbc.gridy++;
        contentPane.add(lblImagenResultado, gbc);
        gbc.gridy++; gbc.weighty = 1.0; gbc.anchor = GridBagConstraints.SOUTH;
        contentPane.add(btnVolver, gbc);

        JScrollPane scroll = new JScrollPane(contentPane);
        scroll.setBorder(null);
        scroll.setBackground(DARK_BACKGROUND);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        setContentPane(scroll);

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
        lblResultado.setText("...");
        int riesgo = comboRiesgo.getSelectedIndex();
        new Thread(() -> {
            try {
                lblContador.setForeground(DARK_BACKGROUND);
                for (int i = 5; i >= 1; i--) {
                    Color color = COUNTDOWN_BG;
                    if (i == 5) color = WIN_COLOR.darker();
                    else if (i == 3) color = Color.YELLOW.darker();
                    else if (i == 1) color = LOSS_COLOR;
                    lblContador.setBackground(color);
                    lblContador.setText("" + i);
                    Thread.sleep(700);
                }
            } catch (InterruptedException ignored) {}
            lblContador.setText("");
            lblContador.setBackground(DARK_BACKGROUND);
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
                lblContador.setBackground(DARK_BACKGROUND);
                lblSaldo.setText("Saldo disponible: " + String.format("€%.2f", saldo));
                if (diferencia >= 0) {
                    lblResultado.setText("¡Has GANADO " + String.format("€%.2f", diferencia) + "!");
                    lblResultado.setForeground(WIN_COLOR);
                    lblImagenResultado.setIcon(cargarImagen("media/alcista.jpg"));
                } else {
                    lblResultado.setText("Has PERDIDO " + String.format("€%.2f", -diferencia) + "...");
                    lblResultado.setForeground(LOSS_COLOR);
                    lblImagenResultado.setIcon(cargarImagen("media/bajista.jpg"));
                }
                parent.actualizarSaldoEnPantalla(saldo);
            });
        }).start();
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

    private ImageIcon cargarImagen(String ruta) {
        ImageIcon icon = new ImageIcon(ruta);
        Image img = icon.getImage().getScaledInstance(250, 150, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}
