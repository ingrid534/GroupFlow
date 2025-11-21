package use_case.editgrouptasks;

import entity.task.Task;

/**
 * Data access interface for editing group tasks.
 */
public interface EditGroupTasksDataAccessInterface {

    /**
     * Returns whether the given user is a moderator in the given group.
     *
     * @param groupId the group id
     * @param userId the user id
     * @return whether user is moderator
     */
    boolean isModerator(String groupId, String userId);

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
