package use_case.editgrouptasks;

import entity.task.Task;

/**
 * Data access interface for editing group tasks.
 */
public interface EditGroupTasksDataAccessInterface {

    /**
     * Returns the Task object with the taskId.
     *
     * @param taskId the taskId
     * @return the Task object
     */
    Task getTask(String taskId);

    /**
     * Saves the task with updated info (not sure if needed).
     *
     * @param task task to be saved
     */
    void saveTask(Task task);
}
