package use_case.manage_members.view_pending;

public interface ViewPendingInputBoundary {
    /**
     * Execute the View Members Use Case.
     *
     * @param viewPendingInputData the input data for this use case
     */
    void execute(ViewPendingInputData viewPendingInputData);
}
