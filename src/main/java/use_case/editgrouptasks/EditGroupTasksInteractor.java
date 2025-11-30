package use_case.editgrouptasks;

import entity.membership.Membership;
import entity.task.Task;
import entity.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Interactor for editing a specific task that belong to a specific group.
 */
public class EditGroupTasksInteractor implements EditGroupTasksInputBoundary {
    private final EditGroupTasksDataAccessInterface dataAccess;
    private final EditGroupTasksUserDataAccessInterface userDataAccess;
    private final EditGroupTasksOutputBoundary presenter;
    private final EditGroupTasksMembershipDataAccessInterface membershipDataAccess;

    /**
     * Constructs an interactor.
     *
     * @param dataAccess                    the data access object
     * @param presenter                     the output boundary
     * @param userDataAccess                the user data access object
     * @param membershipDataAccessInterface the mamber data access object
     */
    public EditGroupTasksInteractor(EditGroupTasksDataAccessInterface dataAccess,
                                    EditGroupTasksOutputBoundary presenter,
                                    EditGroupTasksUserDataAccessInterface userDataAccess,
                                    EditGroupTasksMembershipDataAccessInterface membershipDataAccessInterface) {
        this.dataAccess = dataAccess;
        this.userDataAccess = userDataAccess;
        this.presenter = presenter;
        this.membershipDataAccess = membershipDataAccessInterface;
    }

    @Override
    public void execute(EditGroupTasksInputData inputData) {
        if (!validateMembership(inputData)) {
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

        updateDescription(task, inputData);

        if (!updateDueDate(task, inputData)) {
            return;
        }

        if (inputData.getNewCompleted() != null) {
            if (inputData.getNewCompleted()) {
                task.markCompleted();
            }
            else {
                task.markIncomplete();
            }
        }

        List<String> newUsernames = inputData.getNewAssigneeUsernames();

        List<String> oldAssignees = new ArrayList<>(task.getAssignees());

        task.setAssignees(newUsernames);

        if (newUsernames != null) {

            for (String oldId : oldAssignees) {
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

            dataAccess.upsertTask(task);
        }

        presenter.present(new EditGroupTasksOutputData(true, "Task updated successfully."));
    }

    private boolean validateMembership(EditGroupTasksInputData inputData) {
        Membership membership = membershipDataAccess.get(
                userDataAccess.getCurrentUsername(),
                inputData.getGroupId());

        if (membership != null && !membership.isModerator()) {
            presenter.present(new EditGroupTasksOutputData(false,
                    "Only moderators can edit tasks."));
            return false;
        }
        return true;
    }

    private void updateDescription(Task task, EditGroupTasksInputData inputData) {
        if (inputData.getNewDescription() != null) {
            task.setDescription(inputData.getNewDescription());
        }
    }

    private boolean updateDueDate(Task task, EditGroupTasksInputData inputData) {
        String newDue = inputData.getNewDueDate();
        if (newDue != null && !newDue.isEmpty()) {
            try {
                LocalDateTime date = LocalDateTime.parse(
                        newDue,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                );
                task.setDueDate(date);
            } catch (DateTimeParseException exception) {
                presenter.present(new EditGroupTasksOutputData(false, "Invalid date."));
                return false;
            }
        }
        return true;
    }
}
