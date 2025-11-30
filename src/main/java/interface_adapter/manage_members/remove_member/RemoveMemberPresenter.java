package interface_adapter.manage_members.remove_member;

import interface_adapter.manage_members.ManageMembersState;
import interface_adapter.manage_members.PeopleTabViewModel;
import use_case.manage_members.remove_member.RemoveMemberOutputBoundary;
import use_case.manage_members.remove_member.RemoveMemberOutputData;

public class RemoveMemberPresenter implements RemoveMemberOutputBoundary {
    private final PeopleTabViewModel peopleTabViewModel;

    public RemoveMemberPresenter(PeopleTabViewModel peopleTabViewModel) {
        this.peopleTabViewModel = peopleTabViewModel;
    }

    /**
     * Prepares the success view for the Remove Member use case.
     *
     * @param outputData the output data containing the group's members
     */
    public void prepareSuccessView(RemoveMemberOutputData outputData) {
        // Update PeopleTabViewModel state
        final ManageMembersState manageMembersState = peopleTabViewModel.getState();
        manageMembersState.setMembers(outputData.getMembers());
        peopleTabViewModel.setState(manageMembersState);

        // Notify view
        this.peopleTabViewModel.firePropertyChange("members");
    } // prepareSuccessView
}
