package entity.task;

import java.time.LocalDateTime;

public class TaskFactory {
    public Task createWithDeadline(String description, String groupID, LocalDateTime dueDate) {
        return new Task(description, groupID, dueDate);
    }

    public Task createWithoutDeadline(String description, String groupID) {
        return new Task(description, groupID);
    }
}
