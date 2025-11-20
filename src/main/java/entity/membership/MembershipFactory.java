package entity.membership;

import entity.user.UserRole;

/**
 * Class to create a new membership instance.
 */
public class MembershipFactory {
    /**
     * Create a new membership instance.
     * 
     * @param userID   User for membership.
     * @param groupID  Group for membership.
     * @param userRole Role of the user in the group.
     * @return the new membership
     */
    public Membership create(String userID, String groupID, UserRole userRole) {
        return new Membership(userID, groupID, userRole);
    }
}
