package use_case.viewtasks;

/**
 * Input boundary (controller → use case) for viewing a user's tasks.
 */
public interface ViewTasksInputBoundary {

    /**
     * Executes the use case to fetch tasks for a user.
     */
    void execute();
}
