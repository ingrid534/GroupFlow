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
    private final ViewGroupTasksGroupDataAccessInterface viewDataAccess;
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
        this.viewDataAccess = groupDataAccess;
    }

    @Override
    public void execute() {
        List<Task> tasks = dataAccess.getTasksForGroup();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<ViewGroupTasksOutputData.TaskDTO> dtos = new ArrayList<>();

        for (Task task : tasks) {
            String dueDateString = task.getDueDate()
                    .map(dateTime -> dateTime.format(fmt))
                    .orElse("No due date");
            dtos.add(new ViewGroupTasksOutputData.TaskDTO(task.getID(), task.getDescription(), dueDateString,
                    task.isCompleted(), task.getAssignees()));
        }

        List<String> names = viewDataAccess.getMemberNames();

        presenter.present(
                new ViewGroupTasksOutputData(dtos, names));
    }
}
