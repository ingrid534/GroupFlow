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
     * @return a list of task IDs associated with the currently logged in user
     */
    List<Task> getTasksForCurrentUser();
}
