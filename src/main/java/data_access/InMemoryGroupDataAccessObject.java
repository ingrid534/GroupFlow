package data_access;

import entity.group.Group;
import use_case.create_group.CreateGroupDataAccessInterface;
import use_case.creategrouptask.CreateGroupTaskGroupDataAccessInterface;
import use_case.join_group.JoinGroupDataAccessInterface;
import use_case.login.LoginGroupsDataAccessInterface;
import use_case.viewgrouptasks.ViewGroupTasksGroupDataAccessInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryGroupDataAccessObject implements
        CreateGroupDataAccessInterface,
        LoginGroupsDataAccessInterface,
        CreateGroupTaskGroupDataAccessInterface,
        ViewGroupTasksGroupDataAccessInterface,
        JoinGroupDataAccessInterface {

    private final Map<String, Group> groups = new HashMap<>();

    /**
     * Checks if the given groupCode exists.
     *
     * @param groupCode the groupCode to look for
     * @return true if a group with the given groupCode exists; false otherwise
     */
    @Override
    public boolean groupCodeExists(String groupCode) {
        return groups.containsKey(groupCode);
    }

    /**
     * Returns the group object with group id.
     *
     * @param groupId the group id
     * @return the Group object
     */
    @Override
    public Group getGroup(String groupId) {
        return groups.get(groupId);
    }

    /**
     * Saves the given group to the data source.
     *
     * @param group the group entity to be saved
     */
    @Override
    public void save(Group group) {
        groups.put(group.getGroupID(), group);
    }

    /**
     * Retrieves all groups that a given user belongs to.
     *
     * @param username the username whose group memberships are requested
     * @return a list of Group entities the user is a member of
     **/
    @Override
    public List<Group> getGroupsForUser(String username) {
        List<Group> groupsForUser = new ArrayList<>();

        for (Group gr : groups.values()) {
            if (gr.isMember(username)) {
                groupsForUser.add(gr);
            }
        }

        return groupsForUser;
    }
}
