package use_case.create_schedule;

public interface CreateScheduleInputBoundary {

    /**
     * Executes the Create Schedule use case.
     * Processes the input data necessary for creating a schedule.
     * @param createScheduleInputData the input data with this user's schedule
     */
    void execute(CreateScheduleInputData createScheduleInputData);

    /**
     * Opens the modal for user to create a schedule.
     */
    void openCreateScheduleModal();

    /**
     * Loads the existing schedule for a group.
     * @param groupID the group ID to load the schedule for
     */
    void loadSchedule(String groupID);
}
