package interface_adapter.logged_in;

import interface_adapter.ViewManagerModel;
import interface_adapter.create_group.CreateGroupViewModel;
import interface_adapter.login.LoginState;
import use_case.change_password.ChangePasswordOutputBoundary;
import use_case.change_password.ChangePasswordOutputData;

/**
 * The Presenter for the Change Password Use Case.
 */
public class ChangePasswordPresenter implements ChangePasswordOutputBoundary {

    private final LoggedInViewModel loggedInViewModel;
    private final ViewManagerModel viewManagerModel;
    private final CreateGroupViewModel createGroupViewModel;

    public ChangePasswordPresenter(ViewManagerModel viewManagerModel,
                                   LoggedInViewModel loggedInViewModel,
                                   CreateGroupViewModel createGroupViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.loggedInViewModel = loggedInViewModel;
        this.createGroupViewModel = createGroupViewModel;
    }

    @Override
    public void prepareSuccessView(ChangePasswordOutputData outputData) {
        loggedInViewModel.getState().setPassword("");
        loggedInViewModel.getState().setPasswordError(null);
        loggedInViewModel.firePropertyChange("password");
    }

    @Override
    public void prepareFailView(String error) {
        loggedInViewModel.getState().setPasswordError(error);
        loggedInViewModel.firePropertyChange("password");
    }

    @Override
    public void switchToCreateGroupView(String username) {
        System.out.println(username);
        createGroupViewModel.getState().setGroupCreatorUsername(username);
        viewManagerModel.setState(createGroupViewModel.getViewName());
        viewManagerModel.firePropertyChange();
    }
}
