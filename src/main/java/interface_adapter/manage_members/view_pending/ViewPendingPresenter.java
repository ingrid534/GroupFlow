package interface_adapter.manage_members.view_pending;

import interface_adapter.manage_members.ManageMembersState;
import interface_adapter.manage_members.PeopleTabViewModel;
import use_case.manage_members.view_pending.ViewPendingOutputBoundary;
import use_case.manage_members.view_pending.ViewPendingOutputData;

public class ViewPendingPresenter implements ViewPendingOutputBoundary {
    private final PeopleTabViewModel peopleTabViewModel;

    public ViewPendingPresenter(PeopleTabViewModel peopleTabViewModel) {
        this.peopleTabViewModel = peopleTabViewModel;
    }

    /**
     * Prepares the success view for the View Members use case.
     * This method updates the PeopleTabViewModel with the retrieved members
     * and fires a property change so the People tab can refresh its display.
     *
     * @param outputData the output data containing the group's members
     */
    public void prepareSuccessView(ViewPendingOutputData outputData) {
        // Update PeopleTabViewModel state
        final ManageMembersState manageMembersState = peopleTabViewModel.getState();
        manageMembersState.setPending(outputData.getPending());
        peopleTabViewModel.setState(manageMembersState);

        // Notify view
        this.peopleTabViewModel.firePropertyChange("pending");
    } // prepareSuccessView
}
