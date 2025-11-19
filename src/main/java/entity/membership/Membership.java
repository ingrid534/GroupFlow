package entity.membership;

import entity.user.UserRole;

public class Membership {

    private String user;
    private String group;
    private UserRole role;

    /**
     * Creates a new membership with the given user, group, and role of the
     * given user in the group.
     * 
     * @param user
     * @param group
     * @param role
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

    public void checkPermissions() {
        // need to implement user permissions first
    }

    public void reassignRole(UserRole newRole) {
        this.role = newRole;
    }

    /**
     * 
     * @return Whether this user has the moderator role.
     */
    public Boolean isModerator() {
        return this.role.equals(UserRole.MODERATOR);
    }

}
