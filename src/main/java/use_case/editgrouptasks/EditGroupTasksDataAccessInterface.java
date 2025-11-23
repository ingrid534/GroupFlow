package use_case.editgrouptasks;

import entity.task.Task;
import entity.user.User;

import java.util.List;

/**
 * Data access interface for editing group tasks.
 */
public interface EditGroupTasksDataAccessInterface {

    /**
     * Returns whether the current logged in user is a moderator in the currentGroup.
     *
     * @return whether user is moderator
     */
    boolean isModerator();

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
     * Returns the User object with the username.
     *
     * @param username the username
     * @return the User object
     */
    User getUserFromUsername(String username);

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
}
