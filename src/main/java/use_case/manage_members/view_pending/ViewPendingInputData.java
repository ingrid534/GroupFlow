package use_case.manage_members.view_pending;

public class ViewPendingInputData {
    private final String groupId;

    public ViewPendingInputData(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }
}
