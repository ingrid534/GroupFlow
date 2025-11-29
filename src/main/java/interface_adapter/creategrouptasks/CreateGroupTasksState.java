package interface_adapter.creategrouptasks;

/**
 * State class used by the CreateGroupTaskViewModel.
 * Stores the outcome of a task creation operation.
 */
public class CreateGroupTasksState {

    private boolean success;
    private String message;

    /**
     * Returns whether task creation succeeded.
     *
     * @return true if creation succeeded, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets whether the task creation succeeded.
     *
     * @param success success flag
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Returns the message for the creation result.
     *
     * @return message describing the outcome
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the result message for the creation operation.
     *
     * @param message result message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
