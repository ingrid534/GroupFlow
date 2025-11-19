package use_case.viewtasks;

import java.util.List;

/**
 * Output Data for the ViewTasks Use Case.
 */
public class ViewTasksOutputData {

    /**
     * A task Data Transfer Object for UI/presenter layer.
     */
    public static class TaskDTO {
        public final String taskId;
        public final String description;
        public final String dueDateString;
        public final boolean completed;
        public final String groupId;

        public TaskDTO(String taskId, String description,
                       String dueDateString, boolean completed,
                       String groupId) {
            this.taskId = taskId;
            this.description = description;
            this.dueDateString = dueDateString;
            this.completed = completed;
            this.groupId = groupId;
        }
    }
    private final List<TaskDTO> tasks;

    public ViewTasksOutputData(List<TaskDTO> tasks) {
        this.tasks = tasks;
    }

    public List<TaskDTO> getTasks() {
        return tasks;
    }
}
