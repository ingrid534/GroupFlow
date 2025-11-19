package entity.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Task {
    private String id;
    private String description;
    private boolean completed;
    private final String group;
    private final List<String> assignees;
    private LocalDateTime dueDate;

    /**
     * Creates a new Task instance with the given due date.
     *
     * @param description The text description of the task
     * @param group       The group to which this task belongs
     */
    public Task(String description, String groupID, LocalDateTime dueDate) {
        this.id = UUID.randomUUID().toString();
        this.description = description;
        this.dueDate = dueDate;
        this.group = groupID;
        this.completed = false;
        this.assignees = new ArrayList<>();
    }

    /**
     * Creates a new Task instance without a due date.
     * 
     * @param description
     * @param groupID
     */
    public Task(String description, String groupID) {
        this.id = UUID.randomUUID().toString();
        this.description = description;
        this.group = groupID;
        this.completed = false;
        this.assignees = new ArrayList<>();
    }

    /**
     * @return The id of this task.
     */
    public String getID() {
        return this.id;
    }

    /**
     * @return The description of this task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return The groupID of the group this task is associated with.
     */
    public String getGroup() {
        return group;
    }

    /**
     * @return A list of userIDs corresponding to the users assigned to this task.
     */
    public List<String> getAssignees() {
        return assignees;
    }

    /**
     * @return The due date of this task, or {@code null} if no due date is set.
     */
    public Optional<LocalDateTime> getDueDate() {
        return Optional.ofNullable(dueDate);
    }

    /**
     * @return {@code true} if the task is marked as completed, otherwise
     *         {@code false}.
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
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * 
     * @return Whether this task has a set due date.
     */
    public Boolean hasDueDate() {
        return dueDate != null;
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
     * Adds a user to the list of assignees
     *
     * @param user UserID of the User to assign to this task to
     */

    public void addAssignee(String userID) {
        assignees.add(userID);
    }

    /**
     * Removes a user from the list of assignees.
     *
     * @param user The user to remove from assignment
     */
    public void removeAssignee(String userID) {
        assignees.remove(userID);
    }
}
