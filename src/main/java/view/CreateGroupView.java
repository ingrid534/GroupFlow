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

public class CreateGroupView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "create group";
    private final CreateGroupViewModel createGroupViewModel;

    private final JTextField groupNameInputField = new JTextField(15);
    private final JTextField groupTypeInputField = new JTextField(15);


    private final JButton createGroup;
    private final JButton cancel;
    private CreateGroupController createGroupController;

    public CreateGroupView(CreateGroupViewModel createGroupViewModel) {
        this.createGroupViewModel = createGroupViewModel;
        this.createGroupViewModel.addPropertyChangeListener(this);

        final JLabel title = new JLabel("Create Group Screen");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        final LabelTextPanel groupNameInfo = new LabelTextPanel(
                new JLabel("Group Name:"), groupNameInputField
        );

        final LabelTextPanel groupTypeInfo = new LabelTextPanel(
                new JLabel("Group Type:"), groupTypeInputField
        );

        final JPanel buttons = new JPanel();
        createGroup = new JButton("Create Group");
        buttons.add(createGroup);
        cancel = new JButton("Cancel");
        buttons.add(cancel);

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


        cancel.addActionListener(this);

        groupNameInputField.getDocument().addDocumentListener(new DocumentListener() {

            private void documentListenerHelper() {
                final CreateGroupState currentState = createGroupViewModel.getState();
                currentState.setGroupName(groupNameInputField.getText());
                createGroupViewModel.setState(currentState);
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

        groupTypeInputField.getDocument().addDocumentListener(new DocumentListener() {
            private void documentListenerHelper() {
                CreateGroupState currentState = createGroupViewModel.getState();
                currentState.setGroupType(groupTypeInputField.getText());
                createGroupViewModel.setState(currentState);
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
        this.add(title);
        this.add(groupNameInfo);
        this.add(groupTypeInfo);
        this.add(buttons);

    }

    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final CreateGroupState state = (CreateGroupState) evt.getNewValue();
        setFields(state);
    }

    private void setFields(CreateGroupState state) {
        groupNameInputField.setText(state.getGroupName());
        groupTypeInputField.setText(state.getGroupType());
    }

    public String getViewName() {
        return viewName;
    }

    public void setLoginController(CreateGroupController createGroupController) {
        this.createGroupController = createGroupController;
    }

}
