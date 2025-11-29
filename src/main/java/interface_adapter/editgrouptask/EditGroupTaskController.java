package interface_adapter.editgrouptask;

import use_case.editgrouptasks.EditGroupTasksInputBoundary;
import use_case.editgrouptasks.EditGroupTasksInputData;

import java.util.List;

/**
 * Controller for the EditGroupTasks use case.
 * Receives user input from the view and forwards it to the interactor.
 */
public class EditGroupTaskController {

    private final EditGroupTasksInputBoundary interactor;

    /**
     * Constructs an EditGroupTasksController.
     *
     * @param interactor the edit use case interactor
     */
    public EditGroupTaskController(EditGroupTasksInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Executes the EditGroupTasks use case with the given parameters.
     *
     * @param taskId         ID of the task to edit
     * @param description    new description (nullable)
     * @param dueDate        new due date as string (nullable)
     * @param completed      new completed flag (nullable)
     * @param newAssigneeIds newAssigneeIds to be added to the task (nullable)
     * @param groupId        the group id
     */
    public void execute(String taskId, String description,
                        String dueDate, Boolean completed, List<String> newAssigneeIds, String groupId) {
        EditGroupTasksInputData inputData =
                new EditGroupTasksInputData(taskId,
                        description, dueDate, completed, newAssigneeIds, groupId);

        interactor.execute(inputData);
    }
}
