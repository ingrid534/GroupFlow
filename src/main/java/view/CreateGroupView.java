package view;

import interface_adapter.create_group.CreateGroupController;
import interface_adapter.create_group.CreateGroupState;
import interface_adapter.create_group.CreateGroupViewModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import entity.group.GroupType;

/**
 * The CreateGroupView class represents the user interface for creating a group.
 * It provides input fields for the group name and type,
 * as well as buttons to create or cancel the group creation process.
 * This class listens to user actions and updates the view model accordingly.
 */
public class CreateGroupView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "create group";
    private final CreateGroupViewModel createGroupViewModel;

    private final JTextField groupNameInputField = new JTextField(15);
    private final JComboBox<GroupType> groupTypeInputField = new JComboBox<>(GroupType.values());

    private final JLabel title;
    private final JButton createGroup;
    private final JButton cancel;
    private final JLabel errorField = new JLabel();

    private final LabelTextPanel groupNameInfo;
    private final JPanel groupTypeInfo;

    private final JPanel buttons;

    private CreateGroupController createGroupController;

    public CreateGroupView(CreateGroupViewModel createGroupViewModel) {
        this.createGroupViewModel = createGroupViewModel;
        this.createGroupViewModel.addPropertyChangeListener(this);

        title = new JLabel("Enter your group's details.");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        // Group Name panel
        groupNameInfo = new LabelTextPanel(
                new JLabel("Group Name:"), groupNameInputField
        );
        groupNameInputField.setMaximumSize(new Dimension(200, 25));
        groupNameInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Group Type panel
        groupTypeInputField.setSelectedItem(GroupType.PROJECT);
        groupTypeInputField.setMaximumSize(new Dimension(200, 25));
        groupTypeInfo = new JPanel();
        groupTypeInfo.setLayout(new BoxLayout(groupTypeInfo, BoxLayout.X_AXIS));
        groupTypeInfo.add(new JLabel("Group Type:"));
        groupTypeInfo.add(Box.createRigidArea(new Dimension(10, 0)));
        groupTypeInfo.add(groupTypeInputField);
        groupTypeInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buttons
        buttons = new JPanel();
        buttons.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        createGroup = new JButton("Create Group");
        cancel = new JButton("Cancel");
        buttons.add(createGroup);
        buttons.add(cancel);
        buttons.setAlignmentX(Component.CENTER_ALIGNMENT);

        cancel.addActionListener(this);

        errorField.setForeground(Color.RED);
        errorField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Document listeners
        groupNameInputField.getDocument().addDocumentListener(new DocumentListener() {
            private void updateState() {
                CreateGroupState currentState = createGroupViewModel.getState();
                currentState.setGroupName(groupNameInputField.getText());
                createGroupViewModel.setState(currentState);
            }

            @Override public void insertUpdate(DocumentEvent e) {
                updateState();
            }

            @Override public void removeUpdate(DocumentEvent e) {
                updateState();
            }

            @Override public void changedUpdate(DocumentEvent e) {
                updateState();
            }
        });

        groupTypeInputField.addActionListener(evt -> {
            CreateGroupState currentState = createGroupViewModel.getState();
            currentState.setGroupType((GroupType) groupTypeInputField.getSelectedItem());
            createGroupViewModel.setState(currentState);
        });

        buildLayout();

    }

    /**
     * Builds the layout of the Create Group view.
     * This method arranges the components in the panel.
     */
    public void buildLayout() {
        // Layout main panel
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setMinimumSize(new Dimension(300, 200));
        // Add rigid areas for spacing
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(title);
        this.add(Box.createRigidArea(new Dimension(0, 15)));
        this.add(groupNameInfo);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(groupTypeInfo);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(errorField);
        this.add(Box.createRigidArea(new Dimension(0, 15)));
        this.add(buttons);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
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
    public void attachListeners() {
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
    }

    /**
     * Hooks the Create Group modal to the application frame.
     * This method listens for changes in the view model to open or close the modal.
     *
     * @param application the main application frame
     */
    public void hookCreateGroupModalOpen(JFrame application) {
        JDialog dialog = new JDialog(application, "Create Group", true);
        createGroupViewModel.addPropertyChangeListener(evt -> {
            if ("openModal".equals(evt.getPropertyName())) {
                if (createGroupViewModel.getState().getOpenModal()) {
                    openCreateGroupModal(dialog, application);
                } else {
                    dialog.dispose();
                }
            }
        });
    }

    /**
     * Opens the Create Group modal dialog.
     *
     * @param dialog the modal dialog to display
     * @param parentFrame the parent frame of the modal dialog
     */
    private void openCreateGroupModal(JDialog dialog, JFrame parentFrame) {
        dialog.setMinimumSize(new Dimension(400, 250));
        dialog.setContentPane(this);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.pack();
        dialog.setVisible(true);
    }

}
