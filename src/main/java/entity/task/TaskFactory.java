package entity.task;

import java.time.LocalDateTime;

public class TaskFactory {
    /**
     * Create a new task instance with a deadline.
     * 
     * @param description Task description
     * @param groupID     Group associated with task
     * @param dueDate     Due date for the task.
     * @return the new task
     */
    public Task createWithDeadline(String description, String groupID, LocalDateTime dueDate) {
        return new Task(description, groupID, dueDate);
    }

    /**
     * Create a new task instance without a deadline.
     * 
     * @param description Task description
     * @param groupID     Group associated with task.
     * @return Due date for the task.
     */
    public Task createWithoutDeadline(String description, String groupID) {
        return new Task(description, groupID);
    }
}
