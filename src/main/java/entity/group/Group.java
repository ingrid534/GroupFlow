package entity.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.NoSuchElementException;
import java.util.UUID;

import entity.membership.Membership;
import entity.user.User;
import entity.user.UserRole;
import entity.task.Task;

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
    private String groupType;

    /**
     * Creates a new group with the given name, type, and user who created the
     * group.
     * The user who created the group is the only member by default, and so is
     * granted the Moderator role.
     *
     * @param name         the group name
     * @param groupType    the group type
     * @param groupCreator the User ID of the user that created this group.
     *
     */
    public Group(String name, String groupType, String groupCreator) {
        this.groupID = UUID.randomUUID().toString(); // Temporary until we set up a DB
        this.name = name;
        this.groupType = groupType;

        // will be fixed after Membership is updated.
        Membership creatorMembership = new Membership(groupCreator, this.groupID, UserRole.MODERATOR);
        this.memberships = new ArrayList<>(List.of(creatorMembership));
        this.tasks = new ArrayList<>();
    }

    public String getGroupID() {
        return groupID;
    }

    public String getName() {
        return name;
    }

    public String getGroupType() {
        return groupType;
    }

    /**
     * 
     * @return The userIDs associated with all the users in this group.
     */
    public List<String> getMembers() {
        List<String> users = new ArrayList<>();

        // .add will be resolved after changing membership class.
        for (Membership m : memberships) {
            users.add(m.getUser());
        }

        return users;
    }

    /**
     * May update this method later - unsure about using taskIDs
     * 
     * @return The list of Task IDs associated with all the tasks in this group.
     */
    public List<String> getTasks() {
        return tasks;
    }

    // Unsure about implementation
    // public User getModerator() throws NoSuchElementException {
    // for (Membership m : memberships) {
    // if (m.isModerator()) {
    // return m.getUser();
    // }
    // }

    // throw new NoSuchElementException("No moderator found.");

    // }

    public void setName(String groupName) {
        this.name = groupName;
    }

    public void setGroupType(String groupType) {
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

    public void assignTask(String taskID, String userID) {
        // TODO: implement this
    }

    public void unassignTask(String taskID, String userID) {
        // TODO: implement this
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
