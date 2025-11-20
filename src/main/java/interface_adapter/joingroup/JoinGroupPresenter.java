package interface_adapter.joingroup;

import interface_adapter.ViewManagerModel;
import interface_adapter.dashboard.DashboardViewModel;
import use_case.join_group.JoinGroupOutputBoundary;
import use_case.join_group.JoinGroupOutputData;

public class JoinGroupPresenter implements JoinGroupOutputBoundary {
    private final JoinGroupViewModel joinGroupViewModel;
    private final DashboardViewModel dashboardViewModel;
    private final ViewManagerModel viewManagerModel;

    public JoinGroupPresenter(JoinGroupViewModel joinGroupViewModel,
                              DashboardViewModel dashboardViewModel,
                              ViewManagerModel viewManagerModel) {
        this.joinGroupViewModel = joinGroupViewModel;
        this.dashboardViewModel = dashboardViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(JoinGroupOutputData outputData) {
        // Group code accepted
        JoinGroupState state = joinGroupViewModel.getState();
        state.setGroupCode(outputData.getGroupCode());
        state.setGroupCodeError(null);
        joinGroupViewModel.setState(state);

        viewManagerModel.setState(dashboardViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        JoinGroupState state = joinGroupViewModel.getState();
        state.setGroupCodeError(errorMessage);
        joinGroupViewModel.setState(state);
    }

}
