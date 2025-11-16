package app;

import data_access.FileUserDataAccessObject;
import entity.group.GroupFactory;
import entity.user.UserFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.create_group.CreateGroupController;
import interface_adapter.create_group.CreateGroupPresenter;
import interface_adapter.create_group.CreateGroupViewModel;
import interface_adapter.dashboard.DashboardViewModel;
import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.ChangePasswordPresenter;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.logout.LogoutPresenter;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;
import use_case.change_password.ChangePasswordInputBoundary;
import use_case.change_password.ChangePasswordInteractor;
import use_case.change_password.ChangePasswordOutputBoundary;
import use_case.create_group.CreateGroupInputBoundary;
import use_case.create_group.CreateGroupInteractor;
import use_case.create_group.CreateGroupOutputBoundary;
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.logout.LogoutInputBoundary;
import use_case.logout.LogoutInteractor;
import use_case.logout.LogoutOutputBoundary;
import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;
import view.*;

import javax.swing.*;
import java.awt.*;

public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    final UserFactory userFactory = new UserFactory();
    final GroupFactory groupFactory = new GroupFactory();
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    private final java.util.Map<String, Dimension> viewSizes = new java.util.HashMap<>();

    // set which data access implementation to use, can be any
    // of the classes from the data_access package

    // DAO version using local file storage
    final FileUserDataAccessObject userDataAccessObject = new FileUserDataAccessObject("users.csv", userFactory);

    // DAO version using a shared external database
    // final DBUserDataAccessObject userDataAccessObject = new
    // DBUserDataAccessObject(userFactory);

    private SignupView signupView;
    private SignupViewModel signupViewModel;
    private LoginViewModel loginViewModel;
    private LoggedInViewModel loggedInViewModel;
    private DashboardViewModel dashboardViewModel;
    private CreateGroupViewModel createGroupViewModel;
    private LoggedInView loggedInView;
    private LoginView loginView;
    private DashboardView dashboardView;
    private CreateGroupView createGroupView;

    public AppBuilder() {
        cardPanel.setLayout(cardLayout);
    }

    public AppBuilder addSignupView() {
        signupViewModel = new SignupViewModel();
        signupView = new SignupView(signupViewModel);
        cardPanel.add(signupView, signupView.getViewName());
        viewSizes.put(signupView.getViewName(), new Dimension(420, 320));
        return this;
    }

    public AppBuilder addLoginView() {
        loginViewModel = new LoginViewModel();
        loginView = new LoginView(loginViewModel);
        cardPanel.add(loginView, loginView.getViewName());
        viewSizes.put(loginView.getViewName(), new Dimension(420, 320));
        return this;
    }

    public AppBuilder addLoggedInView() {
        loggedInViewModel = new LoggedInViewModel();
        loggedInView = new LoggedInView(loggedInViewModel);
        cardPanel.add(loggedInView, loggedInView.getViewName());
        return this;
    }

    public AppBuilder addDashboardView() {
        dashboardViewModel = new DashboardViewModel();
        dashboardView = new DashboardView(dashboardViewModel);
        cardPanel.add(dashboardView, dashboardView.getViewName());
        viewSizes.put(dashboardView.getViewName(), new Dimension(1000, 600));
        return this;
    }

    public AppBuilder addCreateGroupView() {
        createGroupViewModel = new CreateGroupViewModel();
        createGroupView = new CreateGroupView(createGroupViewModel);
        cardPanel.add(createGroupView, createGroupView.getViewName());
        return this;
    }

    public AppBuilder addCreateGroupUseCase() {
        final CreateGroupOutputBoundary createGroupOutputBoundary = new CreateGroupPresenter(viewManagerModel,
                dashboardViewModel, createGroupViewModel);
        final CreateGroupInputBoundary createGroupInteractor = new CreateGroupInteractor(
                userDataAccessObject, createGroupOutputBoundary, groupFactory);

        CreateGroupController createGroupController = new CreateGroupController(createGroupInteractor);
        createGroupView.setLoginController(createGroupController);

        final ChangePasswordOutputBoundary changePasswordOutputBoundary = new ChangePasswordPresenter(viewManagerModel,
                loggedInViewModel, createGroupViewModel);

        final ChangePasswordInputBoundary changePasswordInteractor = new ChangePasswordInteractor(userDataAccessObject,
                changePasswordOutputBoundary, userFactory);

        ChangePasswordController changePasswordController = new ChangePasswordController(changePasswordInteractor);
        dashboardView.setChangePasswordController(changePasswordController);

        return this;
    }

    public AppBuilder addSignupUseCase() {
        final SignupOutputBoundary signupOutputBoundary = new SignupPresenter(viewManagerModel,
                signupViewModel, loginViewModel);
        final SignupInputBoundary userSignupInteractor = new SignupInteractor(
                userDataAccessObject, signupOutputBoundary, userFactory);

        SignupController controller = new SignupController(userSignupInteractor);
        signupView.setSignupController(controller);
        return this;
    }

    public AppBuilder addLoginUseCase() {
        final LoginOutputBoundary loginOutputBoundary = new LoginPresenter(viewManagerModel,
                dashboardViewModel, loginViewModel, signupViewModel);
        final LoginInputBoundary loginInteractor = new LoginInteractor(
                userDataAccessObject, loginOutputBoundary);

        LoginController loginController = new LoginController(loginInteractor);
        loginView.setLoginController(loginController);
        return this;
    }

    public AppBuilder addChangePasswordUseCase() {
        final ChangePasswordOutputBoundary changePasswordOutputBoundary = new ChangePasswordPresenter(viewManagerModel,
                loggedInViewModel, createGroupViewModel);

        final ChangePasswordInputBoundary changePasswordInteractor = new ChangePasswordInteractor(userDataAccessObject,
                changePasswordOutputBoundary, userFactory);

        ChangePasswordController changePasswordController = new ChangePasswordController(changePasswordInteractor);
        loggedInView.setChangePasswordController(changePasswordController);
        dashboardView.setChangePasswordController(changePasswordController);
        return this;
    }

    /**
     * Adds the Logout Use Case to the application.
     * 
     * @return this builder
     */
    public AppBuilder addLogoutUseCase() {
        final LogoutOutputBoundary logoutOutputBoundary = new LogoutPresenter(viewManagerModel,
                dashboardViewModel, loginViewModel);

        final LogoutInputBoundary logoutInteractor = new LogoutInteractor(userDataAccessObject, logoutOutputBoundary);

        final LogoutController logoutController = new LogoutController(logoutInteractor);
        dashboardView.setLogoutController(logoutController);
        return this;
    }

    public JFrame build() {
        final JFrame application = new JFrame("Dashboard");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        application.setContentPane(cardPanel);

        // when view changes, set preferred size for that view and pack
        viewManagerModel.addPropertyChangeListener(evt -> {
            if ("state".equals(evt.getPropertyName())) {
                String viewName = (String) evt.getNewValue();
                Dimension d = viewSizes.get(viewName);
                if (d != null) {
                    application.setPreferredSize(d);
                } else {
                    application.setPreferredSize(null); // fallback to view’s own preferred size
                }
                cardLayout.show(cardPanel, viewName);
                application.pack();
                application.setLocationRelativeTo(null);
            }
        });

        // initial state
        String initial = signupView.getViewName();
        viewManagerModel.setState(initial);
        viewManagerModel.firePropertyChange();

        // initial preferred size and pack BEFORE showing
        Dimension initSize = viewSizes.get(initial);
        if (initSize != null)
            application.setPreferredSize(initSize);
        cardLayout.show(cardPanel, initial);
        application.pack();
        application.setLocationRelativeTo(null);

        // minimum size so tiny views don’t collapse
        application.setMinimumSize(new Dimension(400, 300));

        return application;
    }

}
