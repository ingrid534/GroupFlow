package interface_adapter.creategrouptasks;

import use_case.creategrouptask.CreateGroupTaskInputBoundary;
import use_case.creategrouptask.CreateGroupTaskInputData;

import java.util.List;

/**
 * Controller for the CreateGroupTask use case.
 * Handles user input and forwards it to the interactor.
 */
public class CreateGroupTasksController {

    private final CreateGroupTaskInputBoundary interactor;

    /**
     * Constructs a CreateGroupTaskController.
     *
     * @param interactor the interactor for the use case
     */
    public CreateGroupTasksController(CreateGroupTaskInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Executes the CreateGroupTask use case.
     *
     * @param description Description of the task
     * @param dueDate     Due date string (nullable)
     * @param assigneeIds List of user IDs assigned to the task
     */
    public void execute(String description,
                        String dueDate,
                        List<String> assigneeIds) {

        CreateGroupTaskInputData inputData =
                new CreateGroupTaskInputData(description, dueDate, assigneeIds);

        interactor.execute(inputData);
    }
}
