package use_case.creategrouptask;

/**
 * Output data for the result of creating a new group task.
 */
public class CreateGroupTaskOutputData {
    private final boolean success;
    private final String message;

    public CreateGroupTaskOutputData(boolean success, String message) {
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
