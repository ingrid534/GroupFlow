package use_case.editgrouptasks;

import entity.task.Task;
import entity.user.User;

import java.util.List;

/**
 * Data access interface for editing group tasks.
 */
public interface EditGroupTasksDataAccessInterface {

    /**
     * Returns whether the current logged in user is a moderator in the given group.
     *
     * @param groupId the group id
     * @return whether user is moderator
     */
    boolean isModerator(String groupId);

    /**
     * Returns the Task object with the taskId.
     *
     * @param taskId the taskId
     * @return the Task object
     */
    Task getTask(String taskId);

    /**
     * Returns the User object with the userId.
     *
     * @param userId the userId
     * @return the User object
     */
    User getUser(String userId);

    /**
     * Saves the task with updated info (not sure if needed).
     *
     * @param task task to be saved
     */
    void saveTask(Task task);

    /**
     * Saves the user with updated info (not sure if needed).
     *
     * @param user user to be saved
     */
    void saveUser(User user);

    /**
     * Get the list of usernames of the user of the group.
     *
     * @param groupId the groupId of the group
     */
    List<String> getMemberNames(String groupId);
}
