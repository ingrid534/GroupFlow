package view;

import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupState;
import interface_adapter.signup.SignupViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The View for the Signup Use Case.
 */
public class SignupView extends JPanel implements ActionListener, PropertyChangeListener {
    private final String viewName = "sign up";

    private final SignupViewModel signupViewModel;

    private final JTextField usernameInputField = new JTextField(20);
    private final JTextField emailInputField = new JTextField(20);

    private final JPasswordField passwordInputField = new JPasswordField(20);
    private final JPasswordField repeatPasswordInputField = new JPasswordField(20);

    private SignupController signupController;

    private final JButton signUp;
    private final JButton toLogin;

    public SignupView(SignupViewModel signupViewModel) {
        this.signupViewModel = signupViewModel;
        signupViewModel.addPropertyChangeListener(this);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(30, 50, 40, 50));

        styleField(usernameInputField);
        styleField(emailInputField);
        styleField(passwordInputField);
        styleField(repeatPasswordInputField);

        signUp = new JButton("Sign Up");
        signUp.setAlignmentX(Component.LEFT_ALIGNMENT);

        toLogin = new JButton("Go to login");
        toLogin.setAlignmentX(Component.LEFT_ALIGNMENT);

        addFieldLabels();

        addActionListeners();
        // Check similarity to previous

        addDocumentListeners();

    }

    private void addFieldLabels() {
        this.add(createTitleLabel());
        this.add(Box.createVerticalStrut(30));

        this.add(createFieldLabel("Username"));
        this.add(usernameInputField);
        this.add(Box.createVerticalStrut(20));

        this.add(createFieldLabel("Email"));
        this.add(emailInputField);
        this.add(Box.createVerticalStrut(20));

        this.add(createFieldLabel("Password"));
        this.add(passwordInputField);
        this.add(Box.createVerticalStrut(20));

        this.add(createFieldLabel("Confirm Password"));
        this.add(repeatPasswordInputField);
        this.add(Box.createVerticalStrut(20));

        this.add(signUp);
        this.add(Box.createVerticalStrut(10));
        this.add(toLogin);
    }

    private JLabel createTitleLabel() {
        final JLabel title = new JLabel("Sign Up");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setForeground(Color.BLACK);

        return title;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(120, 120, 120));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

    private void addActionListeners() {
        signUp.addActionListener(
                // This creates an anonymous subclass of ActionListener and instantiates it.
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if (evt.getSource().equals(signUp)) {
                            final SignupState currentState = signupViewModel.getState();
                            // TODO: Pass in email to interactor
                            signupController.execute(
                                    currentState.getUsername(),
                                    currentState.getPassword(),
                                    currentState.getRepeatPassword()
                            );
                        }
                    }
                }
        );

        toLogin.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        signupController.switchToLoginView();
                    }
                }
        );
    }

    private void addDocumentListeners() {
        addFieldDocumentListener("username", usernameInputField);
        addFieldDocumentListener("email", emailInputField);
        addFieldDocumentListener("password", passwordInputField);
        addFieldDocumentListener("repeat password", repeatPasswordInputField);
    }

    private void styleField(JTextField field) {
        field.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)));
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setBackground(Color.WHITE);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void addFieldDocumentListener(String fieldName, JTextField field) {
        field.getDocument().addDocumentListener(new DocumentListener() {

            private void documentListenerHelper() {
                final SignupState currentState = signupViewModel.getState();
                currentState.setField(fieldName, field.getText());
                signupViewModel.setState(currentState);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentListenerHelper();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        JOptionPane.showMessageDialog(this, "Cancel not implemented yet.");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final SignupState state = (SignupState) evt.getNewValue();
        if (state.getUsernameError() != null) {
            JOptionPane.showMessageDialog(this, state.getUsernameError());
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setSignupController(SignupController controller) {
        this.signupController = controller;
    }
}

