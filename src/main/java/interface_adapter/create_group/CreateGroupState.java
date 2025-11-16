package interface_adapter.create_group;

/**
 * The state for the Create Group View Model.
 */
public class CreateGroupState {

    private String groupName = "";
    private String groupType = "";
    private String groupCreatorUsername = "";

    public String getGroupName() {
        return groupName;
    }

    public String getGroupType() {
        return groupType;
    }

    public String getGroupCreatorUsername() {return groupCreatorUsername;}

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public void setGroupCreatorUsername(String groupCreatorUsername) {this.groupCreatorUsername = groupCreatorUsername;}


}
