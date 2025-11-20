package entity.group;

/**
 * Factory for creating a Group.
 */

public class GroupFactory {

    /**
     * Creates a new group.
     * // TODO: add String groupID to factory once db is implemented
     * 
     * @param groupName Group name for the new group
     * @param groupType Group type for the new group
     * @return the new group
     */
    public Group create(String groupName, GroupType groupType) {
        return new Group(groupName, groupType);
    }
}