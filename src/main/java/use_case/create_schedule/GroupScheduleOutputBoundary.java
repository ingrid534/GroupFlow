package use_case.create_schedule;

public interface GroupScheduleOutputBoundary {
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
}
