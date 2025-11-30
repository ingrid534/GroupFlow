package use_case.manage_members.update_role;

public interface UpdateRoleOutputBoundary {
    /**
     * Prepares the success view for the Update Role use case.
     * This method is called when the member's role has been successfully updated.
     *
     * @param outputData the output data containing the map of members' usernames to their roles
     */
    void prepareSuccessView(UpdateRoleOutputData outputData);
}
