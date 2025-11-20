package entity.membership;

import entity.user.UserRole;

/**
 * Membership class.
 */
public class Membership {

    private String user;
    private String group;
    private UserRole role;

    /**
     * Creates a new membership with the given user, group, and role of the
     * given user in the group.
     * 
     * @param userID  The user corresponding to this membership.
     * @param groupID The group corresponding to this membership.
     * @param role    The role of the given user in the given group.
     */
    public Membership(String userID, String groupID, UserRole role) {
        this.user = userID;
        this.group = groupID;
        this.role = role;
    }

    public String getUser() {
        return user;
    }

    public String getGroup() {
        return group;
    }

    public UserRole getRole() {
        return role;
    }

    /**
     * Check whether a user has a certain permission.
     * Not yet implemented.
     */
    public void checkPermissions() {
        // need to implement user permissions first
    }

    /**
     * Reassign a new role to the user of this membership.
     * 
     * @param newRole The new role for the user
     */
    public void reassignRole(UserRole newRole) {
        this.role = newRole;
    }

    /**
     * Check whether the user in this membership is moderator of the group.
     * 
     * @return Whether this user has the moderator role.
     */
    public Boolean isModerator() {
        return this.role.equals(UserRole.MODERATOR);
    }

}
