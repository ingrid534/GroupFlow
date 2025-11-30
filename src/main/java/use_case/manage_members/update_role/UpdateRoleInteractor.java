package use_case.manage_members.update_role;

import entity.membership.Membership;
import entity.user.UserRole;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateRoleInteractor implements UpdateRoleInputBoundary {
    private UpdateRoleDataAccessInterface membershipDataAccessObject;
    private final UpdateRoleOutputBoundary updateRolePresenter;

    public UpdateRoleInteractor(UpdateRoleDataAccessInterface membershipDataAccessObject,
                                UpdateRoleOutputBoundary updateRolePresenter) {
        this.membershipDataAccessObject = membershipDataAccessObject;
        this.updateRolePresenter = updateRolePresenter;
    } // ViewMembersInteractor

    @Override
    public void execute(UpdateRoleInputData updateRoleInputData) {
        final String groupId = updateRoleInputData.getGroupId();
        final String username = updateRoleInputData.getUsername();
        final UserRole newRole = updateRoleInputData.getNewRole();

        // update membership record
        membershipDataAccessObject.updateMembership(groupId, username, newRole);
        // update members
        List<Membership> members = membershipDataAccessObject.getMembersForGroup(groupId);
        Map<String, String> newMembersHashMap = new HashMap<>();
        for (Membership member : members) {
            newMembersHashMap.put(member.getUsername(), member.getRole().toString());
        }

        final UpdateRoleOutputData updateRoleOutputData = new UpdateRoleOutputData(newMembersHashMap);
        updateRolePresenter.prepareSuccessView(updateRoleOutputData);
    }
}
