package data_access;

import entity.group.Group;
import use_case.create_group.CreateGroupDataAccessInterface;
import use_case.creategrouptask.CreateGroupTaskGroupDataAccessInterface;
import use_case.login.LoginGroupsDataAccessInterface;
import use_case.viewgrouptasks.ViewGroupTasksGroupDataAccessInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryGroupDataAccessObject implements CreateGroupDataAccessInterface,
        LoginGroupsDataAccessInterface,
        CreateGroupTaskGroupDataAccessInterface,
        ViewGroupTasksGroupDataAccessInterface {

    private final Map<String, Group> groups = new HashMap<>();

    @Override
    public void save(Group group) {
        groups.put(group.getGroupID(), group);
    }

    @Override
    public List<Group> getGroupsForUser(String username) {
        List<Group> result = new ArrayList<>();
        for (Group group : groups.values()) {
            for (String user : group.getMembers()) {
                if (user.equals(username)) {
                    result.add(group);
                }
            }
        }
        return result;
    }

    @Override
    public Group getGroup(String groupId) {
        return groups.get(groupId);
    }
}
