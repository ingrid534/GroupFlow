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
    private final CreateGroupTaskOutputBoundary presenter;
    private final TaskFactory taskFactory;

    /**
     * Constructs a CreateGroupTaskInteractor.
     *
     * @param dataAccess        Data access interface for task/group persistence
     * @param presenter  Output boundary for presenting results
     * @param taskFactory Factory for creating Task entities
     */
    public CreateGroupTaskInteractor(CreateGroupTaskDataAccessInterface dataAccess,
                                     CreateGroupTaskOutputBoundary presenter, TaskFactory taskFactory) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
        this.taskFactory = taskFactory;
    }

    @Override
    public void execute(CreateGroupTaskInputData inputData) {
        if (!dataAccess.isModerator(inputData.getGroupId())) {
            presenter.present(new CreateGroupTaskOutputData(false,
                    "Only moderators may create tasks in this group."));
            return;
        }

        Group group = dataAccess.getGroup(inputData.getGroupId());
        if (group == null) {
            presenter.present(new CreateGroupTaskOutputData(false,
                    "Group not found."));
            return;
        }

        Task task;

        if (inputData.getDueDate() != null && !inputData.getDueDate().isEmpty()) {
            LocalDateTime due = LocalDate.parse(inputData.getDueDate()).atStartOfDay();
            task = taskFactory.createWithDeadline(inputData.getDescription(), inputData.getGroupId(), due);
        } else {
            task = taskFactory.createWithoutDeadline(inputData.getDescription(), inputData.getGroupId());
        }

        group.addTask(task.getID());

        // 4. Optional: Assign users to the task
        List<String> assignees = inputData.getAssignees();
        if (assignees != null) {
            for (String userId : assignees) {
                User u = dataAccess.getUser(userId);
                if (u != null) {
                    u.getTasks().add(task.getID());
                    dataAccess.saveUser(u);
                }
            }
        }

        dataAccess.saveGroup(group);
        dataAccess.saveTask(task);
        presenter.present(new CreateGroupTaskOutputData(true,
                "Task created successfully."));
    }

}
