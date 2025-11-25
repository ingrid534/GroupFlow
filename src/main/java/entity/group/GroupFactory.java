package entity.group;

/**
 * Factory for creating a Group.
 */

public class GroupFactory {

    /**
     * Creates a new group.
     * @param groupName Group name for the new group
     * @param groupId   Group id for the new group
     * @param groupType Group type for the new group
     * @return the new group
     */
    public Group create(String groupName, String groupId, GroupType groupType) {
        return new Group(groupName, groupId, groupType);
    }
}
