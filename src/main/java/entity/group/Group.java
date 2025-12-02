package entity.group;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import entity.membership.Membership;
import entity.user.UserRole;

/**
 * A simple entity representing a group. Groups have IDs, names, memberships,
 * and types.
 * Memberships act as a link between a Group and a User.
 */
public class Group {
    private String groupID;
    private String name;
    private List<Membership> memberships;
    private List<String> tasks;
    private GroupType groupType;
    private int[][] masterSchedule;

    /**
     * Creates a new group with the given name, type, and user who created the
     * group.
     * The user who created the group is the only member by default, and so is
     * granted the Moderator role.
     *
     * @param name      the group name
     * @param groupId   the group join code (id)
     * @param groupType the group type
     *
     */
    public Group(String name, String groupId, GroupType groupType) {
        this.groupID = groupId;
        this.name = name;
        this.groupType = groupType;
        this.memberships = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.masterSchedule = new int[12][7];
    }

    public String getGroupID() {
        return groupID;
    }

    public String getName() {
        return name;
    }

    public GroupType getGroupType() {
        return groupType;
    }

    /**
     * Method to get all members in this group.
     * 
     * @return The userIDs associated with all the users in this group.
     */
    public List<String> getMembers() {
        final List<String> users = new ArrayList<>();

        for (Membership m : memberships) {
            if (m.isApproved()) {
                users.add(m.getUsername());
            }
        }

        return users;
    }

    /**
     * Method to get all tasks in this group.
     * 
     * @return The list of Task IDs associated with all the tasks in this group.
     */
    public List<String> getTasks() {
        return tasks;
    }

    public int[][] getMasterSchedule() {
        return masterSchedule;
    }

    /**
     * Method to get the moderator of this group.
     * @return The moderator of this group
     * @throws NoSuchElementException if the group has no moderator
     */
    public String getModerator() throws NoSuchElementException {
        for (Membership m : memberships) {
            if (m.isModerator()) {
                return m.getUsername();
            }
        }
        throw new NoSuchElementException("No moderator in this group.");
    }

    /**
     * Count the numebr of members in this group.
     * @return the number of members.
     */
    public int getSize() {
        return memberships.size();
    }

    /**
     * Method to check whether this group has a moderator.
     * 
     * @return Whether this group has a moderator.
     */
    public boolean hasModerator() {
        for (Membership m : memberships) {
            if (m.isModerator()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the number of moderators in this group.
     * 
     * @return How many moderators there are in this group.
     */
    public int moderatorCount() {
        int count = 0;
        for (Membership m : memberships) {
            if (m.getRole().equals(UserRole.MODERATOR)) {
                count++;
            }
        }
        return count;
    }

    public void setName(String groupName) {
        this.name = groupName;
    }

    public void setGroupType(GroupType groupType) {
        this.groupType = groupType;
    }

    public void setGroupId(String groupId) {
        this.groupID = groupId;
    }

    public void setMasterSchedule(int[][] newMasterSchedule) {
        this.masterSchedule = newMasterSchedule;
    }

    /**
     * Adds the given membership to this group.
     * 
     * @param membership The membership to be added to the group.
     */
    public void addMembership(Membership membership) {
        if (!memberships.contains(membership)) {
            memberships.add(membership);
        }
    }

    /**
     * Removes the given membership from this group.
     * 
     * @param membership The membership to be removed.
     */
    public void removeMembership(Membership membership) {
        this.memberships.remove(membership);
    }

    /**
     * Checks whether the given user is a member of this group.
     * 
     * @param username The user whose membership to check
     * @return Whether the user with the given user ID is a member of this group.
     */
    public Boolean isMember(String username) {
        for (Membership m : memberships) {
            if (username.equals(m.getUsername())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Update the role of the user corresponding to the given ID with the given new
     * role.
     * If the user is not in the group, throw NoSuchElementException. If the new
     * role is the same as the user's old role, do nothing. If the user is the last
     * moderator in the group and will be demoted, throw IllegalStateException
     * (group must have at least one moderator).
     * 
     * @param userID  The userID for the user whose role needs to change
     * @param newRole The new role for the given user.
     * @throws NoSuchElementException if the given user is not in this group
     * @throws IllegalStateException  if the user is the last moderator in the group
     *                                and will be demoted
     */
    public void changeUserRole(String userID, UserRole newRole) throws NoSuchElementException, IllegalStateException {
        Membership target = null;

        for (Membership m : memberships) {
            if (userID.equals(m.getUsername())) {
                target = m;
            }
        }

        if (!isMember(userID)) {
            throw new NoSuchElementException("User is not in this group.");
        } else if (newRole.equals(target.getRole())) {
            return;
        } else if (target.getRole().equals(UserRole.MODERATOR) && moderatorCount() < 2) {
            throw new IllegalStateException("Group must have at least one moderator");
        } else {
            target.reassignRole(newRole);
        }

    }

    /**
     * Add the given task to the group's list of tasks.
     *
     * @param taskID the task to add
     */
    public void addTask(String taskID) {
        this.tasks.add(taskID);
    }

    /**
     * Remove the given task from the group's list of tasks.
     *
     * @param taskID Task to remove
     */
    public void removeTask(String taskID) {
        this.tasks.remove(taskID);
    }

    @Override
    public int hashCode() {
        return groupID.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Group)) {
            return false;
        }
        Group other = (Group) o;
        return groupID.equals(other.groupID);
    }

}
