package use_case.viewtasks;

/**
 * Output boundary for the ViewTask use case.
 */
public interface ViewTasksOutputBoundary {

    /**
     * Present success view for the ViewTask Use Case.
     * @param outputData the output data
     */
    void presentTasks(ViewTasksOutputData outputData);
}
