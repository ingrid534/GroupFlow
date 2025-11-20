package interface_adapter.viewtasks;

import interface_adapter.ViewModel;
import use_case.viewtasks.ViewTasksOutputData;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel storing task display data for the Swing UI.
 */
public class ViewTasksViewModel extends ViewModel<LoggedInState> {
    private List<ViewTasksOutputData.TaskDTO> tasks = new ArrayList<>();
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public ViewTasksViewModel() {
        super("tasks");
        setState(new LoggedInState());
    }

    /**
     * Updates the list of tasks stored in this ViewModel and notifies all
     * registered property change listeners that the task data has changed.
     *
     * @param tasks the new list of task data transfer objects to display;
     *              must not be {@code null}
     */
    public void setTasks(List<ViewTasksOutputData.TaskDTO> tasks) {
        this.tasks = tasks;
        this.support.firePropertyChange("tasks", null, this.tasks);
    }

    public List<ViewTasksOutputData.TaskDTO> getTasks() {
        return tasks;
    }

}
