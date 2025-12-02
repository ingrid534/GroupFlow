package use_case.create_meeting;

/**
 * Input boundary for the "Create Meeting Task" use case.
 * This defines the request model accepted by the interactor.
 */
public interface CreateMeetingInputBoundary {

    /**
     * Executes the use case to create a task in a meeting.
     *
     * @param inputData the data needed to create the task
     */
    void execute(CreateMeetingInputData inputData);
}