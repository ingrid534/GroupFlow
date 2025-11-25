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
    private final EditGroupTasksUserDataAccessInterface userDataAccess;
    private final EditGroupTasksOutputBoundary presenter;

    /**
     * Constructs an interactor.
     *
     * @param dataAccess     the data access object
     * @param presenter      the output boundary
     * @param userDataAccess the user data access object
     */
    public EditGroupTasksInteractor(EditGroupTasksDataAccessInterface dataAccess,
                                    EditGroupTasksOutputBoundary presenter,
                                    EditGroupTasksUserDataAccessInterface userDataAccess) {
        this.dataAccess = dataAccess;
        this.userDataAccess = userDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(EditGroupTasksInputData inputData) {
        if (!userDataAccess.isModerator()) {
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

        List<String> newUsernames = inputData.getNewAssigneeUsernames();
        if (newUsernames != null) {

            for (String oldId : task.getAssignees()) {
                User u = userDataAccess.get(oldId);
                if (u != null) {
                    u.getTasks().remove(task.getID());
                    userDataAccess.save(u);
                }
            }

            for (String username : newUsernames) {
                User u = userDataAccess.get(username);
                if (u != null) {
                    u.getTasks().add(task.getID());
                    userDataAccess.save(u);
                }
            }

            dataAccess.saveTask(task);
        }
    }
}
