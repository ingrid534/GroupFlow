package use_case.viewtasks;

import entity.task.Task;
import java.util.List;

/**
 * Data access for retrieving task ids.
 */
public interface ViewTasksDataAccessInterface {

    List<String> getTasksForUser(String username);

    Task getTask(String taskId);
}
