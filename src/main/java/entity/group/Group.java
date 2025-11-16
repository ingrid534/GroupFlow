package entity.group;

import java.util.*;

import entity.membership.Membership;
import entity.user.User;
import entity.user.UserRole;

/**
 * A simple entity representing a group. Groups have IDs, names, memberships, and tpes.
 * Memberships act as a link between a Group and a User.
 */
public class Group {
    private final String groupID;
    private String name;
    private List<Membership> memberships;
    private String groupType;

    /**
     * Creates a new group with the given name, type, and user who created the group.
     * The user who created the group is the only member by default, and so is granted the Moderator role.
     *
     * @param name          the group name
     * @param groupType     the group type
     * @param groupCreator  the User object who created the group
     *
     */
    public Group(String name, String groupType, User groupCreator) {
        this.groupID = generateRandomID();      // Temporary until we set up a DB
        this.name = name;
        this.groupType = groupType;

        Membership creatorMembership = new Membership(groupCreator, this, UserRole.MODERATOR);
        this.memberships = List.of(creatorMembership);
    }

    private static String generateRandomID() {
        String alphanum = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        int length = 8;
        for (int i=0; i < length; i++) {
            int randIndex = random.nextInt(alphanum.length());
            char randChar = alphanum.charAt(randIndex);
            sb.append(randChar);
        }

        return sb.toString();
    }

    public void addMembership(Membership membership) {
        if (!memberships.contains(membership)) {
            memberships.add(membership);
        }
    }

    public String getGroupType() {
        return groupType;
    }

    public List<User> getMembers() {
        List<User> users = new ArrayList<>();

        for (Membership m : memberships) {
            users.add(m.getUser());
        }

        return users;
    }

    public void removeMembership(Membership membership) {
        this.memberships.remove(membership);
    }

    public User getModerator() throws NoSuchElementException {
        for (Membership m : memberships) {
            if (m.getRole() == UserRole.MODERATOR) {
                return m.getUser();
            }
        }

        throw new NoSuchElementException("No moderator found.");
    }

    public String getGroupID() {
        return groupID;
    }

    public String getName() {
        return name;
    }

    public void setName(String groupName) {
        this.name = groupName;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    @Override
    public int hashCode() {
        return groupID.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group)) return false;
        Group other = (Group) o;
        return groupID.equals(other.groupID);
    }

}
