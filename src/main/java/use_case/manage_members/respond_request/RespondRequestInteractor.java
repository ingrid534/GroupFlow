package use_case.manage_members.respond_request;

import entity.membership.Membership;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RespondRequestInteractor implements RespondRequestInputBoundary {
    private RespondRequestDataAccessInterface membershipDataAccessObject;
    private final RespondRequestOutputBoundary respondRequestPresenter;

    public RespondRequestInteractor(RespondRequestDataAccessInterface membershipDataAccessObject,
                                    RespondRequestOutputBoundary respondRequestPresenter) {
        this.membershipDataAccessObject = membershipDataAccessObject;
        this.respondRequestPresenter = respondRequestPresenter;
    }

    @Override
    public void execute(RespondRequestInputData respondRequestInputData) {
        final String groupId = respondRequestInputData.getGroupId();
        final String username = respondRequestInputData.getUsername();
        final boolean isAccepted = respondRequestInputData.getIsAccepted();

        // process
        membershipDataAccessObject.updateMembership(groupId, username, isAccepted);
        // update members
        List<Membership> members = membershipDataAccessObject.getMembersForGroup(groupId);
        Map<String, String> newMembersHashMap = new HashMap<>();
        for (Membership member : members) {
            newMembersHashMap.put(member.getUsername(), member.getRole().toString());
        }

        // update pending
        List<Membership> pending = membershipDataAccessObject.getPendingForGroup(groupId);
        ArrayList<String> pendingUsernames = new ArrayList<>();
        for (Membership request : pending) {
            pendingUsernames.add(request.getUsername());
        }

        final RespondRequestOutputData respondRequestOutputData =
                new RespondRequestOutputData(newMembersHashMap, pendingUsernames);
        respondRequestPresenter.prepareSuccessView(respondRequestOutputData);
    }
}
