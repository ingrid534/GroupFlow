package interface_adapter.manage_members.respond_request;

import interface_adapter.manage_members.ManageMembersState;
import interface_adapter.manage_members.PeopleTabViewModel;
import use_case.manage_members.respond_request.RespondRequestOutputBoundary;
import use_case.manage_members.respond_request.RespondRequestOutputData;

public class RespondRequestPresenter implements RespondRequestOutputBoundary {
    private final PeopleTabViewModel peopleTabViewModel;

    public RespondRequestPresenter(PeopleTabViewModel peopleTabViewModel) {
        this.peopleTabViewModel = peopleTabViewModel;
    }

    /**
     * Prepares the success view for the Respond Request use case.
     *
     * @param outputData the output data containing the group's members and pending requests to join
     */
    @Override
    public void prepareSuccessView(RespondRequestOutputData outputData) {
        // Update PeopleTabViewModel state
        final ManageMembersState manageMembersState = peopleTabViewModel.getState();
        manageMembersState.setMembers(outputData.getMembers());
        manageMembersState.setPending(outputData.getPending());
        peopleTabViewModel.setState(manageMembersState);

        // Notify view
        this.peopleTabViewModel.firePropertyChange("members");
        this.peopleTabViewModel.firePropertyChange("pending");

    }
}
