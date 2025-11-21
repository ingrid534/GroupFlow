package use_case.viewgrouptasks;

import entity.task.Task;
import java.util.List;

/**
 * Data access interface for retrieving and modifying tasks that are associated
 * with a particular group, as well as related user and group information.
 */
public interface ViewGroupTasksDataAccessInterface {

    /**
     * Returns all tasks that belong to the given group.
     *
     * @param groupId the group id
     * @return the list of tasks for that group (never {@code null})
     */
    List<Task> getTasksForGroup(String groupId);
}
