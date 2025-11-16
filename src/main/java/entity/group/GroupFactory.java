package entity.group;

import entity.user.User;

/**
 * Factory for creating a Group.
 */

public class GroupFactory {
    public Group create(String groupName, String groupType, User groupCreator) {
        return new Group(groupName, groupType, groupCreator);
    }
}
