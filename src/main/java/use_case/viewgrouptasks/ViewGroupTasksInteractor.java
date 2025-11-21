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
    private final ViewGroupTasksOutputBoundary presenter;

    /**
     * Constructs an interactor.
     *
     * @param dataAccess the data access object
     * @param presenter  the output boundary
     */
    public ViewGroupTasksInteractor(ViewGroupTasksDataAccessInterface dataAccess,
                                ViewGroupTasksOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(ViewGroupTasksInputData inputData) {
        List<Task> tasks = dataAccess.getTasksForGroup(inputData.getGroupId());

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<ViewGroupTasksOutputData.TaskDTO> dtos = new ArrayList<>();

        for (Task task : tasks) {
            String dueDateString = task.getDueDate()
                    .map(dateTime -> dateTime.format(fmt))
                    .orElse("No due date");
            dtos.add(new ViewGroupTasksOutputData.TaskDTO(task.getID(), task.getDescription(), dueDateString,
                    task.isCompleted(), task.getAssignees()));
        }

        presenter.present(
                new ViewGroupTasksOutputData(dtos));
    }
}
