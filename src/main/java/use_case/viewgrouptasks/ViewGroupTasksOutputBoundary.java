package use_case.viewgrouptasks;

/**
 * Output boundary for the group-tasks use case.
 */
public interface ViewGroupTasksOutputBoundary {

    /**
     * Presents the list of tasks for a group.
     *
     * @param outputData the task list data
     */
    void present(ViewGroupTasksOutputData outputData);
}
