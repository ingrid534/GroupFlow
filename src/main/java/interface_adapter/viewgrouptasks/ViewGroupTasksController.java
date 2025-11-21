package interface_adapter.viewgrouptasks;

import use_case.viewgrouptasks.ViewGroupTasksInputBoundary;
import use_case.viewgrouptasks.ViewGroupTasksInputData;

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
     *
     * @param groupId the ID of the group whose tasks should be retrieved
     */
    public void execute(String groupId) {
        ViewGroupTasksInputData inputData = new ViewGroupTasksInputData(groupId);
        interactor.execute(inputData);
    }
}
