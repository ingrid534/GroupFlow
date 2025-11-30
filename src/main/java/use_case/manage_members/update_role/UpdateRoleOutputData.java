package use_case.manage_members.update_role;

import java.util.Map;

public class UpdateRoleOutputData {
    // username -> role
    private final Map<String, String> members;

    public UpdateRoleOutputData(Map<String, String> members) {
        this.members = members;
    }

    public Map<String, String> getMembers() {
        return members;
    } // getMembers

}
