package interface_adapter.login;

import interface_adapter.ViewManagerModel;
import interface_adapter.dashboard.DashboardViewModel;
import interface_adapter.dashboard.LoggedInState;
import interface_adapter.signup.SignupViewModel;
import interface_adapter.viewtasks.ViewTasksViewModel;
import use_case.login.LoginOutputBoundary;
import use_case.login.LoginOutputData;

/**
 * The Presenter for the Login Use Case.
 */
public class LoginPresenter implements LoginOutputBoundary {

    private final LoginViewModel loginViewModel;
    private final DashboardViewModel dashboardViewModel;
    private final ViewManagerModel viewManagerModel;
    private final SignupViewModel signupViewModel;
    private final ViewTasksViewModel viewTasksViewModel;

    public LoginPresenter(ViewManagerModel viewManagerModel,
                          DashboardViewModel loggedInViewModel,
                          LoginViewModel loginViewModel,
                          SignupViewModel signupViewModel,
                          ViewTasksViewModel viewTasksViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.dashboardViewModel = loggedInViewModel;
        this.loginViewModel = loginViewModel;
        this.signupViewModel = signupViewModel;
        this.viewTasksViewModel = viewTasksViewModel;
    }

    @Override
    public void prepareSuccessView(LoginOutputData response) {
        // Update DashboardViewModel state
        final LoggedInState loggedInState = dashboardViewModel.getState();
        loggedInState.setUsername(response.getUsername());
        loggedInState.setGroups(response.getGroups());
        dashboardViewModel.setState(loggedInState);

        // Notify views
        this.dashboardViewModel.firePropertyChange();

        this.viewTasksViewModel.firePropertyChange();

        // Clear everything from the LoginViewModel state
        loginViewModel.setState(new LoginState());

        // Switch to the dashboard view
        this.viewManagerModel.setState(dashboardViewModel.getViewName());
        this.viewManagerModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String error) {
        final LoginState loginState = loginViewModel.getState();
        loginState.setLoginError(error);
        loginViewModel.firePropertyChange();
    }

    @Override
    public void switchToSignupView() {
        viewManagerModel.setState(signupViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }
}
