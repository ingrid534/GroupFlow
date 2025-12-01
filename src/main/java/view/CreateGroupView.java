package view;

import interface_adapter.create_group.CreateGroupController;
import interface_adapter.create_group.CreateGroupState;
import interface_adapter.create_group.CreateGroupViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import entity.group.GroupType;

import static view.FieldUIFactory.*;

/**
 * The CreateGroupView class represents the user interface for creating a group.
 * It provides input fields for the group name and type,
 * as well as buttons to create or cancel the group creation process.
 * This class listens to user actions and updates the view model accordingly.
 */
public class CreateGroupView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "create group";
    private final CreateGroupViewModel createGroupViewModel;
    private JDialog dialog;

    private final JTextField groupNameInputField = new JTextField(15);
    private final JComboBox<GroupType> groupTypeInputField = new JComboBox<>(GroupType.values());

    private final JLabel title;
    private final JButton createGroup;
    private final JButton cancel;
    private final JLabel errorField = new JLabel();

    private final LabelTextPanel groupNameInfo;
    private final JPanel groupTypeInfo;

    private CreateGroupController createGroupController;

    public CreateGroupView(CreateGroupViewModel createGroupViewModel) {
        this.createGroupViewModel = createGroupViewModel;
        this.createGroupViewModel.addPropertyChangeListener(this);
        setBackground(Color.WHITE);

        title = new JLabel("Enter your group's details.");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        title.setMaximumSize(new Dimension(Integer.MAX_VALUE, title.getPreferredSize().height));

        styleFields();

        // Group Name panel
        groupNameInfo = createGroupNamePanel();

        // Group Type panel
        groupTypeInfo = createGroupTypePanel();

        // Buttons
        cancel = new JButton("Cancel");

        createGroup = new JButton("Create Group");

        errorField.setForeground(Color.RED);
        errorField.setSize(new Dimension(errorField.getPreferredSize().width, 30));
        errorField.setAlignmentX(Component.LEFT_ALIGNMENT);

        buildLayout();

        // Document listeners
        addDocumentListeners();
    }

    private LabelTextPanel createGroupNamePanel() {
        final LabelTextPanel panel;
        JLabel groupNameLabel = createFieldLabel("Group Name");

        panel = new LabelTextPanel(groupNameLabel, groupNameInputField);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    private JPanel createGroupTypePanel() {
        // Panel to hold label + combo box
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        // Label
        JLabel groupTypeLabel = createFieldLabel("Group Type");
        groupTypeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Combo box
        groupTypeInputField.setAlignmentX(Component.LEFT_ALIGNMENT);
        groupTypeInputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        // Add components with controlled vertical gap
        panel.add(groupTypeLabel);
        panel.add(groupTypeInputField);

        // Stretch panel width to match parent, but height unconstrained
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        return panel;
    }

    /**
     * Builds the layout of the Create Group view.
     * This method arranges the components in the panel.
     */
    private void buildLayout() {
        // Layout main panel
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(30, 50, 40, 50));

        // Add rigid areas for spacing
        this.add(title);
        this.add(Box.createVerticalStrut(30));

        this.add(groupNameInfo);
        this.add(Box.createVerticalStrut(30));

        this.add(groupTypeInfo);
        this.add(Box.createVerticalStrut(30));

        this.add(errorField);
        this.add(Box.createVerticalStrut(10));

        this.add(createGroup);
        this.add(Box.createVerticalStrut(10));
        this.add(cancel);

        groupTypeInfo.setMaximumSize(new Dimension(Integer.MAX_VALUE, groupTypeInfo.getPreferredSize().height));
    }

    private void addDocumentListeners() {
        groupNameInputField.getDocument().addDocumentListener(new DocumentListener() {
            private void updateState() {
                CreateGroupState currentState = createGroupViewModel.getState();
                currentState.setGroupName(groupNameInputField.getText());
                createGroupViewModel.setState(currentState);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateState();
            }
        });

        groupTypeInputField.addActionListener(evt -> {
            CreateGroupState currentState = createGroupViewModel.getState();
            currentState.setGroupType((GroupType) groupTypeInputField.getSelectedItem());
            createGroupViewModel.setState(currentState);
        });

    }

    private void styleFields() {
        styleInputField(groupNameInputField);
    }

    /**
     * Handles button click events.
     *
     * @param evt the action event triggered by a button click
     */
    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    /**
     * Responds to property change events from the view model.
     *
     * @param evt the property change event containing the updated state
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        final CreateGroupState state = (CreateGroupState) evt.getNewValue();
        setFields(state);

        errorField.setText(state.getError());
    }

    /**
     * Updates the input fields based on the given state.
     *
     * @param state the current state of the Create Group view
     */
    private void setFields(CreateGroupState state) {
        groupNameInputField.setText(state.getGroupName());
        groupTypeInputField.setSelectedItem(state.getGroupType());
    }

    /**
     * Returns the name of the view.
     *
     * @return the name of the view
     */
    public String getViewName() {
        return viewName;
    }

    /**
     * Sets the Create Group controller and attaches listeners to the buttons.
     *
     * @param createGroupController the controller for handling Create Group actions
     */
    public void setCreateGroupController(CreateGroupController createGroupController) {
        this.createGroupController = createGroupController;
        attachListeners();

    }

    /**
     * Attaches listeners to the Create Group button.
     */
    private void attachListeners() {
        createGroup.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {

                        if (!evt.getSource().equals(createGroup)) {
                            return;
                        }

                        final CreateGroupState currentState = createGroupViewModel.getState();

                        createGroupController.execute(
                                currentState.getGroupName(),
                                currentState.getGroupType()
                        );
                    }
                }
        );

        cancel.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        WindowEvent we = new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING);
                        dialog.dispatchEvent(we);
                        createGroupViewModel.setState(new CreateGroupState());
                    }
                }
        );
    }

    /**
     * Hooks the Create Group modal to the application frame.
     * This method listens for changes in the view model to open or close the modal.
     *
     * @param application the main application frame
     */
    public void hookCreateGroupModalOpen(JFrame application) {
        dialog = new JDialog(application, "Create Group", true);
        createGroupViewModel.addPropertyChangeListener(evt -> {
            if ("openModal".equals(evt.getPropertyName())) {
                if (createGroupViewModel.getState().getOpenModal()) {
                    openCreateGroupModal(application);
                } else {
                    dialog.dispose();
                }
            }
        });
    }

    /**
     * Opens the Create Group modal dialog.
     *
     * @param parentFrame the parent frame of the modal dialog
     */
    private void openCreateGroupModal(JFrame parentFrame) {
        dialog.setMinimumSize(new Dimension(500, 370));
        dialog.setContentPane(this);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.pack();
        dialog.setVisible(true);
    }

}
