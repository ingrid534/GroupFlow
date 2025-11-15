package entity.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// import entity.group.Group;
// import entity.user.User;

public class Task {
    private String id;
    private String description;
    private boolean completed;
    // private final Group group;
    // private final List<User> assignees;
    private LocalDateTime dueDate;

    /**
     * Creates a new Task instance.
     *
     * @param description The text description of the task
     * @param group       The group to which this task belongs
     */
    public Task(String description, LocalDateTime dueDate/*, Group group*/) {
        this.id = UUID.randomUUID().toString();
        this.description = description;
        this.dueDate = dueDate;
        // this.group = group;
        this.completed = false;
        // this.assignees = new ArrayList<>();
    }

    /**
     * @return The description of this task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return The group this task is associated with.
     */
    /* public Group getGroup() {
        return group;
    } */

    /**
     * @return An unmodifiable list of users assigned to this task.
     */
    /* public List<User> getAssignees() {
        return assignees;
    } */

    /**
     * @return The due date of this task, or {@code null} if no due date is set.
     */
    public LocalDateTime getDueDate() {
        return dueDate;
    }

    /**
     * @return {@code true} if the task is marked as completed, otherwise {@code false}.
     */
    public boolean isCompleted() {
        return completed;
    }
    
    /**
     * Updates the description of this task.
     *
     * @param description The new description text
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Marks this task as completed.
     */
    public void markCompleted() {
        this.completed = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markIncomplete() {
        this.completed = false;
    }
    
    /**
     * Sets the due date of the task.
     *
     * @param date The deadline for the task
     */
    public void setDueDate(LocalDateTime date) {
        this.dueDate = date;
    }

    /**
     * Determines whether this task is overdue.
     *
     * @return {@code true} if the task is incomplete,
     *         and the due date has passed; otherwise {@code false}.
     */
    public boolean isOverdue() {
        return !completed &&
                dueDate.isBefore(LocalDateTime.now());
    }

    /**
     * Adds a user to the list of assignees.
     * This method is intentionally package-private and should only be invoked
     * by {@code Group} to enforce group-level permission logic.
     *
     * @param user User to assign to this task
     */
    /* void addAssignee(User user) {
        assignees.add(user);
        user.addTask(this);
    } */

    /**
     * Removes a user from the list of assignees.
     * This method is intentionally package-private and should only be invoked
     * by {@code Group}.
     *
     * @param user The user to remove from assignment
     */
    /* void removeAssignee(User user) {
        assignees.remove(user);
        user.removeTask(this);
    } */
}
