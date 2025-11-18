package entity.group;

/**
 * Factory for creating a Group.
 */

public class GroupFactory {

    // TODO: add String groupID to factory once db implemented.
    public Group create(String groupName, String groupType, String groupCreator) {
        return new Group(groupName, groupType, groupCreator);
    }
}
