package view;

import interface_adapter.joingroup.JoinGroupController;
import interface_adapter.joingroup.JoinGroupState;
import interface_adapter.joingroup.JoinGroupViewModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

public class JoinGroupView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "join group";

    private final JoinGroupViewModel joinGroupviewModel;
    private final JTextField groupCodeField = new JTextField(15);
    private JoinGroupController controller;

    private final JButton joinButton;
    private final JButton cancelButton;

    public JoinGroupView(JoinGroupViewModel viewModel) {
        this.joinGroupviewModel = viewModel;
        this.joinGroupviewModel.addPropertyChangeListener(this);

        JLabel title = new JLabel(JoinGroupViewModel.TITLE_LABEL);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        LabelTextPanel groupCodePanel;
        groupCodePanel = new LabelTextPanel(
                new JLabel(JoinGroupViewModel.GROUP_CODE_LABEL), groupCodeField);

        JPanel buttons = new JPanel();
        joinButton = new JButton(JoinGroupViewModel.JOIN_BUTTON_LABEL);
        cancelButton = new JButton(JoinGroupViewModel.CANCEL_BUTTON_LABEL);
        buttons.add(joinButton);
        buttons.add(cancelButton);

        joinButton.addActionListener(e00 -> {
            JoinGroupState state = joinGroupviewModel.getState();
            try {
                controller.execute(state.getGroupCode());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        cancelButton.addActionListener(this);

        addGroupCodeListener();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(title);
        this.add(groupCodePanel);
        this.add(buttons);
    }

    private void addGroupCodeListener() {
        groupCodeField.getDocument().addDocumentListener(new DocumentListener() {

            private void updateState() {
                JoinGroupState state = joinGroupviewModel.getState();
                state.setGroupCode(groupCodeField.getText());
                joinGroupviewModel.setState(state);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateState(); }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateState(); }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateState(); }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(this, "Cancel not implemented yet.");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        JoinGroupState state = (JoinGroupState) evt.getNewValue();
        if (state.getGroupCodeError() != null) {
            JOptionPane.showMessageDialog(this, state.getGroupCodeError());
        }
    }

    public String getViewName() {
        return viewName; }

    public void setJoinGroupController(JoinGroupController control) {
        this.controller = control;
    }
}
