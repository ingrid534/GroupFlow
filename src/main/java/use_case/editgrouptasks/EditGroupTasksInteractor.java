package use_case.editgrouptasks;

import entity.task.Task;
import entity.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

        if (inputData.getNewDueDate() != null && !inputData.getNewDueDate().isEmpty()) {
            LocalDateTime newDue = LocalDate.parse(inputData.getNewDueDate()).atStartOfDay();
            task.setDueDate(newDue);
        }

        if (inputData.getNewCompleted() != null) {
            if (inputData.getNewCompleted()) {
                task.markCompleted();
            }
            task.markIncomplete();
        }

        List<String> newIds = inputData.getNewAssigneeUserIds();
        if (newIds != null) {

            for (String oldId : task.getAssignees()) {
                User u = dataAccess.getUser(oldId);
                if (u != null) {
                    u.getTasks().remove(task.getID());
                    dataAccess.saveUser(u);
                }
            }

            for (String newId : newIds) {
                User u = dataAccess.getUser(newId);
                if (u != null) {
                    u.getTasks().add(task.getID());
                    dataAccess.saveUser(u);
                }
            }

            dataAccess.saveTask(task);
        }
    }
}
