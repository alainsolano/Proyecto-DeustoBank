package AppSwing;

import javax.swing.*;
import java.awt.*;

public class Defaults {
    public static ImageIcon getResizeImage(String path, int width, int height){
        ImageIcon imageIcon = new ImageIcon(
                new ImageIcon(path)
                        .getImage()
                        .getScaledInstance(
                                width,
                                height,
                                Image.SCALE_SMOOTH
                        )
        );

        return imageIcon;
    }
}
