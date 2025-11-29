package interface_adapter.manage_members.view_members;

import interface_adapter.manage_members.ManageMembersState;
import interface_adapter.manage_members.PeopleTabViewModel;
import use_case.manage_members.view_members.ViewMembersOutputBoundary;
import use_case.manage_members.view_members.ViewMembersOutputData;

public class ViewMembersPresenter implements ViewMembersOutputBoundary {
    private final PeopleTabViewModel peopleTabViewModel;

    public ViewMembersPresenter(PeopleTabViewModel peopleTabViewModel) {
        this.peopleTabViewModel = peopleTabViewModel;
    }

    /**
     * Prepares the success view for the View Members use case.
     * This method updates the PeopleTabViewModel with the retrieved members
     * and fires a property change so the People tab can refresh its display.
     *
     * @param outputData the output data containing the group's members
     */
    public void prepareSuccessView(ViewMembersOutputData outputData) {
        // Update PeopleTabViewModel state
        final ManageMembersState manageMembersState = peopleTabViewModel.getState();
        manageMembersState.setMembers(outputData.getMembers());
        peopleTabViewModel.setState(manageMembersState);

        // Notify view
        this.peopleTabViewModel.firePropertyChange("members");
    } // prepareSuccessView
}
