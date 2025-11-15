package use_case.create_group;

/**
 * The Input Data for the Create Group Use Case.
 */
public class CreateGroupInputData {
    private final String groupName;
    private final String groupType;

    public CreateGroupInputData(String groupName, String groupType) {
        this.groupName = groupName;
        this.groupType = groupType;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupType() {
        return groupType;
    }

}
