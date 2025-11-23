package use_case.creategrouptask;

import entity.group.Group;

public interface CreateGroupTaskGroupDataAccessInterface {

    /**
     * Returns the current selected group object.
     *
     * @return the Group object
     */
    Group getCurrentGroup();

    /**
     * Returns the current selected group id.
     *
     * @return the Group id
     */
    String getGroupId();

    /**
     * Saves the group with updated info (not sure if needed).
     *
     * @param group group to be saved
     */
    void save(Group group);
}
