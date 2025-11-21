package use_case.viewtasks;

import java.util.List;

/**
 * Output Data for the ViewTasks Use Case.
 */
public class ViewTasksOutputData {

    private final List<TaskDTO> tasks;

    public ViewTasksOutputData(List<TaskDTO> tasks) {
        this.tasks = tasks;
    }

    public List<TaskDTO> getTasks() {
        return tasks;
    }

    /**
     * A task Data Transfer Object for UI/presenter layer.
     */
    public static class TaskDTO {
        private final String taskId;
        private final String description;
        private final String dueDateString;
        private final boolean completed;
        private final String groupId;

        public TaskDTO(String taskId, String description,
                       String dueDateString, boolean completed,
                       String groupId) {
            this.taskId = taskId;
            this.description = description;
            this.dueDateString = dueDateString;
            this.completed = completed;
            this.groupId = groupId;
        }

        public String getTaskId() {
            return taskId;
        }

        public String getDescription() {
            return description;
        }

        public String getDueDateString() {
            return dueDateString;
        }

        public boolean isCompleted() {
            return completed;
        }

        public String getGroupId() {
            return groupId;
        }
    }

}
