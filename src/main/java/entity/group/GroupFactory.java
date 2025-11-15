package entity.group;

/**
 * Factory for creating a Group.
 */

public class GroupFactory {
    public Group create(String groupName, String groupType) {
        return new Group(groupName, groupType);
    }
}
