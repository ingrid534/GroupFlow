package interface_adapter.create_group;

import interface_adapter.ViewManagerModel;
import interface_adapter.dashboard.DashboardViewModel;
import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginState;
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
        // and clear everything from the  state
        createGroupViewModel.setState(new CreateGroupState());

        // switch to the logged in view
        this.viewManagerModel.setState(dashboardViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String error) {

    }

    @Override
    public void switchToGroupView() {
        viewManagerModel.setState(dashboardViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }
}
