package view;

import javax.swing.*;
import java.awt.*;

public class FieldUIFactory {
    public static void styleInputField(JTextField field) {
        field.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)));
        field.setFont(new Font("SansSerif", Font.PLAIN, 18));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setBackground(Color.WHITE);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    public static JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SanSerif", Font.PLAIN, 14));
        label.setForeground(new Color(120, 120, 120));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

}
