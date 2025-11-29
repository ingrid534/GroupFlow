package use_case.manage_members.view_pending;

import entity.membership.Membership;

import java.util.List;

public interface ViewPendingMembershipDataAccessInterface {
    /**
     * Retrieves all pending membership requests for the specified group.
     *
     * @param groupID the ID of the group
     * @return a list of pending Memberships for that group
     */
    List<Membership> getPendingForGroup(String groupID);
}
