package use_case.manage_members.view_members;

public class ViewMembersInputData {
    private final String groupId;

    public ViewMembersInputData(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }
}
