package use_case.creategrouptask;

import entity.group.Group;
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

    /**
     * Constructs a CreateGroupTaskInteractor.
     *
     * @param dataAccess      Data access interface for task/group persistence
     * @param presenter       Output boundary for presenting results
     * @param taskFactory     Factory for creating Task entities
     * @param userDataAccess  Data access interface for user data
     * @param groupDataAccess Data access interface for group data
     */
    public CreateGroupTaskInteractor(CreateGroupTaskDataAccessInterface dataAccess,
                                     CreateGroupTaskOutputBoundary presenter, TaskFactory taskFactory,
                                     CreateGroupTaskUserDataAccessInterface userDataAccess,
                                     CreateGroupTaskGroupDataAccessInterface groupDataAccess) {
        this.dataAccess = dataAccess;
        this.userDataAccess = userDataAccess;
        this.groupDataAccess = groupDataAccess;
        this.presenter = presenter;
        this.taskFactory = taskFactory;
    }

    @Override
    public void execute(CreateGroupTaskInputData inputData) {
        if (!userDataAccess.isModerator()) {
            presenter.present(new CreateGroupTaskOutputData(false,
                    "Only moderators may create tasks in this group."));
            return;
        }

        Group group = groupDataAccess.getCurrentGroup();
        if (group == null) {
            presenter.present(new CreateGroupTaskOutputData(false,
                    "Group not found."));
            return;
        }

        Task task;

        if (inputData.getDueDate() != null && !inputData.getDueDate().isEmpty()) {
            LocalDateTime due = LocalDate.parse(inputData.getDueDate()).atStartOfDay();
            task = taskFactory.createWithDeadline(inputData.getDescription(), groupDataAccess.getGroupId(), due);
        } else {
            task = taskFactory.createWithoutDeadline(inputData.getDescription(), groupDataAccess.getGroupId());
        }

        group.addTask(task.getID());

        // 4. Optional: Assign users to the task
        List<String> assignees = inputData.getAssignees();
        if (assignees != null) {
            for (String username : assignees) {
                User u = userDataAccess.getUserFromUsername(username);
                if (u != null) {
                    u.getTasks().add(task.getID());
                    userDataAccess.save(u);
                }
            }
        }

        groupDataAccess.save(group);
        dataAccess.saveTask(task);
        presenter.present(new CreateGroupTaskOutputData(true,
                "Task created successfully."));
    }

}
