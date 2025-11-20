package use_case.create_group;

import entity.group.GroupType;

/**
 * The Input Data for the Create Group Use Case.
 */
public class CreateGroupInputData {
    private final String groupName;
    private final GroupType groupType;

    public CreateGroupInputData(String groupName, GroupType groupType) {
        this.groupName = groupName;
        this.groupType = groupType;
    }

    public String getGroupName() {
        return groupName;
    }

    public GroupType getGroupType() {
        return groupType;
    }

}
