package use_case.manage_members.update_role;

import entity.user.UserRole;

public class UpdateRoleInputData {
    private final String groupId;
    private final String username;
    private final UserRole newRole;

    public UpdateRoleInputData(String groupId, String username, UserRole newRole) {
        this.groupId = groupId;
        this.username = username;
        this.newRole = newRole;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getUsername() {
        return username;
    }

    public UserRole getNewRole() {
        return newRole;
    }
}
