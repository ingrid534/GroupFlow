package interface_adapter.viewtasks;

import use_case.viewtasks.ViewTasksInputBoundary;
import use_case.viewtasks.ViewTasksInputData;

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
     * @param username the username of the user
     */
    public void execute(String username) {
        ViewTasksInputData inputData = new ViewTasksInputData(username);
        interactor.execute(inputData);
    }
}
