package use_case.creategrouptask;

import java.util.List;

/**
 * Input data required to create a new task in a group.
 */
public class CreateGroupTaskInputData {

    private final String description;
    private final String dueDate;
    private final List<String> assignees;
    private final String groupId;

    /**
     * Creates new input data for editing a task.
     *
     * @param description the description of the task
     * @param dueDate     the due date in yyyy-MM-dd format
     * @param assignees   list of assignee usernames
     * @param groupId     the group id
     */
    public CreateGroupTaskInputData(String description,
                                    String dueDate,
                                    List<String> assignees, String groupId) {
        this.description = description;
        this.dueDate = dueDate;
        this.assignees = assignees;
        this.groupId = groupId;
    }

    public String getDescription() {
        return description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public List<String> getAssignees() {
        return assignees;
    }

    public String getGroupId() {
        return groupId;
    }
}
