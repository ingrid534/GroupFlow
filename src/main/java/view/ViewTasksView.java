package view;

import interface_adapter.viewtasks.LoggedInState;
import interface_adapter.viewtasks.ViewTasksViewModel;
import interface_adapter.viewtasks.ViewTasksController;
import use_case.viewtasks.ViewTasksOutputData;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class ViewTasksView extends JPanel implements PropertyChangeListener {

    private final ViewTasksViewModel viewModel;
    private ViewTasksController viewTasksController;

    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> taskList = new JList<>(listModel);

    public ViewTasksView(ViewTasksViewModel viewModel, ViewTasksController viewTasksController) {
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);
        this.viewTasksController = viewTasksController;

        setLayout(new BorderLayout());

        // just a hint to layout manager
        taskList.setVisibleRowCount(10);
        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setPreferredSize(new Dimension(250, 400));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(scrollPane, BorderLayout.CENTER);

        // Optional: initial render if there are already tasks in the view model
        refreshList();
    }

    private void refreshList() {
        listModel.clear();
        List<ViewTasksOutputData.TaskDTO> tasks = viewModel.getState().getTasks();
        if (tasks.isEmpty()) {
            listModel.addElement("All done!");
            return;
        }

        for (ViewTasksOutputData.TaskDTO dto : tasks) {
            String text;
            if (dto.getDueDateString() == null || dto.getDueDateString().isEmpty()) {
                text = dto.getDescription();
            } else {
                text = dto.getDescription() + " (due " + dto.getDueDateString() + ")";
            }
            listModel.addElement(text);
        }
    }

    /**
     * Responds to property change events fired by the {@link ViewTasksViewModel}.
     *
     * @param evt the property change event fired by the ViewModel
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            LoggedInState state = (LoggedInState) evt.getNewValue();
            viewTasksController.execute(state.getUsername());
        }
        if ("tasks".equals(evt.getPropertyName())) {
            refreshList();
        }
    }
}
