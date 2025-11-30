package use_case.manage_members.respond_request;

public class RespondRequestInputData {
    private final String groupId;
    private final String username;
    private final boolean isAccepted;

    public RespondRequestInputData(String groupId, String username, boolean isAccepted) {
        this.groupId = groupId;
        this.username = username;
        this.isAccepted = isAccepted;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getUsername() {
        return username;
    }

    public boolean getIsAccepted() {
        return isAccepted;
    }
}
