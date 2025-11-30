package use_case.editgrouptasks;

/**
 * Input boundary for editing a group task.
 */
public interface EditGroupTasksInputBoundary {
    /**
     * Executes the use case to update fields of a task.
     *
     * @param inputData the data needed to update the task
     */
    void execute(EditGroupTasksInputData inputData);
}
