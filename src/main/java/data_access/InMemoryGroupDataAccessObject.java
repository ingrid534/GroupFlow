package data_access;

import entity.group.Group;
import use_case.create_group.CreateGroupDataAccessInterface;
import use_case.create_schedule.CreateScheduleGroupDataAccessInterface;
import use_case.creategrouptask.CreateGroupTaskGroupDataAccessInterface;
import use_case.join_group.JoinGroupDataAccessInterface;
import use_case.login.LoginGroupsDataAccessInterface;
import use_case.viewgrouptasks.ViewGroupTasksGroupDataAccessInterface;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryGroupDataAccessObject implements
        CreateGroupDataAccessInterface,
        LoginGroupsDataAccessInterface,
        CreateGroupTaskGroupDataAccessInterface,
        ViewGroupTasksGroupDataAccessInterface,
        JoinGroupDataAccessInterface,
        CreateScheduleGroupDataAccessInterface {

    private final Map<String, Group> groups = new HashMap<>();
    private String currentGroupID;
    private static final String JOIN_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int JOIN_CODE_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();


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
        group.setGroupId(generateUniqueJoinCode());
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

    @Override
    public void setCurrentGroupID(String groupID) {
        this.currentGroupID = groupID;
    }

    @Override
    public String getCurrentGroupID() {
        return currentGroupID;
    }

    @Override
    public void saveMasterSchedule(Group group) {
        groups.put(group.getGroupID(), group);
    }


    private String generateUniqueJoinCode() {
        String code;
        do {
            code = generateRandomJoinCode();
        } while (groupCodeExists(code));
        return code;
    }

    private String generateRandomJoinCode() {
        StringBuilder sb = new StringBuilder(JOIN_CODE_LENGTH);
        for (int i = 0; i < JOIN_CODE_LENGTH; i++) {
            int idx = random.nextInt(JOIN_CODE_CHARS.length());
            sb.append(JOIN_CODE_CHARS.charAt(idx));
        }
        return sb.toString();
    }
}
