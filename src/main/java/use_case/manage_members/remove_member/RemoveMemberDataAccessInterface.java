package use_case.manage_members.remove_member;

import entity.membership.Membership;

import java.util.List;

public interface RemoveMemberDataAccessInterface {
    /**
     * Retrieves all memberships for the specified group.
     *
     * @param groupID the ID of the group
     * @return a list of Memberships for that group
     */
    List<Membership> getMembersForGroup(String groupID);

    /**
     * Removes the membership record for the specified user in the given group.
     *
     * @param groupID  the ID of the group the user is being removed from
     * @param username the username of the member to remove
     */
    void removeMembership(String groupID, String username);
}
