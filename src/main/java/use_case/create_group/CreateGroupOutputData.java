package use_case.create_group;

import entity.group.GroupType;

import java.util.Map;

public class CreateGroupOutputData {
    private final String groupID;
    private final String groupName;
    private final GroupType groupType;
    private final Map<String, String> groups;

    public CreateGroupOutputData(String groupID, String groupName, GroupType groupType, Map<String, String> groups) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.groupType = groupType;
        this.groups = groups;
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

    public Map<String, String> getGroups() {
        return groups;
    }
}
