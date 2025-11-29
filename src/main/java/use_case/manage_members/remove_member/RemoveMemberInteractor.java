package use_case.manage_members.remove_member;

import entity.membership.Membership;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoveMemberInteractor implements RemoveMemberInputBoundary {
    private RemoveMemberDataAccessInterface membershipDataAccessObject;
    private final RemoveMemberOutputBoundary removeMemberPresenter;

    public RemoveMemberInteractor(RemoveMemberDataAccessInterface membershipDataAccessObject,
                                  RemoveMemberOutputBoundary removeMemberPresenter) {
        this.membershipDataAccessObject = membershipDataAccessObject;
        this.removeMemberPresenter = removeMemberPresenter;
    } // ViewMembersInteractor

    @Override
    public void execute(RemoveMemberInputData viewMembersInputData) {
        final String groupId = viewMembersInputData.getGroupId();
        final String username = viewMembersInputData.getUsername();

        // remove
        membershipDataAccessObject.removeMembership(groupId, username);
        // update
        List<Membership> members = membershipDataAccessObject.getMembersForGroup(groupId);
        Map<String, String> newMembersHashMap = new HashMap<>();
        for (Membership member : members) {
            newMembersHashMap.put(member.getUsername(), member.getRole().toString());
        }

        final RemoveMemberOutputData removeMemberOutputData = new RemoveMemberOutputData(newMembersHashMap);
        removeMemberPresenter.prepareSuccessView(removeMemberOutputData);
    }
}
