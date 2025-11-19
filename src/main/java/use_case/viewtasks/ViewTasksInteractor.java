package use_case.viewtasks;

import entity.task.Task;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ViewTasksInteractor implements ViewTasksInputBoundary {
    private final ViewTasksDataAccessInterface taskRepo;
    private final ViewTasksOutputBoundary presenter;

    public ViewTasksInteractor(ViewTasksDataAccessInterface taskRepo,
                               ViewTasksOutputBoundary presenter) {
        this.taskRepo = taskRepo;
        this.presenter = presenter;
    }

    @Override
    public void execute(ViewTasksInputData inputData) {
        List<String> task_ids = taskRepo.getTasksForUser(inputData.getUserId());
        List<ViewTasksOutputData.TaskDTO> dtoList = new ArrayList<>();

        for (String taskId : task_ids) {
            Task task = taskRepo.getTask(taskId);

            if (task == null) continue;

            dtoList.add(
                    new ViewTasksOutputData.TaskDTO(
                            task.getID(),
                            task.getDescription(),
                            task.getDueDate()
                                    .map(d -> d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                                    .orElse("No due date"),
                            task.isCompleted(),
                            task.getGroup()
                    )
            );
        }
        presenter.presentTasks(new ViewTasksOutputData(dtoList));
    }
}
