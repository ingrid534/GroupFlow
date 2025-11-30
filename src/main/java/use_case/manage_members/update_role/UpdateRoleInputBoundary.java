package use_case.manage_members.update_role;

public interface UpdateRoleInputBoundary {
    /**
     * Execute the Update Role Use Case.
     *
     * @param updateRoleInputData the input data for this use case
     */
    void execute(UpdateRoleInputData updateRoleInputData);
}
