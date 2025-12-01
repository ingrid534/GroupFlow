package use_case.join_group;

import entity.group.Group;

public interface JoinGroupUserDataAccessInterface {
    /**
     * Checks if the given groupCode exists.
     *
     * @param groupCode the groupCode to look for
     * @return true if a group with the given groupCode exists; false otherwise
     */
    boolean groupCodeExists(String groupCode);

    /**
     * Return the group with the given group code.
     *
     * @param groupCode the groupCode to look for
     * @return the group to return
     */
    Group getGroup(String groupCode);
}
