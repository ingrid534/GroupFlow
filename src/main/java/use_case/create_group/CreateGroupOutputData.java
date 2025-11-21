package use_case.create_group;

import entity.group.GroupType;

public class CreateGroupOutputData {
    private final String groupID;
    private final String groupName;
    private final GroupType groupType;

    public CreateGroupOutputData(String groupID, String groupName, GroupType groupType) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.groupType = groupType;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public GroupType getGroupType() {
        return groupType;
    }
}
