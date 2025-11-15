package interface_adapter.create_group;

/**
 * The state for the Create Group View Model.
 */
public class CreateGroupState {

    private String groupName = "";
    private String groupType = "";

    public String getGroupName() {
        return groupName;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }


}
