package use_case.create_schedule;

import entity.user.User;

public interface CreateScheduleUserDataAccessInterface {

    /**
     * Get user by username.
     * @param username the username
     * @return the user with the given username
     */
    User get(String username);

    /**
     * Save the schedule to memory.
     * @param user the user to save the new schedule for
     */
    void saveSchedule(User user);

    /**
     * Get the username of the current user.
     * @return the username of current user.
     */
    String getCurrentUsername();
}
