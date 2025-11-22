package view;

import interface_adapter.creategrouptasks.CreateGroupTasksController;
import interface_adapter.creategrouptasks.CreateGroupTasksViewModel;
import interface_adapter.editgrouptask.EditGroupTaskController;
import interface_adapter.editgrouptask.EditGroupTaskViewModel;
import interface_adapter.viewgrouptasks.ViewGroupTasksController;
import interface_adapter.viewgrouptasks.ViewGroupTasksViewModel;
import use_case.viewgrouptasks.ViewGroupTasksOutputData;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * UI panel inside the "Tasks" tab of a group's workspace.
 * Displays all tasks belonging to the group and allows moderators
 * to create and edit tasks.
 */
public class GroupTasksView extends JPanel implements PropertyChangeListener {
    private final String groupId;
    private final String userId;
    private final List<String> usernames;

    private final ViewGroupTasksViewModel viewModel;
    private final EditGroupTaskViewModel editModel;
    private final CreateGroupTasksViewModel createModel;

    private final ViewGroupTasksController viewController;
    private final EditGroupTaskController editController;
    private final CreateGroupTasksController createController;

    private final JPanel tasksListPanel = new JPanel();

    /**
     * Constructs a GroupTasksView.
     *
     * @param groupId         The group to display tasks for
     * @param userId          The user currently logged in
     * @param memberNames     The list of usernames of the users of the group
     * @param viewModel       ViewModel for viewing tasks
     * @param editModel       ViewModel for editing tasks
     * @param createModel     ViewModel for creating tasks
     * @param viewController  Controller for view tasks use case
     * @param editController  Controller for edit tasks use case
     * @param createController Controller for create tasks use case
     */
    public GroupTasksView(
            String groupId,
            String userId,
            List<String> memberNames,
            ViewGroupTasksViewModel viewModel,
            EditGroupTaskViewModel editModel,
            CreateGroupTasksViewModel createModel,
            ViewGroupTasksController viewController,
            EditGroupTaskController editController,
            CreateGroupTasksController createController) {

        this.groupId = groupId;
        this.userId = userId;
        this.usernames = memberNames;

        this.viewModel = viewModel;
        this.editModel = editModel;
        this.createModel = createModel;

        this.viewController = viewController;
        this.editController = editController;
        this.createController = createController;

        this.viewModel.addPropertyChangeListener(this);
        this.editModel.addPropertyChangeListener(this);
        this.createModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        tasksListPanel.setLayout(new BoxLayout(tasksListPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(tasksListPanel), BorderLayout.CENTER);

        refresh();

        // load tasks immediately
        viewController.execute(groupId);
    }

    /** Refreshes the UI according to the ViewModel state. */
    private void refresh() {
        tasksListPanel.removeAll();

        JButton createBtn = new JButton("Create Task");
        createBtn.addActionListener(e -> openCreateDialog());
        tasksListPanel.add(createBtn);

        List<ViewGroupTasksOutputData.TaskDTO> tasks = viewModel.getState().getTasks();

        if (tasks == null || tasks.isEmpty()) {
            tasksListPanel.add(new JLabel("No tasks yet."));
        } else {
            for (ViewGroupTasksOutputData.TaskDTO task : tasks) {
                tasksListPanel.add(makeTaskRow(task));
            }
        }

        tasksListPanel.revalidate();
        tasksListPanel.repaint();
    }

    /**
     * Builds a single row in the task list for the given task.
     *
     * @param dto the task DTO to display
     * @return a panel representing one task row
     */
    private JPanel makeTaskRow(ViewGroupTasksOutputData.TaskDTO dto) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        String text = dto.getDescription();
        if (dto.getDueDateString() != null && !dto.getDueDateString().isEmpty()) {
            text += " (due " + dto.getDueDateString() + ")";
        }
        row.add(new JLabel(text), BorderLayout.CENTER);

        JButton editBtn = new JButton("Edit");
        editBtn.addActionListener(e -> openEditDialog(dto));
        row.add(editBtn, BorderLayout.EAST);

        return row;
    }

    /**
     * Opens the dialog window for creating a new task.
     * The dialog is responsible for collecting user input and invoking
     * the CreateGroupTasksController.
     */
    private void openCreateDialog() {
        new CreateTaskView(userId, groupId, usernames, createController, createModel);
    }

    /**
     * Opens the dialog window for editing an existing task.
     *
     * @param dto the task DTO to edit
     */
    private void openEditDialog(ViewGroupTasksOutputData.TaskDTO dto) {
        new EditTaskView(userId, groupId, dto.getId(), usernames, editController, editModel);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();

        if ("tasks".equals(prop)) {
            // ViewGroupTasks presenter updated the tasks list.
            refresh();
        } else if ("edit_result".equals(prop) || "create_result".equals(prop)) {
            // After a successful or failed edit/create, reload tasks from the use case.
            viewController.execute(groupId);
        }
    }
}
