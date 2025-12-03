package data_access;

import entity.membership.Membership;
import entity.user.UserRole;
import use_case.create_group.CreateGroupMembershipDataAccessInterface;
import use_case.create_schedule.CreateScheduleMembershipDataAccessInterface;
import use_case.creategrouptask.CreateGroupTasksMembershipDataAccessInterface;
import use_case.editgrouptasks.EditGroupTasksMembershipDataAccessInterface;
import use_case.join_group.JoinGroupMembershipDataAccessInterface;
import use_case.manage_members.remove_member.RemoveMemberDataAccessInterface;
import use_case.manage_members.respond_request.RespondRequestDataAccessInterface;
import use_case.manage_members.update_role.UpdateRoleDataAccessInterface;
import use_case.manage_members.view_members.ViewMembersMembershipDataAccessInterface;
import use_case.manage_members.view_pending.ViewPendingMembershipDataAccessInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryMembershipDataAccessObject implements
        CreateGroupMembershipDataAccessInterface,
        ViewMembersMembershipDataAccessInterface,
        ViewPendingMembershipDataAccessInterface,
        CreateGroupTasksMembershipDataAccessInterface,
        JoinGroupMembershipDataAccessInterface,
        EditGroupTasksMembershipDataAccessInterface,
        RemoveMemberDataAccessInterface,
        RespondRequestDataAccessInterface,
        UpdateRoleDataAccessInterface,
        CreateScheduleMembershipDataAccessInterface {

    private final Map<Keys, Membership> memberships = new HashMap<>();

    /**
     * Saves the membership group to the data source.
     *
     * @param membership the membership entity to be saved
     */
    @Override
    public void save(Membership membership) {
        memberships.put(new Keys(membership.getUsername(), membership.getGroup()), membership);
    }

    /**
     * Retrieves a membership by user id and group id.
     *
     * @param username The user id.
     * @param groupID  The group id.
     * @return The Membership object if found.
     */
    @Override
    public Membership get(String username, String groupID) {
        return memberships.get(new Keys(username, groupID));
    }

    /**
     * Removes the membership record for the specified user in the given group.
     *
     * @param groupID  the ID of the group the user is being removed from
     * @param username the username of the member to remove
     */
    @Override
    public void removeMembership(String groupID, String username) {
        memberships.remove(new Keys(username, groupID));
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
        Keys membershipKey = new Keys(username, groupID);
        Membership membership = memberships.get(membershipKey);

        if (membership == null) {
            return;
        }

        if (accepted) {
            membership.approve();
        } else {
            memberships.remove(membershipKey);
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
        Membership membership = memberships.get(new Keys(username, groupID));

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

        for (Membership m : memberships.values()) {
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

        for (Membership m : memberships.values()) {
            if (m.getGroup().equals(groupID) && !m.isApproved()) {
                pendingMemberships.add(m);
            }
        }

        return pendingMemberships;
    }

    public static class Keys {
        private final String username;
        private final String groupId;

        public Keys(String userID, String groupID) {
            this.username = userID;
            this.groupId = groupID;
        }

        public String getUserID() {
            return username;
        }

        public String getGroupID() {
            return groupId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Keys)) {
                return false;
            }
            Keys keys = (Keys) o;
            return username.equals(keys.username)
                    && groupId.equals(keys.groupId);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(username, groupId);
        }
    }
}
