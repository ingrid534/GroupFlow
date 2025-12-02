package use_case.create_meeting;

/**
 * Output data for the result of creating a new task in a meeting.
 */
public class CreateMeetingOutputData {
    private final boolean success;
    private final String message;

    public CreateMeetingOutputData(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}