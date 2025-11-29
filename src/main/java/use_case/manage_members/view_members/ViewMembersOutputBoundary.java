package use_case.manage_members.view_members;

public interface ViewMembersOutputBoundary {
    /**
     * Prepares the success view for the View Members use case.
     * This method is called when the members of a group have been successfully retrieved
     * and is responsible for presenting that data to the user.
     *
     * @param outputData the output data containing the map of members' usernames to their roles
     */
    void prepareSuccessView(ViewMembersOutputData outputData);
}
