package use_case.editgrouptasks;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Input data describing an edit to a specific group task.
 */
public class EditGroupTasksInputData {

    private final String groupId;
    private final String userId;
    private final String taskId;

    private final String newDescription;
    private final LocalDateTime newDueDate;
    private final Boolean newCompleted;
    private final List<String> newAssigneeIds;

    /**
     * Creates new input data for editing a task.
     *
     * @param groupId          the group id
     * @param userId           the id of the user performing the edit
     * @param taskId           the task id to edit
     * @param newDescription   the new description, or {@code null} to keep
     * @param newDueDate       the new due date, or {@code null} to keep/clear
     * @param newCompleted     the new completion flag, or {@code null} to keep
     * @param newAssigneeIds the new assignee user ids, or {@code null} to keep
     */
    public EditGroupTasksInputData(String groupId, String userId, String taskId,
                                   String newDescription, LocalDateTime newDueDate,
                                   Boolean newCompleted, List<String> newAssigneeIds) {
        this.groupId = groupId;
        this.userId = userId;
        this.taskId = taskId;
        this.newDescription = newDescription;
        this.newDueDate = newDueDate;
        this.newCompleted = newCompleted;
        this.newAssigneeIds = newAssigneeIds;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getNewDescription() {
        return newDescription;
    }

    public LocalDateTime getNewDueDate() {
        return newDueDate;
    }

    public Boolean getNewCompleted() {
        return newCompleted;
    }

    public List<String> getNewAssigneeUserIds() {
        return newAssigneeIds;
    }
}
