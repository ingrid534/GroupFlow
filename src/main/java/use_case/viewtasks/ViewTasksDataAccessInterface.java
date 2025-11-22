package use_case.viewtasks;

import entity.group.Group;
import entity.task.Task;
import java.util.List;

/**
 * Data access for retrieving task ids.
 */
public interface ViewTasksDataAccessInterface {

    /**
     * Returns a list of task IDs that are assigned to the specified user.
     *
     * @return a list of task IDs associated with the currently logged in user
     */
    List<String> GetTasksForCurrentUser();

    /**
     * Retrieves the full {@link Task} entity corresponding to the given task ID.
     *
     * @param taskId the unique identifier of the task to retrieve
     * @return the {@link Task} object associated with the ID, or {@code null}
     *         if no task with the given ID exists
     */
    Task getTask(String taskId);

    void saveGroup(Group group);
}
