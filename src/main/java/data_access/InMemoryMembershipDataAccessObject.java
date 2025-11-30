package data_access;

import entity.membership.Membership;
import entity.user.UserRole;
import use_case.create_group.CreateGroupMembershipDataAccessInterface;
import use_case.creategrouptask.CreateGroupTasksMembershipDataAccessInterface;
import use_case.editgrouptasks.EditGroupTasksMembershipDataAccessInterface;
import use_case.manage_members.remove_member.RemoveMemberDataAccessInterface;
import use_case.manage_members.respond_request.RespondRequestDataAccessInterface;
import use_case.manage_members.update_role.UpdateRoleDataAccessInterface;
import use_case.manage_members.view_members.ViewMembersMembershipDataAccessInterface;
import use_case.manage_members.view_pending.ViewPendingMembershipDataAccessInterface;

import java.util.ArrayList;
import java.util.List;

public class InMemoryMembershipDataAccessObject implements
        CreateGroupMembershipDataAccessInterface,
        ViewMembersMembershipDataAccessInterface,
        ViewPendingMembershipDataAccessInterface,
        CreateGroupTasksMembershipDataAccessInterface,
        EditGroupTasksMembershipDataAccessInterface,
        RemoveMemberDataAccessInterface,
        RespondRequestDataAccessInterface,
        UpdateRoleDataAccessInterface {

    private final List<Membership> memberships = new ArrayList<>();

    /**
     * Saves the membership group to the data source.
     *
     * @param membership the membership entity to be saved
     */
    @Override
    public void save(Membership membership) {
        memberships.add(membership);
    }

    /**
     * Retrieves a membership by user id and group id.
     *
     * @param username The user id.
     * @param groupID  The group id.
     * @return The Membership object if found.
     * @throws RuntimeException If membership is not found.
     */
    @Override
    public Membership get(String username, String groupID) {
        Membership membership = findMembershipByDetails(username, groupID);

        if (membership != null) {
            return membership;
        }

        throw new RuntimeException("Membership not found");
    }

    /**
     * Removes the membership record for the specified user in the given group.
     *
     * @param groupID  the ID of the group the user is being removed from
     * @param username the username of the member to remove
     */
    @Override
    public void removeMembership(String groupID, String username) {
        Membership membership = findMembershipByDetails(username, groupID);

        if (membership != null) {
            memberships.remove(membership);
        }

    }

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
    @Override
    public void updateMembership(String groupID, String username, boolean accepted) {
        Membership membership = findMembershipByDetails(username, groupID);

        if (membership == null) {
            return;
        }

        if (accepted) {
            membership.approve();
        } else {
            memberships.remove(membership);
        }
    }

    /**
     * Updates the membership record for the given group and user by assigning a new role.
     * This method is used after when an existing member's role needs to be changed.
     *
     * @param groupID  the ID of the group whose membership is being updated
     * @param username the username of the member whose role is being changed
     * @param newRole  the new role to assign to the member
     */
    @Override
    public void updateMembership(String groupID, String username, UserRole newRole) {
        Membership membership = findMembershipByDetails(username, groupID);

        if (membership == null) {
            return;
        }

        membership.reassignRole(newRole);
    }

    /**
     * Retrieves all approved memberships that belong to the specified group.
     *
     * @param groupID the ID of the group whose members should be returned
     * @return a list of Membership objects for that group
     */
    @Override
    public List<Membership> getMembersForGroup(String groupID) {
        List<Membership> approvedMemberships = new ArrayList<>();

        for (Membership m : memberships) {
            if (m.getGroup().equals(groupID) && m.isApproved()) {
                approvedMemberships.add(m);
            }
        }

        return approvedMemberships;
    }

    /**
     * Retrieves all pending membership requests for the specified group.
     *
     * @param groupID the ID of the group
     * @return a list of pending Memberships for that group
     */
    @Override
    public List<Membership> getPendingForGroup(String groupID) {
        List<Membership> pendingMemberships = new ArrayList<>();

        for (Membership m : memberships) {
            if (m.getGroup().equals(groupID) && !m.isApproved()) {
                pendingMemberships.add(m);
            }
        }

        return pendingMemberships;
    }

    private Membership findMembershipByDetails(String username, String groupID) {
        for (Membership m : memberships) {
            if (m.getUsername().equals(username) && m.getGroup().equals(groupID)) {
                return m;
            }
        }

        return null;
    }
}
