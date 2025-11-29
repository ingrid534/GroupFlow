package use_case.manage_members.view_members;

import entity.membership.Membership;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewMembersInteractor implements ViewMembersInputBoundary {
    private final ViewMembersMembershipDataAccessInterface membershipDataAccessObject;
    private final ViewMembersOutputBoundary viewMembersPresenter;

    public ViewMembersInteractor(ViewMembersMembershipDataAccessInterface membershipDataAccessObject,
                                 ViewMembersOutputBoundary viewMembersPresenter) {
        this.membershipDataAccessObject = membershipDataAccessObject;
        this.viewMembersPresenter = viewMembersPresenter;
    } // ViewMembersInteractor

    @Override
    public void execute(ViewMembersInputData viewMembersInputData) {
        final String groupId = viewMembersInputData.getGroupId();

        List<Membership> members = membershipDataAccessObject.getMembembersForGroup(groupId);
        Map<String, String> newMembersHashMap = new HashMap<>();
        for (Membership member : members) {
            newMembersHashMap.put(member.getUsername(), member.getRole().toString());
        }

        final ViewMembersOutputData viewMembersOutputData = new ViewMembersOutputData(newMembersHashMap);
        viewMembersPresenter.prepareSuccessView(viewMembersOutputData);

    }
}
