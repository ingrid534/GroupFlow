package interface_adapter.editgrouptask;

/**
 * State class for the EditGroupTasksViewModel.
 * Stores the outcome of an edit operation for display in the view.
 */
public class EditGroupTaskState {

    private boolean success;
    private String message;

    /**
     * Returns whether the edit operation succeeded.
     *
     * @return true if edit was successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets whether the edit operation succeeded.
     *
     * @param success success flag
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Returns the result message of the edit operation.
     *
     * @return message describing success or failure
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the result message of the edit operation.
     *
     * @param message result message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
