package view;

import interface_adapter.login.LoginController;
import interface_adapter.login.LoginState;
import interface_adapter.login.LoginViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The View for when the user is logging into the program.
 */
public class LoginView extends JPanel implements ActionListener, PropertyChangeListener {
    private final String viewName = "log in";
    private final LoginViewModel loginViewModel;

    private final JTextField usernameInputField = new JTextField(20);

    private final JPasswordField passwordInputField = new JPasswordField(20);

    private final JLabel usernameErrorField = new JLabel();

    private final JButton logIn;
    private final JButton toSignup;
    private LoginController loginController;

    public LoginView(LoginViewModel loginViewModel) {
        this.loginViewModel = loginViewModel;
        this.loginViewModel.addPropertyChangeListener(this);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(30, 50, 40, 50));

        styleField(usernameInputField);
        styleField(passwordInputField);

        logIn = new JButton("Login");
        logIn.setAlignmentX(Component.LEFT_ALIGNMENT);

        toSignup = new JButton("Go to signup");
        toSignup.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.add(createTitleLabel());
        this.add(Box.createVerticalStrut(30));

        this.add(createUsernameLabel());
        this.add(usernameInputField);
        this.add(Box.createVerticalStrut(20));

        this.add(createPasswordLabel());
        this.add(passwordInputField);
        this.add(Box.createVerticalStrut(10));

        this.add(usernameErrorField);
        usernameErrorField.setForeground(Color.RED);
        usernameErrorField.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.add(Box.createVerticalStrut(10));

        this.add(logIn);
        this.add(Box.createVerticalStrut(20));
        this.add(toSignup);

        actionListeners();

        usernameInputField.getDocument().addDocumentListener(new DocumentListener() {
            private void documentListenerHelper() {
                final LoginState currentState = loginViewModel.getState();
                currentState.setUsername(usernameInputField.getText());
                loginViewModel.setState(currentState);
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

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        passwordInputField.getDocument().addDocumentListener(new DocumentListener() {
            private void documentListenerHelper() {
                final LoginState currentState = loginViewModel.getState();
                currentState.setPassword(new String(passwordInputField.getPassword()));
                loginViewModel.setState(currentState);
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

    private void actionListeners() {
        logIn.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if (evt.getSource().equals(logIn)) {
                            final LoginState currentState = loginViewModel.getState();
                            loginController.execute(
                                    currentState.getUsername(),
                                    currentState.getPassword());
                        }
                    }
                });

        toSignup.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        loginController.switchToSignupView();
                    }
                });
    }

    private JLabel createTitleLabel() {
        JLabel title = new JLabel("Log In");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setForeground(Color.BLACK);

        return title;
    }

    private JLabel createUsernameLabel() {
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setForeground(new Color(120, 120, 120));
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        return usernameLabel;
    }

    private JLabel createPasswordLabel() {
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setForeground(new Color(120, 120, 120));
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        return passwordLabel;
    }

    private void styleField(JTextField field) {
        field.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)));
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setBackground(Color.WHITE);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    /**
     * React to a button click that results in evt.
     *
     * @param evt the ActionEvent to react to
     */
    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final LoginState state = (LoginState) evt.getNewValue();
        setFields(state);
        passwordInputField.setText("");
        usernameErrorField.setText(state.getLoginError());
    }

    private void setFields(LoginState state) {
        usernameInputField.setText(state.getUsername());
    }

    public String getViewName() {
        return viewName;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }
}
