package use_case.creategrouptask;

import entity.user.User;

public interface CreateGroupTaskUserDataAccessInterface {

    /**
     * Returns the User object with the username.
     *
     * @param username the username
     * @return the User object
     */
    User get(String username);

    /**
     * Return the current user.
     *
     * @return the User object
     */
    String getCurrentUsername();

    /**
     * Saves the user with updated info (not sure if needed).
     *
     * @param user user to be saved
     */
    void save(User user);
}
