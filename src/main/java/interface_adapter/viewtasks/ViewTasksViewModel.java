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

    public void firePropertyChange(List<ViewTasksOutputData.TaskDTO> tasks) {
        this.tasks = tasks;
        this.support.firePropertyChange("tasks", null, this.tasks);
    }

    public List<ViewTasksOutputData.TaskDTO> getTasks() {
        return tasks;
    }

}
