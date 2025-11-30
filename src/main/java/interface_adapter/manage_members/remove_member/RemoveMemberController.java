package interface_adapter.manage_members.remove_member;

import use_case.manage_members.remove_member.RemoveMemberInputBoundary;
import use_case.manage_members.remove_member.RemoveMemberInputData;

public class RemoveMemberController {
    private final RemoveMemberInputBoundary removeMemberInteractor;

    public RemoveMemberController(RemoveMemberInputBoundary removeMemberInteractor) {
        this.removeMemberInteractor = removeMemberInteractor;
    }

    /**
     * Executes the Remove Member use case for the specified user in the given group.
     *
     * @param groupID  the ID of the group the member is being removed from
     * @param username the username of the member to remove
     */
    public void execute(String groupID, String username) {
        final RemoveMemberInputData viewMembersInputData = new RemoveMemberInputData(groupID, username);

        removeMemberInteractor.execute(viewMembersInputData);
    }
}
