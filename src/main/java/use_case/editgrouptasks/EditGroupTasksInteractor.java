package use_case.editgrouptasks;

import entity.task.Task;

/**
 * Interactor for editing a specific task that belong to a specific group.
 */
public class EditGroupTasksInteractor implements EditGroupTasksInputBoundary {
    private final EditGroupTasksDataAccessInterface dataAccess;
    private final EditGroupTasksOutputBoundary presenter;

    /**
     * Constructs an interactor.
     *
     * @param dataAccess the data access object
     * @param presenter  the output boundary
     */
    public EditGroupTasksInteractor(EditGroupTasksDataAccessInterface dataAccess,
                                    EditGroupTasksOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(EditGroupTasksInputData inputData) {
        if (!dataAccess.isModerator(inputData.getGroupId(), inputData.getUserId())) {
            presenter.present(new EditGroupTasksOutputData(false,
                    "Only moderators can edit tasks."));
            return;
        }

        Task task = dataAccess.getTask(inputData.getTaskId());
        if (task == null) {
            presenter.present(new EditGroupTasksOutputData(false,
                    "Task not found."));
            return;
        }

        if (inputData.getNewDescription() != null) {
            task.setDescription(inputData.getNewDescription());
        }

        if (inputData.getNewDueDate() != null) {
            task.setDueDate(inputData.getNewDueDate());
        }

        if (inputData.getNewCompleted() != null) {
            if (inputData.getNewCompleted()) {
                task.markCompleted();
            }
            task.markIncomplete();
        }

        dataAccess.saveTask(task);
    }
}
