package entity.group;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import entity.membership.Membership;
import entity.user.User;
import entity.user.UserRole;

/**
 * A simple entity representing a group.
 */
public class Group {
    String groupID;
    String name;
    List<Membership> memberships;
    String groupType;

    /**
     * Creates a new group with <></>.
     *
     * @param name          the group name
     * @param groupType     the group type (e.g. public, private)
     */
    public Group(String name, String groupType) {
        this.groupID = generateRandomID();      // Temporary until we set up a DB
        this.name = name;
        this.groupType = groupType;
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

    public void addMembership(Membership memberhsip) {
        if (!memberships.contains(memberhsip)) {
            memberships.add(memberhsip);
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

}
