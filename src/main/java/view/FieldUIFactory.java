package view;

import javax.swing.*;
import java.awt.*;

/**
 * Utility factory for styling common form field UI components.
 *
 * <p>
 * Provides methods to apply a consistent look to input text fields and to
 * create standardized labels for form fields used across the UI.
 * </p>
 *
 * <p>Usage examples:
 * <ul>
 *     <li>Call {@link #styleInputField(JTextField)} to make a text field match the app's form input style.</li>
 *     <li>Call {@link #createFieldLabel(String)} to obtain a JLabel styled for form field captions.</li>
 * </ul>
 * </p>
 */
public class FieldUIFactory {

    /**
     * Apply a transparent border and grey underline with Sans Serif font.
     *
     * @param field the field to style
     */
    public static void styleInputField(JTextField field) {
        field.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)));
        field.setFont(new Font("SansSerif", Font.PLAIN, 18));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        // field.setBackground(Color.WHITE);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    /**
     * Create a left-aligned label styled for form fields.
     *
     * @param text label text
     * @return configured JLabel
     */
    public static JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SanSerif", Font.PLAIN, 14));
        label.setForeground(Color.LIGHT_GRAY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

}
