package entity.membership;

import entity.user.UserRole;

/**
 * Membership class.
 */
public class Membership {

    private String username;
    private String group;
    private UserRole role;
    private boolean approved;

    /**
     * Creates a new membership record for a user in a group. Stores the user ID,
     * group ID, the role assigned to the user, and whether the membership has
     * been approved or is still pending.
     *
     * @param username   The ID of the user this membership belongs to.
     * @param groupID  The ID of the group the user is joining.
     * @param role     The role assigned to the user in this group.
     * @param approved True if the user has been accepted into the group, false if
     *                 they have only requested to join.
     */
    public Membership(String username, String groupID, UserRole role, boolean approved) {
        this.username = username;
        this.group = groupID;
        this.role = role;
        this.approved = approved;
    }

    public String getUsername() {
        return username;
    }

    public String getGroup() {
        return group;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isApproved() {
        return approved;
    }

    /**
     * Marks this membership as approved. After calling this method,
     * the user is considered a full member of the group.
     */
    public void approve() {
        this.approved = true;
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
