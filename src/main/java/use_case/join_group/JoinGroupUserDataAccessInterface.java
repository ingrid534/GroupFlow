package use_case.join_group;

import entity.user.User;

public interface JoinGroupUserDataAccessInterface {
    /**
     * Returns the user with the given username.
     *
     * @param username the username to look up
     * @return the user with the given username
     */
    User get(String username);

    /**
     * Retrieves the username of the currently logged-in user.
     *
     * @return the username of the currently logged-in user
     */
    String getCurrentUsername();

    /**
     * Saves the user.
     *
     * @param user the user to save
     */
    void save(User user);

    /**
     * Sets the current username in use.
     *
     * @param name The username to be set
     */
    void setCurrentUsername(String name);
}
