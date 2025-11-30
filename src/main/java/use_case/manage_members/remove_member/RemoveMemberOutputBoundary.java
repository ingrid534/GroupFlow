package use_case.manage_members.remove_member;

public interface RemoveMemberOutputBoundary {
    /**
     * Prepares the success view for the Remove Member use case.
     * This method is called when the member of a group has been successfully removed.
     *
     * @param outputData the output data containing the map of members' usernames to their roles
     */
    void prepareSuccessView(RemoveMemberOutputData outputData);

}
