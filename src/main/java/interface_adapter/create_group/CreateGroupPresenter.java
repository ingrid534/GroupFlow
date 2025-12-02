package interface_adapter.create_group;

import interface_adapter.ViewManagerModel;
import interface_adapter.dashboard.DashboardViewModel;
import interface_adapter.dashboard.LoggedInState;
import use_case.create_group.CreateGroupOutputBoundary;
import use_case.create_group.CreateGroupOutputData;

public class CreateGroupPresenter implements CreateGroupOutputBoundary {

    private final CreateGroupViewModel createGroupViewModel;
    private final DashboardViewModel dashboardViewModel;
    private final ViewManagerModel viewManagerModel;

    public CreateGroupPresenter(ViewManagerModel viewManagerModel,
                                DashboardViewModel dashboardViewModel,
                                CreateGroupViewModel createGroupViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.dashboardViewModel = dashboardViewModel;
        this.createGroupViewModel = createGroupViewModel;
    }

    @Override
    public void prepareSuccessView(CreateGroupOutputData response) {
        createGroupViewModel.getState().setOpenModal(false);
        createGroupViewModel.getState().setSuccess(true);
        createGroupViewModel.getState().setMessage("Group Created Successfully");
        createGroupViewModel.firePropertyChange("openModal");

        // clear everything from the state
        createGroupViewModel.setState(new CreateGroupState());

        // append to a list of Groups to dynamically show the groups
        final LoggedInState loggedInState = dashboardViewModel.getState();
        loggedInState.setGroups(response.getGroups());
        dashboardViewModel.setState(loggedInState);
        dashboardViewModel.firePropertyChange("groups");
    }

    @Override
    public void prepareFailView(String error) {
        final CreateGroupState createGroupState = createGroupViewModel.getState();
        createGroupState.setError(error);
        createGroupViewModel.firePropertyChange();
    }

    @Override
    public void openCreateGroupModal() {
        createGroupViewModel.getState().setOpenModal(true);
        createGroupViewModel.firePropertyChange("openModal");
    }
}
