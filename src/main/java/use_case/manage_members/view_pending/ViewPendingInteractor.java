package use_case.manage_members.view_pending;

import entity.membership.Membership;

import java.util.ArrayList;
import java.util.List;

public class ViewPendingInteractor implements ViewPendingInputBoundary {
    private final ViewPendingMembershipDataAccessInterface membershipDataAccessObject;
    private final ViewPendingOutputBoundary viewPendingPresenter;

    public ViewPendingInteractor(ViewPendingMembershipDataAccessInterface membershipDataAccessObject,
                                 ViewPendingOutputBoundary viewPendingPresenter) {
        this.membershipDataAccessObject = membershipDataAccessObject;
        this.viewPendingPresenter = viewPendingPresenter;
    } // ViewPendingInteractor

    @Override
    public void execute(ViewPendingInputData viewPendingInputData) {
        final String groupId = viewPendingInputData.getGroupId();

        List<Membership> pending = membershipDataAccessObject.getPendingForGroup(groupId);
        ArrayList<String> pendingUsernames = new ArrayList<>();
        for (Membership request : pending) {
            pendingUsernames.add(request.getUsername());
        }

        final ViewPendingOutputData viewPendingOutputData = new ViewPendingOutputData(pendingUsernames);
        viewPendingPresenter.prepareSuccessView(viewPendingOutputData);

    }
}
