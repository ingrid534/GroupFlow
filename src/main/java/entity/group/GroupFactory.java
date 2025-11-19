package entity.group;

/**
 * Factory for creating a Group.
 */

public class GroupFactory {

    // TODO: add String groupID to factory once db is implemented
    public Group create(String groupName, GroupType groupType) {
        return new Group(groupName, groupType);
    }
}