package use_case.creategrouptask;

import entity.group.Group;

public interface CreateGroupTaskGroupDataAccessInterface {

    /**
     * Returns the group object with group id.
     *
     * @param groupId the group id
     * @return the Group object
     */
    Group getGroup(String groupId);

    /**
     * Saves the group with updated info (not sure if needed).
     *
     * @param group group to be saved
     */
    void save(Group group);
}
