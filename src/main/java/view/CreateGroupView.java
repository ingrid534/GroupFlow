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

public class CreateGroupView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "create group";
    private final CreateGroupViewModel createGroupViewModel;

    private final JTextField groupNameInputField = new JTextField(15);
    private final JComboBox<GroupType> groupTypeInputField = new JComboBox<>(GroupType.values());

    private final JButton createGroup;
    private final JButton cancel;

    private final JLabel errorField = new JLabel();

    private CreateGroupController createGroupController;

    public CreateGroupView(CreateGroupViewModel createGroupViewModel) {
        this.createGroupViewModel = createGroupViewModel;
        this.createGroupViewModel.addPropertyChangeListener(this);

        JLabel title = new JLabel("Enter your group's details.");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        // Group Name panel
        LabelTextPanel groupNameInfo = new LabelTextPanel(
                new JLabel("Group Name:"), groupNameInputField
        );
        groupNameInputField.setMaximumSize(new Dimension(200, 25));
        groupNameInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Group Type panel
        groupTypeInputField.setSelectedItem(GroupType.PROJECT);
        groupTypeInputField.setMaximumSize(new Dimension(200, 25));
        JPanel groupTypeInfo = new JPanel();
        groupTypeInfo.setLayout(new BoxLayout(groupTypeInfo, BoxLayout.X_AXIS));
        groupTypeInfo.add(new JLabel("Group Type:"));
        groupTypeInfo.add(Box.createRigidArea(new Dimension(10, 0)));
        groupTypeInfo.add(groupTypeInputField);
        groupTypeInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buttons
        JPanel buttons = new JPanel();
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

            @Override public void insertUpdate(DocumentEvent e) { updateState(); }
            @Override public void removeUpdate(DocumentEvent e) { updateState(); }
            @Override public void changedUpdate(DocumentEvent e) { updateState(); }
        });

        groupTypeInputField.addActionListener(e -> {
            CreateGroupState currentState = createGroupViewModel.getState();
            currentState.setGroupType((GroupType) groupTypeInputField.getSelectedItem());
            createGroupViewModel.setState(currentState);
        });

        // Layout main panel
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setMinimumSize(new Dimension(300, 200));
        this.add(Box.createRigidArea(new Dimension(0, 10)));  // Add rigid areas for spacing
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


    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        final CreateGroupState state = (CreateGroupState) evt.getNewValue();
        setFields(state);

        errorField.setText(state.getError());
    }

    private void setFields(CreateGroupState state) {
        groupNameInputField.setText(state.getGroupName());
        groupTypeInputField.setSelectedItem(state.getGroupType());
    }

    public String getViewName() {
        return viewName;
    }

    public void setCreateGroupController(CreateGroupController createGroupController) {
        this.createGroupController = createGroupController;
        attachListeners();

    }

    public void attachListeners() {
        createGroup.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {

                        if (!evt.getSource().equals(createGroup)) {return;}

                        final CreateGroupState currentState = createGroupViewModel.getState();

                        createGroupController.execute(
                                currentState.getGroupName(),
                                currentState.getGroupType()
                        );
                    }
                }
        );
    }

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

    private void openCreateGroupModal(JDialog dialog, JFrame parentFrame) {
        dialog.setMinimumSize(new Dimension(400, 250)); // user cannot shrink below this
        dialog.setContentPane(this);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.pack();
        dialog.setVisible(true);
    }

}
