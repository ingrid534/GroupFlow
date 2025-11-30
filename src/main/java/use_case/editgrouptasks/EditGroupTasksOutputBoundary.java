package use_case.editgrouptasks;

public interface EditGroupTasksOutputBoundary {

    /**
     * Presents the result of editing a task.
     *
     * @param outputData the result message and success flag
     */
    void present(EditGroupTasksOutputData outputData);
}
