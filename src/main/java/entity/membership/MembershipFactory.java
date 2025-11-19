package entity.membership;

import entity.user.UserRole;

public class MembershipFactory {
    public Membership create(String userID, String groupID, UserRole userRole) {
        return new Membership(userID, groupID, userRole);
    }
}
