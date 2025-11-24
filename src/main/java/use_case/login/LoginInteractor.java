package use_case.login;

import entity.group.Group;
import entity.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Login Interactor.
 */
public class LoginInteractor implements LoginInputBoundary {
    private final LoginUserDataAccessInterface userDataAccessObject;
    private final LoginGroupsDataAccessInterface groupDataAccess;
    //    private final LoginMembershipsDataAccessInterface groupDataAccess;
    private final LoginOutputBoundary loginPresenter;

    public LoginInteractor(LoginUserDataAccessInterface userDataAccessInterface,
                           LoginGroupsDataAccessInterface groupDataAccess,
                           LoginOutputBoundary loginOutputBoundary) {
        this.userDataAccessObject = userDataAccessInterface;
        this.groupDataAccess = groupDataAccess;
        this.loginPresenter = loginOutputBoundary;
    }

    @Override
    public void execute(LoginInputData loginInputData) {
        final String username = loginInputData.getUsername();
        final String password = loginInputData.getPassword();

        if (!userDataAccessObject.existsByName(username)) {
            loginPresenter.prepareFailView(username + ": Account does not exist.");
            return;
        }

        final User user = userDataAccessObject.get(username);
        final String pwd = user.getPassword();

        if (!password.equals(pwd)) {
            loginPresenter.prepareFailView("Incorrect password for \"" + username + "\".");
            return;
        }

        // login success
        userDataAccessObject.setCurrentUsername(username);

        // load groups for this user
        // adjust method name to whatever your GroupDataAccessInterface actually uses
        final List<Group> groups = groupDataAccess.getGroupsForUser(username);

        final Map<String, String> groupMap = new HashMap<>();
        for (Group g : groups) {
            // id -> name
            groupMap.put(g.getGroupID(), g.getName());
        }

        final LoginOutputData loginOutputData = new LoginOutputData(user.getName(), groupMap);
        loginPresenter.prepareSuccessView(loginOutputData);
    }

    @Override
    public void switchToSignupView() {
        loginPresenter.switchToSignupView();
    }
}
