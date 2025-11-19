package view;

import interface_adapter.viewtasks.LoggedInState;
import interface_adapter.viewtasks.ViewTasksViewModel;
import interface_adapter.viewtasks.ViewTasksController;
import use_case.viewtasks.ViewTasksOutputData;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class ViewTasksView extends JPanel implements PropertyChangeListener {

    private final ViewTasksViewModel viewModel;
    private ViewTasksController viewTasksController;

    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> taskList = new JList<>(listModel);

    public ViewTasksView(ViewTasksViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        taskList.setVisibleRowCount(10); // just a hint to layout manager
        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(scrollPane, BorderLayout.CENTER);

        LoggedInState currentState = viewModel.getState();
        viewTasksController.execute(currentState.getUsername());

        // Optional: initial render if there are already tasks in the view model
        refreshList();
    }

    public void setController(ViewTasksController viewTasksController) {
        this.viewTasksController = viewTasksController;
    }

    private void refreshList() {
        listModel.clear();
        for (ViewTasksOutputData.TaskDTO dto : viewModel.getTasks()) {
            String text;
            if (dto.dueDateString == null || dto.dueDateString.isEmpty()) {
                text = dto.description;
            } else {
                text = dto.description + " (due " + dto.dueDateString + ")";
            }
            listModel.addElement(text);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("tasks".equals(evt.getPropertyName())) {
            refreshList();
        }
    }
}
