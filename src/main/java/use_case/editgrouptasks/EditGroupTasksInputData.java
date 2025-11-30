package use_case.editgrouptasks;

import java.util.List;

/**
 * Input data describing an edit to a specific group task.
 */
public class EditGroupTasksInputData {

    private final String taskId;

    private final String newDescription;
    private final String newDueDate;
    private final Boolean newCompleted;
    private final List<String> newAssigneeUsernames;
    private final String groupId;

    /**
     * Creates new input data for editing a task.
     *
     * @param taskId               the task id to edit
     * @param newDescription       the new description, or {@code null} to keep
     * @param newDueDate           the new due date, or {@code null} to keep/clear
     * @param newCompleted         the new completion flag, or {@code null} to keep
     * @param newAssigneeUsernames the new assignee user names, or {@code null} to keep
     * @param groupId              the group Id of the group
     */
    public EditGroupTasksInputData(String taskId,
                                   String newDescription, String newDueDate,
                                   Boolean newCompleted, List<String> newAssigneeUsernames,
                                   String groupId) {
        this.taskId = taskId;
        this.newDescription = newDescription;
        this.newDueDate = newDueDate;
        this.newCompleted = newCompleted;
        this.newAssigneeUsernames = newAssigneeUsernames;
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getNewDescription() {
        return newDescription;
    }

    public String getNewDueDate() {
        return newDueDate;
    }

    public Boolean getNewCompleted() {
        return newCompleted;
    }

    public List<String> getNewAssigneeUsernames() {
        return newAssigneeUsernames;
    }
}
