package interface_adapter.manage_members.view_pending;

import use_case.manage_members.view_pending.ViewPendingInputBoundary;
import use_case.manage_members.view_pending.ViewPendingInputData;

public class ViewPendingController {
    private final ViewPendingInputBoundary viewPendingInteractor;

    public ViewPendingController(ViewPendingInputBoundary viewPendingInteractor) {
        this.viewPendingInteractor = viewPendingInteractor;
    }

    /**
     * Executes the View Pending Use Case.
     * @param groupID the id of the group to view pending join requests for
     */
    public void execute(String groupID) {
        final ViewPendingInputData viewPendingInputData = new ViewPendingInputData(groupID);

        viewPendingInteractor.execute(viewPendingInputData);
    }
}
