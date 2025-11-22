package view;

import interface_adapter.creategrouptasks.CreateGroupTasksController;
import interface_adapter.creategrouptasks.CreateGroupTasksState;
import interface_adapter.creategrouptasks.CreateGroupTasksViewModel;

import javax.swing.JButton;
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
 * Dialog window for creating a new task in a group.
 * <p>
 * Collects a short description, a due date, and a list of assignees, then
 * delegates the creation to the {@link CreateGroupTasksController}.
 * The {@link CreateGroupTasksViewModel} is observed to display success
 * or error messages. If the creation succeeds, the dialog closes itself.
 */
public class CreateTaskView extends JDialog implements PropertyChangeListener {

    private final String groupId;
    private final List<String> memberNames;

    private final CreateGroupTasksController controller;
    private final CreateGroupTasksViewModel viewModel;

    private JTextField descriptionField;
    private JTextField dueDateField;
    private JList<String> assigneeList;
    private JButton createButton;
    private JButton cancelButton;

    /**
     * Constructs a modal dialog for creating a new task.
     *
     * @param groupId     ID of the group in which the task is created
     * @param memberNames list of usernames that can be assigned this task
     * @param controller  controller for the create-group-tasks use case
     * @param viewModel   ViewModel providing creation result feedback
     */
    public CreateTaskView(String groupId,
                          List<String> memberNames,
                          CreateGroupTasksController controller,
                          CreateGroupTasksViewModel viewModel) {
        super((Frame) null, "Create Task", true);

        this.groupId = groupId;
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

        // Description
        formPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        descriptionField = new JTextField(25);
        formPanel.add(descriptionField, gbc);

        // Due date
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Due date (yyyy-MM-dd HH:mm):"), gbc);

        gbc.gridx = 1;
        dueDateField = new JTextField(20);
        formPanel.add(dueDateField, gbc);

        // Assignees
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        formPanel.add(new JLabel("Assign to:"), gbc);

        gbc.gridx = 1;
        assigneeList = new JList<>(memberNames.toArray(new String[0]));
        assigneeList.setVisibleRowCount(6);
        assigneeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane listScroll = new JScrollPane(assigneeList);
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(listScroll, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttons = new JPanel();
        createButton = new JButton("Create");
        cancelButton = new JButton("Cancel");

        createButton.addActionListener(e -> onCreate());
        cancelButton.addActionListener(e -> dispose());

        buttons.add(createButton);
        buttons.add(cancelButton);

        add(buttons, BorderLayout.SOUTH);
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

        List<String> selectedAssignees = assigneeList.getSelectedValuesList();

        // Delegate to the use case via controller.
        createButton.setEnabled(false);
        controller.execute(groupId, description, due, selectedAssignees);
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
