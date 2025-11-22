package interface_adapter.viewgrouptasks;

import use_case.viewgrouptasks.ViewGroupTasksInputBoundary;

/**
 * Controller for the ViewGroupTasks use case.
 * Allows the UI to request the list of tasks for a specific group.
 */
public class ViewGroupTasksController {

    private final ViewGroupTasksInputBoundary interactor;

    /**
     * Constructs a ViewGroupTasksController.
     *
     * @param interactor the input boundary for the use case
     */
    public ViewGroupTasksController(ViewGroupTasksInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Executes the ViewGroupTasks use case.
     */
    public void execute() {
        interactor.execute();
    }
}
