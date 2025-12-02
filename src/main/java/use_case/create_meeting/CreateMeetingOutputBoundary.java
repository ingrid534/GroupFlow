package use_case.create_meeting;

/**
 * Output boundary for presenting the result of creating a meeting task.
 */
public interface CreateMeetingOutputBoundary {

    /**
     * Presents the result of creating a task.
     *
     * @param outputData the output data
     */
    void present(CreateMeetingOutputData outputData);
}