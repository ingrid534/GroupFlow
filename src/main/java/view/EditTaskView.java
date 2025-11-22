package view;

import interface_adapter.editgrouptask.EditGroupTaskController;
import interface_adapter.editgrouptask.EditGroupTaskState;
import interface_adapter.editgrouptask.EditGroupTaskViewModel;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * Dialog window for editing an existing task in a group.
 *
 * Allows updating the description, due date, completion status and assignees.
 * Delegates to the {@link EditGroupTaskController} and observes the
 * {@link EditGroupTaskViewModel} for success or error messages. On success,
 * the dialog closes itself.
 */
public class EditTaskView extends JDialog implements PropertyChangeListener {

    private final String groupId;
    private final String taskId;
    private final List<String> memberNames;

    private final EditGroupTaskController controller;
    private final EditGroupTaskViewModel viewModel;

    private JTextField descriptionField;
    private JTextField dueDateField;
    private JList<String> assigneeList;
    private JCheckBox completedCheckBox;
    private JButton saveButton;
    private JButton cancelButton;

    /**
     * Constructs a modal dialog for editing a task.
     *
     * @param groupId     ID of the group containing the task
     * @param taskId      ID of the task to edit
     * @param memberNames list of usernames that can be assigned this task
     * @param controller  controller for the edit-group-task use case
     * @param viewModel   ViewModel providing edit result feedback
     */
    public EditTaskView(String groupId, String taskId, List<String> memberNames,
                        EditGroupTaskController controller, EditGroupTaskViewModel viewModel) {
        super((Frame) null, "Edit Task", true);

        this.groupId = groupId;
        this.taskId = taskId;
        this.memberNames = memberNames;
        this.controller = controller;
        this.viewModel = viewModel;

        this.viewModel.addPropertyChangeListener(this);

        initComponents();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Initializes all Swing components and lays them out.
     */
    private void initComponents() {
        setLayout(new BorderLayout(8, 8));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Description (optional; empty means "no change")
        formPanel.add(new JLabel("New description (optional):"), gbc);

        gbc.gridx = 1;
        descriptionField = new JTextField(25);
        formPanel.add(descriptionField, gbc);

        // Due date (optional; empty means "no change")
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("New due date (yyyy-MM-dd HH:mm, optional):"), gbc);

        gbc.gridx = 1;
        dueDateField = new JTextField(20);
        formPanel.add(dueDateField, gbc);

        // Assignees (optional; empty list means no change)
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        formPanel.add(new JLabel("New assignees (optional):"), gbc);

        gbc.gridx = 1;
        assigneeList = new JList<>(memberNames.toArray(new String[0]));
        assigneeList.setVisibleRowCount(6);
        assigneeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane listScroll = new JScrollPane(assigneeList);
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(listScroll, gbc);

        // Completed checkbox
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_START;
        completedCheckBox = new JCheckBox("Mark as completed");
        // When unchecked, we still send "false" (explicit change).
        formPanel.add(completedCheckBox, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttons = new JPanel();
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> dispose());

        buttons.add(saveButton);
        buttons.add(cancelButton);

        add(buttons, BorderLayout.SOUTH);
    }

    /**
     * Handles the "Save" button click:
     * gathers form input and calls the controller.
     */
    private void onSave() {
        String newDescription = descriptionField.getText().trim();
        if (newDescription.isEmpty()) {
            newDescription = null;
        }

        String newDue = dueDateField.getText().trim();
        if (newDue.isEmpty()) {
            newDue = null;
        }

        // If the user selects no assignees, treat it as "no change".
        List<String> selectedAssignees = assigneeList.getSelectedValuesList();
        if (selectedAssignees.isEmpty()) {
            selectedAssignees = null;
        }

        Boolean completed = Boolean.valueOf(completedCheckBox.isSelected());

        saveButton.setEnabled(false);
        controller.execute(groupId, taskId,
                newDescription, newDue, completed, selectedAssignees);
    }

    /**
     * Reacts to changes in the EditGroupTaskViewModel.
     *
     * @param evt the property change event fired by the ViewModel
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"edit_result".equals(evt.getPropertyName())) {
            return;
        }

        EditGroupTaskState state = viewModel.getState();
        saveButton.setEnabled(true);

        if (state.isSuccess()) {
            JOptionPane.showMessageDialog(this,
                    state.getMessage(),
                    "Task Updated",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    state.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
