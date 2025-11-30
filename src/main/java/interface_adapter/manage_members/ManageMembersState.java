package interface_adapter.manage_members;

import java.util.HashMap;
import java.util.Map;

public class ManageMembersState {
    private String username;
    private String groupId;

    // Maps members' usernames to their roles
    private Map<String, String> members;

    public ManageMembersState(ManageMembersState copy) {
        username = copy.username;
        groupId = copy.groupId;
        members = copy.members;
    }

    // Because of the previous copy constructor, the default constructor must be explicit.
    public ManageMembersState() {
        this.username = "";
        this.groupId = "";
        this.members = new HashMap<>();
    }

    public String getUsername() {
        return username;
    }

    public String getGroupId() {
        return groupId;
    }

    public Map<String, String> getMembers() {
        return members;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setMembers(Map<String, String> members) {
        this.members = members;
    }

}
