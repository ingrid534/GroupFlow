package use_case.manage_members.view_members;

import entity.membership.Membership;

import java.util.List;

public interface ViewMembersMembershipDataAccessInterface {
    /**
     * Retrieves all approved memberships that belong to the specified group.
     *
     * @param groupID the ID of the group whose members should be returned
     * @return a list of Membership objects for that group
     */
    List<Membership> getMembersForGroup(String groupID);
}
