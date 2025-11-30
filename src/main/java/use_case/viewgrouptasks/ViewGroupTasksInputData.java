package use_case.viewgrouptasks;

/**
 * Input data containing the group view group tasks.
 */
public class ViewGroupTasksInputData {
    private final String groupId;

    /**
     * Creates a new input data object.
     *
     * @param groupId the identifier of the group whose tasks are viewed
     */
    public ViewGroupTasksInputData(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }
}
