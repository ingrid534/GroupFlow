package use_case.creategrouptask;

import entity.group.Group;
import entity.membership.Membership;
import entity.task.Task;
import entity.task.TaskFactory;
import entity.user.User;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Interactor for creating a new task inside a group.
 * Enforces moderator permissions.
 */
public class CreateGroupTaskInteractor implements CreateGroupTaskInputBoundary {

    private final CreateGroupTaskDataAccessInterface dataAccess;
    private final CreateGroupTaskUserDataAccessInterface userDataAccess;
    private final CreateGroupTaskGroupDataAccessInterface groupDataAccess;
    private final CreateGroupTaskOutputBoundary presenter;
    private final TaskFactory taskFactory;
    private final CreateGroupTasksMembershipDataAccessInterface membershipDataAccess;

    /**
     * Constructs a CreateGroupTaskInteractor.
     *
     * @param dataAccess                    Data access interface for task/group persistence
     * @param presenter                     Output boundary for presenting results
     * @param taskFactory                   Factory for creating Task entities
     * @param userDataAccess                Data access interface for user data
     * @param groupDataAccess               Data access interface for group data
     * @param membershipDataAccessInterface Data access interface for member data
     */
    public CreateGroupTaskInteractor(CreateGroupTaskDataAccessInterface dataAccess,
                                     CreateGroupTaskOutputBoundary presenter, TaskFactory taskFactory,
                                     CreateGroupTaskUserDataAccessInterface userDataAccess,
                                     CreateGroupTaskGroupDataAccessInterface groupDataAccess,
                                     CreateGroupTasksMembershipDataAccessInterface membershipDataAccessInterface) {
        this.dataAccess = dataAccess;
        this.userDataAccess = userDataAccess;
        this.groupDataAccess = groupDataAccess;
        this.membershipDataAccess = membershipDataAccessInterface;
        this.presenter = presenter;
        this.taskFactory = taskFactory;
    }

    @Override
    public void execute(CreateGroupTaskInputData inputData) {
        if (validateMembership(inputData)) {
            presenter.present(new CreateGroupTaskOutputData(false,
                    "Only moderators may create tasks in this group."));
            return;
        }
        if (inputData.getDescription() == null || "".equals(inputData.getDescription())) {
            presenter.present(new CreateGroupTaskOutputData(false,
                    "Description cannot be empty."));
            return;
        }
        Task task;
        List<String> assignees = inputData.getAssignees();
        task = createTask(inputData, assignees);
        if (task == null) {
            return;
        }
        dataAccess.upsertTask(task);
        updateAssignees(assignees, task);
        Group group = groupDataAccess.getGroup(inputData.getGroupId());
        group.addTask(task.getID());
        groupDataAccess.save(group);
        presenter.present(new CreateGroupTaskOutputData(true,
                "Task created successfully."));
    }

    @Nullable
    private Task createTask(CreateGroupTaskInputData inputData, List<String> assignees) {
        Task task;
        if (inputData.getDueDate() != null && !inputData.getDueDate().isEmpty()) {
            try {
                LocalDateTime due = LocalDateTime.parse(inputData.getDueDate(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                );
                task = taskFactory.createWithDeadline("", inputData.getDescription(), inputData.getGroupId(),
                        false, assignees, due);
            } catch (DateTimeParseException exception) {
                presenter.present(new CreateGroupTaskOutputData(false, "Invalid date."));
                return null;
            }
        } else {
            task = taskFactory.createWithoutDeadline("", inputData.getDescription(), inputData.getGroupId(),
                    false, assignees);
        }
        return task;
    }

    private void updateAssignees(List<String> assignees, Task task) {
        if (assignees != null) {
            for (String username : assignees) {
                User u = userDataAccess.get(username);
                if (u != null) {
                    u.getTasks().add(task.getID());
                    userDataAccess.save(u);
                }
            }
        }
    }

    private boolean validateMembership(CreateGroupTaskInputData inputData) {
        Membership membership = membershipDataAccess.get(
                userDataAccess.getCurrentUsername(),
                inputData.getGroupId());
        return membership != null && !membership.isModerator();
    }

}
