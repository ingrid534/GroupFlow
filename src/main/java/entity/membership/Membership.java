package entity.membership;

import entity.user.User;
import entity.user.UserRole;
import entity.group.Group;

public class Membership {

    private User user;
    private Group group;
    private UserRole role;

    /**
     * Creates a new membership with the given user, group, and role of the
     * given user in the group.
     * 
     * @param user
     * @param group
     * @param role
     */
    public Membership(User user, Group group, UserRole role) {
        this.user = user;
        this.group = group;
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public Group getGroup() {
        return group;
    }

    public UserRole getRole() {
        return role;
    }

    /**
     * A group will call this method for the given user they want to remove.
     * Uncomment once helper functions are implemented.
     */
    public void deleteMembership() {
        // user.removeMembership(this);
        // group.removeMembership(this);

        // user = null;
        // group = null;
    }

    public void checkPermissions() {
        // need to implement user permissions first
    }

    public void reassignRole(UserRole newRole) {
        this.role = newRole;
    }

}
