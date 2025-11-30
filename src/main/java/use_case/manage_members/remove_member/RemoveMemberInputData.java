package use_case.manage_members.remove_member;

public class RemoveMemberInputData {
    private final String groupId;
    private final String username;

    public RemoveMemberInputData(String groupId, String username) {
        this.groupId = groupId;
        this.username = username;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getUsername() {
        return username;
    }
}
