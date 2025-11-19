package use_case.create_group;

import entity.user.User;

/**
 * DAO interface for a LoggedIn user in the CreateGroup use case.
 */
public interface LoggedInDataAccessInterface {

    /**
     * Checks if the given username exists.
     *
     * @param username the username to look for
     * @return true if a user with the given username exists; false otherwise
     */
    boolean existsByName(String username);

    /**
     * Returns the user with the given username.
     *
     * @param username the username to look up
     * @return the user with the given username
     */
    User get(String username);

    String getCurrentUsername();
}
