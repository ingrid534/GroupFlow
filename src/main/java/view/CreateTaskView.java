package view;

import interface_adapter.creategrouptasks.CreateGroupTasksController;
import interface_adapter.creategrouptasks.CreateGroupTasksState;
import interface_adapter.creategrouptasks.CreateGroupTasksViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog window for creating a new task in a group.
 * Collects a short description, a due date, and a list of assignees, then
 * delegates the creation to the {@link CreateGroupTasksController}.
 * The {@link CreateGroupTasksViewModel} is observed to display success
 * or error messages. If the creation succeeds, the dialog closes itself.
 */
public class CreateTaskView extends JDialog implements PropertyChangeListener {

    private final List<String> memberNames;

    private final CreateGroupTasksController controller;
    private final CreateGroupTasksViewModel viewModel;

    private JTextField descriptionField;
    private JTextField dueDateField;
    private java.util.List<JCheckBox> assigneeCheckBoxes;
    private JButton createButton;
    private JButton cancelButton;

    /**
     * Constructs a modal dialog for creating a new task.
     *
     * @param memberNames list of usernames that can be assigned this task
     * @param controller  controller for the create-group-tasks use case
     * @param viewModel   ViewModel providing creation result feedback
     */
    public CreateTaskView(List<String> memberNames,
                          CreateGroupTasksController controller,
                          CreateGroupTasksViewModel viewModel) {
        super((Frame) null, "Create Task", true);

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

        return panel;
    }

    private JPanel buildButtonsPanel() {
        createButton = new JButton("Create");
        cancelButton = new JButton("Cancel");

        createButton.addActionListener(event -> onCreate());
        cancelButton.addActionListener(event -> dispose());

        JPanel buttons = new JPanel();
        buttons.add(createButton);
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
        panel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        descriptionField = new JTextField(25);
        panel.add(descriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void addDueDateField(JPanel panel, GridBagConstraints gbc) {
        panel.add(new JLabel("Due date (yyyy-MM-dd HH:mm):"), gbc);

        gbc.gridx = 1;
        dueDateField = new JTextField(20);
        panel.add(dueDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void addAssigneeCheckboxes(JPanel panel, GridBagConstraints gbc) {
        panel.add(new JLabel("Assign to:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;

        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));

        assigneeCheckBoxes = new ArrayList<>();
        for (String name : memberNames) {
            JCheckBox cb = new JCheckBox(name);
            assigneeCheckBoxes.add(cb);
            checkboxPanel.add(cb);
        }

        JScrollPane scroll = new JScrollPane(checkboxPanel);
        scroll.setPreferredSize(new Dimension(200, 120));
        panel.add(scroll, gbc);
    }

    /**
     * Handles the "Create" button click:
     * gathers form input and calls the controller.
     */
    private void onCreate() {
        String description = descriptionField.getText().trim();
        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Description cannot be empty.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String due = dueDateField.getText().trim();
        if (due.isEmpty()) {
            due = null;
        }

        List<String> selectedAssignees = new ArrayList<>();
        for (JCheckBox cb : assigneeCheckBoxes) {
            if (cb.isSelected()) {
                selectedAssignees.add(cb.getText());
            }
        }

        // Delegate to the use case via controller.
        createButton.setEnabled(false);
        controller.execute(description, due, selectedAssignees);
    }

    /**
     * Reacts to changes in the CreateGroupTasksViewModel.
     *
     * @param evt the property change event fired by the ViewModel
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"create_result".equals(evt.getPropertyName())) {
            return;
        }

        CreateGroupTasksState state = viewModel.getState();
        createButton.setEnabled(true);

        if (state.isSuccess()) {
            JOptionPane.showMessageDialog(this,
                    state.getMessage(),
                    "Task Created",
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
