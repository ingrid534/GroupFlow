package use_case.creategrouptask;

import entity.group.Group;
import entity.task.Task;
import entity.user.User;

public interface CreateGroupTaskDataAccessInterface {
    /**
     * Returns whether the given user is a moderator in the given group.
     *
     * @param groupId the group id
     * @param userId the user id
     * @return whether user is moderator
     */
    boolean isModerator(String groupId, String userId);

    /**
     * Returns the Group object with the groupId.
     *
     * @param groupId the groupId
     * @return the Group object
     */
    Group getGroup(String groupId);

    /**
     * Returns the User object with the userId.
     *
     * @param userId the userId
     * @return the User object
     */
    User getUser(String userId);

    /**
     * Saves the group with updated info (not sure if needed).
     *
     * @param group group to be saved
     */
    void saveGroup(Group group);

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
