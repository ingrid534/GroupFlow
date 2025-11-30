package use_case.manage_members.remove_member;

import java.util.Map;

public class RemoveMemberOutputData {
    // username -> role
    private final Map<String, String> members;

    public RemoveMemberOutputData(Map<String, String> members) {
        this.members = members;
    }

    public Map<String, String> getMembers() {
        return members;
    } // getMembers
}
