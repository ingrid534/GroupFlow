package use_case.viewtasks;

import entity.task.Task;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ViewTasksInteractor implements ViewTasksInputBoundary {
    private final ViewTasksDataAccessInterface taskAccessObject;
    private final ViewTasksOutputBoundary presenter;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ViewTasksInteractor(ViewTasksDataAccessInterface taskAccessObject,
                               ViewTasksOutputBoundary presenter) {
        this.taskAccessObject = taskAccessObject;
        this.presenter = presenter;
    }

    @Override
    public void execute() {
        List<String> taskIds = taskAccessObject.GetTasksForCurrentUser();
        List<ViewTasksOutputData.TaskDTO> dtoList = new ArrayList<>();

        for (String taskId : taskIds) {
            Task task = taskAccessObject.getTask(taskId);

            if (task == null) {
                continue;
            }

            if (task.isCompleted()) {
                continue;
            }

            if (task.isOverdue()) {
                continue;
            }

            String dueDateString = task.getDueDate()
                            .map(dateTime -> dateTime.format(DATE_FORMATTER))
                                    .orElse("No due date");
            dtoList.add(
                    new ViewTasksOutputData.TaskDTO(
                            task.getID(),
                            task.getDescription(),
                            dueDateString,
                            task.isCompleted(),
                            task.getGroup()
                    )
            );
        }
        presenter.presentTasks(new ViewTasksOutputData(dtoList));
    }
}
