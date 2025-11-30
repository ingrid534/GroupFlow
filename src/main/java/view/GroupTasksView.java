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
import java.util.ArrayList;
import java.util.List;

/**
 * UI panel inside the "Tasks" tab of a group's workspace.
 * Displays all tasks belonging to the group and allows moderators
 * to create and edit tasks.
 */
public class GroupTasksView extends JPanel implements PropertyChangeListener {

    private final List<String> usernames;
    private final ViewGroupTasksViewModel viewModel;
    private final EditGroupTaskViewModel editModel;
    private final CreateGroupTasksViewModel createModel;

    private final ViewGroupTasksController viewController;
    private final EditGroupTaskController editController;
    private final CreateGroupTasksController createController;

    private final String groupId;

    private final JPanel tasksListPanel = new JPanel();
    private final JPanel topBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    /**
     * Constructs a GroupTasksView.
     *
     * @param viewModel        ViewModel for viewing tasks
     * @param editModel        ViewModel for editing tasks
     * @param createModel      ViewModel for creating tasks
     * @param viewController   Controller for view tasks use case
     * @param editController   Controller for edit tasks use case
     * @param createController Controller for create tasks use case
     * @param groupId          the groupId
     */
    public GroupTasksView(
            ViewGroupTasksViewModel viewModel,
            EditGroupTaskViewModel editModel,
            CreateGroupTasksViewModel createModel,
            ViewGroupTasksController viewController,
            EditGroupTaskController editController,
            CreateGroupTasksController createController, String groupId) {

        this.usernames = new ArrayList<>();
        this.viewModel = viewModel;
        this.editModel = editModel;
        this.createModel = createModel;
        this.groupId = groupId;

        this.viewController = viewController;
        this.editController = editController;
        this.createController = createController;

        this.viewModel.addPropertyChangeListener(this);
        this.editModel.addPropertyChangeListener(this);
        this.createModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        JButton createBtn = new JButton("Create Task");
        createBtn.addActionListener(event -> openCreateDialog(groupId));
        topBarPanel.add(createBtn);
        add(topBarPanel, BorderLayout.NORTH);

        tasksListPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tasksListPanel.setLayout(new BoxLayout(tasksListPanel, BoxLayout.Y_AXIS));
        tasksListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane scroll = new JScrollPane(tasksListPanel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scroll, BorderLayout.CENTER);

        refresh();

        // load tasks immediately
        viewController.execute(groupId);
    }

    /** Refreshes the UI according to the ViewModel state. */
    private void refresh() {
        tasksListPanel.removeAll();

        List<ViewGroupTasksOutputData.TaskDTO> tasks = viewModel.getState().getTasks();

        if (tasks == null || tasks.isEmpty()) {
            JLabel empty = new JLabel("No tasks yet.");
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            tasksListPanel.add(empty);
        } else {
            for (ViewGroupTasksOutputData.TaskDTO task : tasks) {
                tasksListPanel.add(makeTaskRow(task));
                tasksListPanel.add(Box.createVerticalStrut(6));
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

        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Task text
        String text = dto.getDescription();
        if (dto.getDueDateString() != null && !dto.getDueDateString().isEmpty()) {
            text += " (due " + dto.getDueDateString() + ")";
            if (dto.isCompleted()) {
                text += " (completed)";
            }
            else {
                text += " (not completed)";
            }
        }
        JLabel label = new JLabel(text);
        row.add(label);

        // Push button to right
        row.add(Box.createHorizontalGlue());

        // Small, neat edit button
        JButton editBtn = new JButton("Edit");
        editBtn.setPreferredSize(new Dimension(60, 25));
        editBtn.setMaximumSize(new Dimension(60, 25));
        editBtn.addActionListener(event -> openEditDialog(dto, groupId));
        row.add(editBtn);

        return row;
    }

    /**
     * Opens the dialog window for creating a new task.
     * The dialog is responsible for collecting user input and invoking
     * the CreateGroupTasksController.
     *
     * @param groupid the group id
     */
    private void openCreateDialog(String groupid) {
        new CreateTaskView(usernames, createController, createModel, groupid);
    }

    /**
     * Opens the dialog window for editing an existing task.
     *
     * @param dto     the task DTO to edit
     * @param groupid the group id
     */
    private void openEditDialog(ViewGroupTasksOutputData.TaskDTO dto, String groupid) {
        new EditTaskView(dto.getId(), usernames, editController, editModel, groupid, dto);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();

        if ("tasks".equals(prop)) {
            // Update member names
            usernames.clear();
            usernames.addAll(viewModel.getState().getMemberNames());
            // ViewGroupTasks presenter updated the tasks list.
            refresh();
        } else if ("edit_result".equals(prop) || "create_result".equals(prop)) {
            // After a successful or failed edit/create, reload tasks from the use case.
            viewController.execute(groupId);
        }
    }
}
