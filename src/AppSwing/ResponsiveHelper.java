package AppSwing;

import java.awt.*;

public class ResponsiveHelper {

    // Calcular tamaño responsive basado en la pantalla
    public static Dimension getResponsiveDimension(double widthPercent, double heightPercent) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * widthPercent / 100);
        int height = (int) (screenSize.height * heightPercent / 100);
        return new Dimension(width, height);
    }

    // Fuente responsive
    public static Font getResponsiveFont(int baseSize) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int diagonal = (int) Math.sqrt(Math.pow(screenSize.width, 2) + Math.pow(screenSize.height, 2));
        int scaledSize = baseSize * diagonal / 1500; // Ajuste según tamaño de pantalla
        return new Font("Arial", Font.PLAIN, Math.max(scaledSize, baseSize));
    }

    // Crear margenes responsive
    public static Insets getResponsiveInsets(int baseInset) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int scaledInset = baseInset * screenSize.width / 1920; // Basado en 1080p
        return new Insets(scaledInset, scaledInset, scaledInset, scaledInset);
    }
}
