package interface_adapter.viewgrouptasks;

import use_case.viewgrouptasks.ViewGroupTasksOutputData;

import java.util.ArrayList;
import java.util.List;

/**
 * State class for ViewGroupTasksViewModel.
 * Stores the list of tasks belonging to a specific group.
 */
public class ViewGroupTasksState {

    private List<String> memberNames = new ArrayList<>();
    private List<ViewGroupTasksOutputData.TaskDTO> tasks = new ArrayList<>();
    private String error;

    /**
     * Sets the list of membernames.
     *
     * @param names list of member names
     */
    public void setMemberNames(List<String> names) {
        this.memberNames = names;
    }

    /**
     * Returns the list of member names for the current group.
     *
     * @return list of member names
     */
    public List<String> getMemberNames() {
        return memberNames;
    }

    /**
     * Returns the list of tasks for the current group.
     *
     * @return list of task DTOs
     */
    public List<ViewGroupTasksOutputData.TaskDTO> getTasks() {
        return tasks;
    }

    /**
     * Sets the list of tasks.
     *
     * @param tasks list of task DTOs
     */
    public void setTasks(List<ViewGroupTasksOutputData.TaskDTO> tasks) {
        this.tasks = tasks;
    }

    /**
     * Sets an error message for the view.
     *
     * @param error error message
     */
    public void setError(String error) {
        this.error = error;
    }
}
