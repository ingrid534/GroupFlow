package use_case.creategrouptask;

/**
 * Input boundary for the "Create Group Task" use case.
 * This defines the request model accepted by the interactor.
 */
public interface CreateGroupTaskInputBoundary {
    /**
     * Executes the use case to creat a task.
     *
     * @param inputData the data needed to create the task
     */
    void execute(CreateGroupTaskInputData inputData);
}
