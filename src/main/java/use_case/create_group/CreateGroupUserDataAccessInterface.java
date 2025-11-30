package use_case.create_group;

import entity.user.User;

/**
 * DAO interface for a LoggedIn user in the CreateGroup use case.
 * This interface defines the contract for accessing data related to the currently logged-in user.
 */
public interface CreateGroupUserDataAccessInterface {

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
     * Sets the current username in use.
     *
     * @param name The username to be set
     */
    void setCurrentUsername(String name);

    /**
     * Saves the user.
     *
     * @param user the user to save
     */
    void save(User user);
}
