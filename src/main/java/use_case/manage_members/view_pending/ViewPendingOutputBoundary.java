package use_case.manage_members.view_pending;

public interface ViewPendingOutputBoundary {
    /**
     * Prepares the success view for the View Pending use case.
     * This method is called when the pending join requests of a group have been successfully retrieved
     * and is responsible for presenting that data to the user.
     *
     * @param outputData the output data containing the list of pending join requests for the group
     */
    void prepareSuccessView(ViewPendingOutputData outputData);
}
