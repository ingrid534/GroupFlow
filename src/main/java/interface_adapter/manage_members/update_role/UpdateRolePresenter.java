package interface_adapter.manage_members.update_role;

import interface_adapter.manage_members.ManageMembersState;
import interface_adapter.manage_members.PeopleTabViewModel;
import use_case.manage_members.update_role.UpdateRoleOutputBoundary;
import use_case.manage_members.update_role.UpdateRoleOutputData;

public class UpdateRolePresenter implements UpdateRoleOutputBoundary {
    private final PeopleTabViewModel peopleTabViewModel;

    public UpdateRolePresenter(PeopleTabViewModel peopleTabViewModel) {
        this.peopleTabViewModel = peopleTabViewModel;
    }

    /**
     * Prepares the success view for the Update Role use case.
     *
     * @param outputData the output data containing the updated group's members
     */
    @Override
    public void prepareSuccessView(UpdateRoleOutputData outputData) {
        // Update PeopleTabViewModel state
        final ManageMembersState manageMembersState = peopleTabViewModel.getState();
        manageMembersState.setMembers(outputData.getMembers());
        peopleTabViewModel.setState(manageMembersState);

        // Notify view
        this.peopleTabViewModel.firePropertyChange("members");

    }
}
