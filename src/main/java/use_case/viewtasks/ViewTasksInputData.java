package use_case.viewtasks;

/**
 * Input data containing the user requesting to view tasks.
 */
public class ViewTasksInputData {
    private final String username;

    public ViewTasksInputData(String username) {
        this.username = username;
    }

    public String getUserName() {
        return username;
    }
}
