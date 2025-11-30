package use_case.creategrouptask;

import entity.group.Group;
import entity.membership.Membership;
import entity.task.Task;
import entity.task.TaskFactory;
import entity.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        Membership membership = membershipDataAccess.get(
                userDataAccess.getCurrentUsername(),
                inputData.getGroupId());
        if (membership != null && !membership.isModerator()) {
            presenter.present(new CreateGroupTaskOutputData(false,
                    "Only moderators may create tasks in this group."));
            return;
        }

        Group group = groupDataAccess.getGroup(inputData.getGroupId());
        if (group == null) {
            presenter.present(new CreateGroupTaskOutputData(false,
                    "Group not found."));
            return;
        }

        Task task;

        List<String> assignees = inputData.getAssignees();

        if (inputData.getDueDate() != null && !inputData.getDueDate().isEmpty()) {
            LocalDateTime due = LocalDate.parse(inputData.getDueDate()).atStartOfDay();
            task = taskFactory.createWithDeadline("", inputData.getDescription(), inputData.getGroupId(),
                    false, assignees, due);
        } else {
            task = taskFactory.createWithoutDeadline("", inputData.getDescription(), inputData.getGroupId(),
                    false, assignees);
        }

        // save task to get the mongo generated ID
        dataAccess.upsertTask(task);

        // 4. Optional: Assign users to the task
        if (assignees != null) {
            for (String username : assignees) {
                User u = userDataAccess.get(username);
                if (u != null) {
                    u.getTasks().add(task.getID());
                    userDataAccess.save(u);
                }
            }
        }

        group.addTask(task.getID());

        groupDataAccess.save(group);

        presenter.present(new CreateGroupTaskOutputData(true,
                "Task created successfully."));
    }

}
