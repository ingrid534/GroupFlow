package use_case.viewtasks;

import entity.task.Task;
import java.util.List;

/**
 * Data access for retrieving task ids.
 */
public interface ViewTasksDataAccessInterface {

    /**
     * Returns a list of task IDs that are assigned to the specified user.
     *
     * @param username the unique identifier of the user whose tasks
     *                 should be retrieved
     * @return a list of task IDs associated with the user; never {@code null},
     *         but may be empty if the user has no assigned tasks
     */
    List<String> getTasksForUser(String username);

    /**
     * Retrieves the full {@link Task} entity corresponding to the given task ID.
     *
     * @param taskId the unique identifier of the task to retrieve
     * @return the {@link Task} object associated with the ID, or {@code null}
     *         if no task with the given ID exists
     */
    Task getTask(String taskId);
}
