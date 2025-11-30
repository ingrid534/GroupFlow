package interface_adapter.manage_members.respond_request;

import use_case.manage_members.respond_request.RespondRequestInputBoundary;
import use_case.manage_members.respond_request.RespondRequestInputData;

public class RespondRequestController {
    private final RespondRequestInputBoundary respondRequestInteractor;

    public RespondRequestController(RespondRequestInputBoundary respondRequestInteractor) {
        this.respondRequestInteractor = respondRequestInteractor;
    }

    /**
     * Executes the Respond Request use case for the specified user in the given group.
     *
     * @param groupID  the ID of the group the member is being removed from
     * @param username the username of the member to remove
     * @param isAccepted whether this request was accepted or not
     *
     */
    public void execute(String groupID, String username, boolean isAccepted) {
        final RespondRequestInputData respondRequestInputData =
                new RespondRequestInputData(groupID, username, isAccepted);

        respondRequestInteractor.execute(respondRequestInputData);
    }
}
