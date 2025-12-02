package use_case.create_schedule;

/**
 * Output boundary interface for viewing the group schedule.
 * This handles displaying the aggregated schedule after individual schedules are created.
 */
public interface ViewScheduleOutputBoundary {
    
    /**
     * Prepares the success view for displaying the group schedule.
     * This method is called when the schedule data is successfully retrieved
     * and is responsible for presenting it to the user.
     *
     * @param outputData the output data containing the updated schedule
     */
    void prepareSuccessView(CreateScheduleOutputData outputData);

    /**
     * Prepares the failure view for the group schedule.
     * This method is called when retrieving the schedule fails and is responsible
     * for presenting the error message to the user.
     *
     * @param errorMessage the error message describing the reason for the failure
     */
    void prepareFailView(String errorMessage);
}
