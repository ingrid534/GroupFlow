package use_case.creategrouptask;

/**
 * Output boundary for presenting the result of creating a group task.
 */
public interface CreateGroupTaskOutputBoundary {

    /**
     * Presents the result of creating a task.
     *
     * @param outputData the output data
     */
    void present(CreateGroupTaskOutputData outputData);
}
