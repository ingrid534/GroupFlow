package use_case.create_meeting;

import entity.user.User;

/**
 * Data access interface for user information when creating meeting tasks.
 */
public interface CreateMeetingUserDataAccessInterface {

    /**
     * Gets the currently logged-in username.
     *
     * @return the username
     */
    String getCurrentUsername();

    /**
     * Retrieves a user by username.
     *
     * @param username the username
     * @return the user or null if not found
     */
    User get(String username);

    /**
     * Saves the updated user.
     *
     * @param user the user to save
     */
    void save(User user);
}