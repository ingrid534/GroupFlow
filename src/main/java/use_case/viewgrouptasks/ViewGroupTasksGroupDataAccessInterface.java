package use_case.viewgrouptasks;

import entity.group.Group;

public interface ViewGroupTasksGroupDataAccessInterface {

    /**
     * Returns all member names that belong to the given group.
     *
     * @param groupId the group id
     * @return the list of member names for that group (never {@code null})
     */
    Group getGroup(String groupId);
}
