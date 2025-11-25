package use_case.creategrouptask;

import entity.user.User;

public interface CreateGroupTaskUserDataAccessInterface {
    /**
     * Returns whether the current logged in user is a moderator in the currentGroup.
     *
     * @return whether user is moderator
     */
    boolean isModerator();

    /**
     * Returns the User object with the username.
     *
     * @param username the username
     * @return the User object
     */
    User get(String username);

    /**
     * Saves the user with updated info (not sure if needed).
     *
     * @param user user to be saved
     */
    void save(User user);
}
