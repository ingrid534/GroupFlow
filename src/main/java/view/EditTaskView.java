package view;

import interface_adapter.editgrouptask.EditGroupTaskController;
import interface_adapter.editgrouptask.EditGroupTaskState;
import interface_adapter.editgrouptask.EditGroupTaskViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog window for editing an existing task in a group.
 * Allows updating the description, due date, completion status and assignees.
 * Delegates to the {@link EditGroupTaskController} and observes the
 * {@link EditGroupTaskViewModel} for success or error messages. On success,
 * the dialog closes itself.
 */
public class EditTaskView extends JDialog implements PropertyChangeListener {

    private final String taskId;
    private final List<String> memberNames;
    private final String groupId;
    private final String initialDescription;
    private final String initialDueDateString;
    private final List<String> initialAssignees;
    private final boolean initialCompleted;

    private final EditGroupTaskController controller;
    private final EditGroupTaskViewModel viewModel;

    private JTextField descriptionField;
    private JTextField dueDateField;
    private List<JCheckBox> assigneeCheckBoxes;
    private JCheckBox completedCheckBox;
    private JButton saveButton;
    private JButton cancelButton;

    /**
     * Constructs a modal dialog for editing a task.
     *
     * @param taskId               ID of the task to edit
     * @param memberNames          list of usernames that can be assigned this task
     * @param controller           controller for the edit-group-task use case
     * @param viewModel            ViewModel providing edit result feedback
     * @param groupId              The group Id
     * @param initialDescription   the initial description
     * @param initialDueDateString the initial due date
     * @param initialAssignees     the initial assignees
     * @param initialCompleted     the initial completed status
     */
    public EditTaskView(String taskId, List<String> memberNames,
                        EditGroupTaskController controller, EditGroupTaskViewModel viewModel,
                        String groupId, String initialDescription, String initialDueDateString, List<String> initialAssignees, boolean initialCompleted) {
        super((Frame) null, "Edit Task", true);
        this.taskId = taskId;
        this.memberNames = memberNames;
        this.groupId = groupId;
        this.controller = controller;
        this.viewModel = viewModel;
        this.initialDescription = initialDescription;
        this.initialDueDateString = initialDueDateString;
        this.initialAssignees = initialAssignees;
        this.initialCompleted = initialCompleted;

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

        JPanel formPanel = buildFormPanel();
        add(formPanel, BorderLayout.CENTER);

        JPanel buttons = buildButtonsPanel();
        add(buttons, BorderLayout.SOUTH);
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = baseGbc();

        addDescriptionField(panel, gbc);
        addDueDateField(panel, gbc);
        addAssigneeCheckboxes(panel, gbc);
        addCompletedCheckbox(panel, gbc);

        return panel;
    }

    private JPanel buildButtonsPanel() {
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        saveButton.addActionListener(event -> onSave());
        cancelButton.addActionListener(event -> dispose());

        JPanel buttons = new JPanel();
        buttons.add(saveButton);
        buttons.add(cancelButton);

        return buttons;
    }

    private GridBagConstraints baseGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private void addDescriptionField(JPanel panel, GridBagConstraints gbc) {
        panel.add(new JLabel("New description (optional):"), gbc);

        gbc.gridx = 1;
        descriptionField = new JTextField(25);
        if (initialDescription != null) {
            descriptionField.setText(initialDescription);
        }
        panel.add(descriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void addDueDateField(JPanel panel, GridBagConstraints gbc) {
        panel.add(new JLabel("New due date (yyyy-MM-dd HH:mm, optional):"), gbc);

        gbc.gridx = 1;
        dueDateField = new JTextField(20);
        if (initialDueDateString != null && !initialDueDateString.equals("No due date")) {
            dueDateField.setText(initialDueDateString);
        }
        panel.add(dueDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void addAssigneeCheckboxes(JPanel panel, GridBagConstraints gbc) {
        panel.add(new JLabel("New assignees (optional):"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;

        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));

        assigneeCheckBoxes = new ArrayList<>();
        for (String name : memberNames) {
            JCheckBox cb = new JCheckBox(name);
            if (initialAssignees != null && initialAssignees.contains(name)) {
                cb.setSelected(true);
            }
            assigneeCheckBoxes.add(cb);
            checkboxPanel.add(cb);
        }

        JScrollPane scroll = new JScrollPane(checkboxPanel);
        scroll.setPreferredSize(new Dimension(200, 120));
        panel.add(scroll, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void addCompletedCheckbox(JPanel panel, GridBagConstraints gbc) {
        completedCheckBox = new JCheckBox("Mark as completed");
        completedCheckBox.setSelected(initialCompleted);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(completedCheckBox, gbc);
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
        List<String> selectedAssignees = new ArrayList<>();
        for (JCheckBox cb : assigneeCheckBoxes) {
            if (cb.isSelected()) {
                selectedAssignees.add(cb.getText());
            }
        }

        if (selectedAssignees.isEmpty()) {
            selectedAssignees = null;
        }

        Boolean completed = Boolean.valueOf(completedCheckBox.isSelected());

        saveButton.setEnabled(false);
        controller.execute(taskId,
                newDescription, newDue, completed, selectedAssignees, groupId);
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
        this.viewModel.removePropertyChangeListener(this);
        super.dispose();
    }
}
