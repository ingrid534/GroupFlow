package interface_adapter.viewtasks;

import use_case.viewtasks.ViewTasksOutputData;

import java.util.ArrayList;
import java.util.List;

/**
 * The State information representing the logged-in user.
 */
public class ViewTasksState {

    private List<ViewTasksOutputData.TaskDTO> tasks = new ArrayList<>();

    public void setTasks(List<ViewTasksOutputData.TaskDTO> tasks) {
        this.tasks = tasks;
    }

    public List<ViewTasksOutputData.TaskDTO> getTasks() {
        return tasks;
    }
}

