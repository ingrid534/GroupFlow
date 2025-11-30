package use_case.viewgrouptasks;

import entity.task.Task;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Interactor for viewing tasks that belong to a specific group.
 */
public class ViewGroupTasksInteractor implements ViewGroupTasksInputBoundary {
    private final ViewGroupTasksDataAccessInterface dataAccess;
    private final ViewGroupTasksGroupDataAccessInterface groupDataAccess;
    private final ViewGroupTasksOutputBoundary presenter;

    /**
     * Constructs an interactor.
     *
     * @param dataAccess      the data access object
     * @param presenter       the output boundary
     * @param groupDataAccess the data access object for group
     */
    public ViewGroupTasksInteractor(ViewGroupTasksDataAccessInterface dataAccess,
                                    ViewGroupTasksOutputBoundary presenter,
                                    ViewGroupTasksGroupDataAccessInterface groupDataAccess) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
        this.groupDataAccess = groupDataAccess;
    }

    @Override
    public void execute(ViewGroupTasksInputData inputData) {
        List<Task> tasks = dataAccess.getTasksForGroup(inputData.getGroupId());

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<ViewGroupTasksOutputData.TaskDTO> dtos = new ArrayList<>();

        for (Task task : tasks) {
            String dueDateString = task.getDueDate()
                    .map(dateTime -> dateTime.format(fmt))
                    .orElse("No due date");
            dtos.add(new ViewGroupTasksOutputData.TaskDTO(task.getID(), task.getDescription(), dueDateString,
                    task.isCompleted(), task.getAssignees()));
        }

        List<String> names = groupDataAccess.getGroup(inputData.getGroupId()).getMembers();

        presenter.present(
                new ViewGroupTasksOutputData(dtos, names));
    }
}
