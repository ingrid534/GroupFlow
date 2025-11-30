package use_case.manage_members.update_role;

import entity.membership.Membership;
import entity.user.UserRole;

import java.util.List;

public interface UpdateRoleDataAccessInterface {
    /**
     * Retrieves all memberships for the specified group.
     *
     * @param groupID the ID of the group
     * @return a list of Memberships for that group
     */
    List<Membership> getMembersForGroup(String groupID);

    /**
     * Updates the membership record for the given group and user by assigning a new role.
     * This method is used after when an existing member's role needs to be changed.
     *
     * @param groupID the ID of the group whose membership is being updated
     * @param username the username of the member whose role is being changed
     * @param newRole the new role to assign to the member
     */
    void updateMembership(String groupID, String username, UserRole newRole);
}
