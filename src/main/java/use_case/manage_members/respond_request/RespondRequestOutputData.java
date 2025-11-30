package use_case.manage_members.respond_request;

import java.util.ArrayList;
import java.util.Map;

public class RespondRequestOutputData {
    // username -> role
    private final Map<String, String> members;
    // usernames
    private final ArrayList<String> pending;

    public RespondRequestOutputData(Map<String, String> members, ArrayList<String> pending) {
        this.members = members;
        this.pending = pending;
    }

    public Map<String, String> getMembers() {
        return members;
    } // getMembers

    public ArrayList<String> getPending() {
        return pending;
    } // getPending
}
