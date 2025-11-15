package use_case.create_group;

public class CreateGroupOutputData {
    private final String groupID;

    public CreateGroupOutputData(String groupID) {
        this.groupID = groupID;
    }

    public String getGroupID() {
        return groupID;
    }
}
