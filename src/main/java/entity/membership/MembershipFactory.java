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
     * @param approved True if the user has been accepted into the group, false if
     *                 they have only requested to join.
     * @return the new membership
     */
    public Membership create(String userID, String groupID, UserRole userRole, boolean approved) {
        return new Membership(userID, groupID, userRole, approved);
    }
}
