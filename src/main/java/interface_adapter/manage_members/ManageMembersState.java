package interface_adapter.manage_members;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManageMembersState {
    private String username;
    private String groupId;

    // Maps members' usernames to their roles
    private Map<String, String> members;
    // List of usernames of users who requested to join
    private ArrayList<String> pending;

    public ManageMembersState(ManageMembersState copy) {
        username = copy.username;
        groupId = copy.groupId;
        members = copy.members;
        pending = copy.pending;
    }

    // Because of the previous copy constructor, the default constructor must be explicit.
    public ManageMembersState() {
        this.username = "";
        this.groupId = "";
        this.members = new HashMap<>();
        this.pending = new ArrayList<>();
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

    public ArrayList<String> getPending() {
        return pending;
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

    public void setPending(ArrayList<String> pending) {
        this.pending = pending;
    }

}
