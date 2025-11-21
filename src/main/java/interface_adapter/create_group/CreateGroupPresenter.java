package interface_adapter.create_group;

import interface_adapter.ViewManagerModel;
import interface_adapter.dashboard.DashboardViewModel;
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
        createGroupViewModel.firePropertyChange("openModal");

        // clear everything from the state
        createGroupViewModel.setState(new CreateGroupState());

        // Test to see we have the group data (TODO: Remove later)
        System.out.println(response.getGroupName());
        System.out.println(response.getGroupType());
        System.out.println(response.getGroupID());

        // TODO: Add the response to a DashboardState (e.g. append to a list of Groups) to dynamically show the groups
        // dashboardViewModel.getState().addGroup(groupData)
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
