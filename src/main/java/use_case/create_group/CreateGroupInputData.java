package use_case.create_group;

/**
 * The Input Data for the Create Group Use Case.
 */
public class CreateGroupInputData {
    private final String groupName;
    private final String groupType;
    private final String groupCreatorUsername;

    public CreateGroupInputData(String groupName, String groupType, String groupCreatorUsername) {
        this.groupName = groupName;
        this.groupType = groupType;
        this.groupCreatorUsername = groupCreatorUsername;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupType() {
        return groupType;
    }

    public String getGroupCreatorUsername() {return groupCreatorUsername;}

}
