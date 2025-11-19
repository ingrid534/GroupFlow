package use_case.viewtasks;

/**
 * Input data containing the user requesting to view tasks.
 */
public class ViewTasksInputData {
    private final String userId;

    public ViewTasksInputData(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
