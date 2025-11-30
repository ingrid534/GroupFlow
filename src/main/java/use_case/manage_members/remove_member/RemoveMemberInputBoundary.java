package use_case.manage_members.remove_member;

public interface RemoveMemberInputBoundary {
    /**
     * Execute the Remove Member Use Case.
     *
     * @param removeMemberInputData the input data for this use case
     */
    void execute(RemoveMemberInputData removeMemberInputData);
}
