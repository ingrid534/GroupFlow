package use_case.manage_members.view_members;

import java.util.Map;

public class ViewMembersOutputData {
    // username -> role
    private final Map<String, String> members;

    public ViewMembersOutputData(Map<String, String> members) {
        this.members = members;
    }

    public Map<String, String> getMembers() {
        return members;
    } // getMembers
}
