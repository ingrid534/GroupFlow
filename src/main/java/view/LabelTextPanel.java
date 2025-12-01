package view;

import javax.swing.*;

/**
 * A panel containing a label and a text field.
 */
class LabelTextPanel extends JPanel {
    LabelTextPanel(JLabel label, JTextField textField) {
        this.add(label);
        add(Box.createVerticalStrut(5));
        this.add(textField);
    }
}
