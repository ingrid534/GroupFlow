package use_case.create_schedule;

/**
 * Output boundary interface for the Create Schedule use case.
 */
public interface CreateScheduleOutputBoundary {
    
    /**
     * Prepares the success view for the Create Schedule use case.
     * This method is called when the schedule creation is successful and is responsible
     * for presenting the successful output to the user.
     *
     * @param outputData the output data containing the updated schedule
     */
    void prepareSuccessView(CreateScheduleOutputData outputData);

    /**
     * Prepares the failure view for the Create Schedule use case.
     * This method is called when the schedule creation fails and is responsible
     * for presenting the error message to the user.
     *
     * @param errorMessage the error message describing the reason for the failure
     */
    void prepareFailView(String errorMessage);

    /**
     * Opens the Create Schedule modal.
     * This method triggers the display of the modal for creating a group.
     */
    void openCreateScheduleModal();
}
