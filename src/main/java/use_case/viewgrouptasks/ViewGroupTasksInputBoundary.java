package use_case.viewgrouptasks;

/**
 * Input Boundary for viewing group tasks.
 */
public interface ViewGroupTasksInputBoundary {

    /**
     * Executes the view-group-tasks use case.
     *
     * @param inputData the input data containing the group id
     */
    void execute(ViewGroupTasksInputData inputData);
}

