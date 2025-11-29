package use_case.manage_members.view_members;

public interface ViewMembersInputBoundary {
    /**
     * Execute the View Members Use Case.
     *
     * @param viewMembersInputData the input data for this use case
     */
    void execute(ViewMembersInputData viewMembersInputData);
}
