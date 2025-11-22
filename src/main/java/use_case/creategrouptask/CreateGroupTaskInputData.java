package use_case.creategrouptask;

import java.util.List;

/**
 * Input data required to create a new task in a group.
 */
public class CreateGroupTaskInputData {

    private final String groupId;
    private final String description;
    private final String dueDate;
    private final List<String> assignees;

    /**
     * Creates new input data for editing a task.
     *
     * @param groupId     the group id
     * @param description the description of the task
     * @param dueDate     the due date in yyyy-MM-dd format
     * @param assignees   list of assignee ids
     */
    public CreateGroupTaskInputData(String groupId,
                                    String description,
                                    String dueDate,
                                    List<String> assignees) {
        this.groupId = groupId;
        this.description = description;
        this.dueDate = dueDate;
        this.assignees = assignees;
    }

    public String getGroupId() {
        return groupId;
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
}
