package interface_adapter.viewtasks;

import use_case.viewtasks.ViewTasksInputBoundary;

/**
 * Controller that the Dashboard/Homepage will call to trigger the use case.
 */
public class ViewTasksController {
    private final ViewTasksInputBoundary interactor;

    public ViewTasksController(ViewTasksInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Executes the ViewTask Use Case.
     *
     */
    public void execute() {
        interactor.execute();
    }
}
