package use_case.viewgrouptasks;

import java.util.List;

public class ViewGroupTasksOutputData {

    private final java.util.List<TaskDTO> tasks;

    /**
     * Creates new output data for group tasks.
     *
     * @param tasks the tasks belonging to the group
     */
    public ViewGroupTasksOutputData(List<TaskDTO> tasks) {
        this.tasks = tasks;
    }

    public java.util.List<TaskDTO> getTasks() {
        return tasks;
    }

    /**
     * DTO representing a single task row in the UI.
     */
    public static class TaskDTO {

        private final String id;
        private final String description;
        private final String dueDateString;
        private final boolean completed;
        private final List<String> assigneeUserIds;

        /**
         * Creates a new task DTO.
         *
         * @param id                 the task id
         * @param description        the description
         * @param dueDateString      the formatted due date (may be empty)
         * @param completed          whether the task is completed
         * @param assigneeUserIds  usernames of the assignees
         */
        public TaskDTO(String id, String description, String dueDateString,
                       boolean completed, List<String> assigneeUserIds) {
            this.id = id;
            this.description = description;
            this.dueDateString = dueDateString;
            this.completed = completed;
            this.assigneeUserIds = assigneeUserIds;
        }

        public String getId() {
            return id;
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

        public List<String> getAssigneeUserIds() {
            return assigneeUserIds;
        }
    }
}
