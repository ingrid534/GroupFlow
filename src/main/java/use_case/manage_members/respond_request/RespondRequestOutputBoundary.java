package use_case.manage_members.respond_request;

public interface RespondRequestOutputBoundary {
    /**
     * Prepares the success view for the Respond Request use case.
     *
     * @param outputData the output data containing the map of members' usernames to their roles
     *                   and the list of updated pending requests to join.
     */
    void prepareSuccessView(RespondRequestOutputData outputData);

}
