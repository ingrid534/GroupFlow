package entity.group;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import entity.membership.Membership;
import entity.user.UserRole;

/**
 * A simple entity representing a group. Groups have IDs, names, memberships,
 * and tpes.
 * Memberships act as a link between a Group and a User.
 */
public class Group {
    private final String groupID;
    private String name;
    private List<Membership> memberships;
    private List<String> tasks;
    private GroupType groupType;

    /**
     * Creates a new group with the given name, type, and user who created the
     * group.
     * The user who created the group is the only member by default, and so is
     * granted the Moderator role.
     *
     * @param name      the group name
     * @param groupType the group type
     *
     */
    public Group(String name, GroupType groupType) {
        this.groupID = UUID.randomUUID().toString(); // Temporary until we set up a DB
        this.name = name;
        this.groupType = groupType;
        this.memberships = new ArrayList<>();
        this.tasks = new ArrayList<>();
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
     * 
     * @return The userIDs associated with all the users in this group.
     */
    public List<String> getMembers() {
        List<String> users = new ArrayList<>();

        for (Membership m : memberships) {
            users.add(m.getUser());
        }

        return users;
    }

    /**
     * 
     * @return The list of Task IDs associated with all the tasks in this group.
     */
    public List<String> getTasks() {
        return tasks;
    }

    /**
     * 
     * @return The moderator of this group. If group has no moderator, throw an
     *         exception.
     * @throws NoSuchElementException
     */
    public String getModerator() throws NoSuchElementException {
        for (Membership m : memberships) {
            if (m.isModerator()) {
                return m.getUser();
            }
        }
        throw new NoSuchElementException("No moderator in this group.");
    }

    /**
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

    public void addMembership(Membership membership) {
        if (!memberships.contains(membership)) {
            memberships.add(membership);
        }
    }

    public void removeMembership(Membership membership) {
        this.memberships.remove(membership);
    }

    /**
     * 
     * @param userID
     * @return Whether the user with the given user ID is a member of this group.
     */
    public Boolean isMember(String userID) {
        for (Membership m : memberships) {
            if (userID.equals(m.getUser())) {
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
     * @param userID
     * @param newRole
     */
    public void changeUserRole(String userID, UserRole newRole) throws NoSuchElementException, IllegalStateException {
        Membership target = null;

        for (Membership m : memberships) {
            if (userID.equals(m.getUser())) {
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

    @Override
    public int hashCode() {
        return groupID.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Group))
            return false;
        Group other = (Group) o;
        return groupID.equals(other.groupID);
    }

}
