package use_case.editgrouptasks;

/**
 * Output data for the result of editing a group task.
 */
public class EditGroupTasksOutputData {

    private final boolean success;
    private final String message;

    /**
     * Creates new output data.
     *
     * @param success whether the edit succeeded
     * @param message a user-facing message
     */
    public EditGroupTasksOutputData(boolean success, String message) {
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

