package use_case.view_meeting;

/**
 * Output boundary for the ViewMeetings use case.
 */
public interface ViewMeetingsOutputBoundary {
    /**
     * Presents the meetings data.
     * @param outputData the meetings output data
     */
    void present(ViewMeetingsOutputData outputData);
}