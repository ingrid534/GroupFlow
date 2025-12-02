package use_case.view_meeting;

/**
 * Input boundary for the ViewMeetings use case.
 */
public interface ViewMeetingsInputBoundary {
    /**
     * Executes the view meetings use case.
     * @param inputData the input data containing groupId
     */
    void execute(ViewMeetingsInputData inputData);
}