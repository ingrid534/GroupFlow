package use_case.manage_members.respond_request;

import entity.membership.Membership;

import java.util.List;

public interface RespondRequestDataAccessInterface {
    /**
     * Retrieves all memberships for the specified group.
     *
     * @param groupID the ID of the group
     * @return a list of Memberships for that group
     */
    List<Membership> getMembersForGroup(String groupID);

    /**
     * Retrieves all pending membership requests for the specified group.
     *
     * @param groupID the ID of the group
     * @return a list of pending Memberships for that group
     */
    List<Membership> getPendingForGroup(String groupID);

    /**
     * Updates a pending membership request for the given group and user.
     * If {@code accepted} is true, the user's membership record is updated to an
     * accepted/active state. If {@code accepted} is false, the user's pending
     * membership record is removed entirely.
     *
     * @param groupID  the ID of the group whose membership request is being updated
     * @param username the username of the member whose request is being processed
     * @param accepted true to accept the request, false to decline and remove it
     */
    void updateMembership(String groupID, String username, boolean accepted);
}
