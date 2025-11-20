package app;

import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import data_access.DBUserDataAccessObject;
import data_access.DBTaskDataAccessObject;
import entity.user.UserFactory;
import interface_adapter.ViewManagerModel;
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
import interface_adapter.viewtasks.ViewTasksController;
import interface_adapter.viewtasks.ViewTasksPresenter;
import interface_adapter.viewtasks.ViewTasksViewModel;
import use_case.change_password.ChangePasswordInputBoundary;
import use_case.change_password.ChangePasswordInteractor;
import use_case.change_password.ChangePasswordOutputBoundary;
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.logout.LogoutInputBoundary;
import use_case.logout.LogoutInteractor;
import use_case.logout.LogoutOutputBoundary;
import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;
import use_case.viewtasks.ViewTasksInputBoundary;
import use_case.viewtasks.ViewTasksInteractor;
import use_case.viewtasks.ViewTasksOutputBoundary;
import view.DashboardView;
import view.LoggedInView;
import view.LoginView;
import view.SignupView;
import view.ViewManager;
import view.ViewTasksView;

/**
 * Class for setting up application.
 */
public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    final UserFactory userFactory = new UserFactory();
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);

    private final java.util.Map<String, Dimension> viewSizes = new java.util.HashMap<>();

    // set which data access implementation to use, can be any
    // of the classes from the data_access package

    // DAO version using MongoDB
    final DBUserDataAccessObject userDataAccessObject = new DBUserDataAccessObject(userFactory,
            "mongodb+srv://data_access:WCV3cDtZas1zWFTg@cluster0.pdhhga4.mongodb.net/?appName=Cluster0", "group_flow");
    // to be implemented
    final DBTaskDataAccessObject taskDataAccessObject = new DBTaskDataAccessObject();

    // DAO version using a shared external database
    // final DBUserDataAccessObject userDataAccessObject = new
    // DBUserDataAccessObject(userFactory);

    private SignupView signupView;
    private SignupViewModel signupViewModel;
    private LoginViewModel loginViewModel;
    private LoggedInViewModel loggedInViewModel;
    private DashboardViewModel dashboardViewModel;
    private LoggedInView loggedInView;
    private LoginView loginView;
    private DashboardView dashboardView;
    private ViewTasksView viewTasksView;
    private ViewTasksViewModel viewTasksViewModel;

    public AppBuilder() {
        cardPanel.setLayout(cardLayout);
    }

    /**
     * Method to add the SignUp view.
     * 
     * @return The app builder.
     */
    public AppBuilder addSignupView() {
        signupViewModel = new SignupViewModel();
        signupView = new SignupView(signupViewModel);
        cardPanel.add(signupView, signupView.getViewName());
        viewSizes.put(signupView.getViewName(), new Dimension(420, 320));
        return this;
    }

    /**
     * Method to add the LoginView.
     * 
     * @return App builder
     */
    public AppBuilder addLoginView() {
        loginViewModel = new LoginViewModel();
        loginView = new LoginView(loginViewModel);
        cardPanel.add(loginView, loginView.getViewName());
        viewSizes.put(loginView.getViewName(), new Dimension(420, 320));
        return this;
    }

    /**
     * Method to add the LoggedInView.
     * 
     * @return App builder.
     */
    public AppBuilder addLoggedInView() {
        loggedInViewModel = new LoggedInViewModel();
        loggedInView = new LoggedInView(loggedInViewModel);
        cardPanel.add(loggedInView, loggedInView.getViewName());
        return this;
    }

    /**
     * Method to add the dashboard view.
     * 
     * @return App builder.
     */
    public AppBuilder addDashboardView() {
        dashboardViewModel = new DashboardViewModel();
        dashboardView = new DashboardView(dashboardViewModel);
        cardPanel.add(dashboardView, dashboardView.getViewName());
        viewSizes.put(dashboardView.getViewName(), new Dimension(1000, 600));
        return this;
    }

    /**
     * Method to add the TaskView to dashboard.
     *
     * @return App builder
     */
    public AppBuilder addViewTasksUseCase() {
        viewTasksViewModel = new ViewTasksViewModel();
        ViewTasksOutputBoundary presenter = new ViewTasksPresenter(viewTasksViewModel);
        ViewTasksInputBoundary interactor = new ViewTasksInteractor(taskDataAccessObject, presenter);
        ViewTasksController viewTasksController = new ViewTasksController(interactor);
        viewTasksView = new ViewTasksView(viewTasksViewModel, viewTasksController);
        dashboardView.setTasksView(viewTasksView);
        return this;
    }

    /**
     * Method to add the Signup Use case.
     * 
     * @return App builder.
     */
    public AppBuilder addSignupUseCase() {
        final SignupOutputBoundary signupOutputBoundary = new SignupPresenter(viewManagerModel,
                signupViewModel, loginViewModel);
        final SignupInputBoundary userSignupInteractor = new SignupInteractor(
                userDataAccessObject, signupOutputBoundary, userFactory);

        final SignupController controller = new SignupController(userSignupInteractor);
        signupView.setSignupController(controller);
        return this;
    }

    /**
     * Method to add LoginUseCase.
     * 
     * @return App builder.
     */
    public AppBuilder addLoginUseCase() {
        final LoginOutputBoundary loginOutputBoundary = new LoginPresenter(viewManagerModel,
                dashboardViewModel, loginViewModel, signupViewModel);
        final LoginInputBoundary loginInteractor = new LoginInteractor(
                userDataAccessObject, loginOutputBoundary);

        final LoginController loginController = new LoginController(loginInteractor);
        loginView.setLoginController(loginController);
        return this;
    }

    /**
     * Method to add the ChangePassword use case.
     * 
     * @return App builder.
     */
    public AppBuilder addChangePasswordUseCase() {
        final ChangePasswordOutputBoundary changePasswordOutputBoundary = new ChangePasswordPresenter(viewManagerModel,
                loggedInViewModel);

        final ChangePasswordInputBoundary changePasswordInteractor = new ChangePasswordInteractor(userDataAccessObject,
                changePasswordOutputBoundary, userFactory);

        final ChangePasswordController changePasswordController = new ChangePasswordController(
                changePasswordInteractor);
        loggedInView.setChangePasswordController(changePasswordController);
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

    /**
     * Build the JFrame.
     * 
     * @return The JFrame.
     */
    public JFrame build() {
        final JFrame application = new JFrame("Dashboard");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        application.setContentPane(cardPanel);

        // when view changes, set preferred size for that view and pack
        viewManagerModel.addPropertyChangeListener(evt -> {
            if ("state".equals(evt.getPropertyName())) {
                final String viewName = (String) evt.getNewValue();
                final Dimension d = viewSizes.get(viewName);
                if (d != null) {
                    application.setPreferredSize(d);
                } else {
                    application.setPreferredSize(null);
                    // fallback to view’s own preferred size
                }
                cardLayout.show(cardPanel, viewName);
                application.pack();
                application.setLocationRelativeTo(null);
            }
        });

        // initial state
        final String initial = signupView.getViewName();
        viewManagerModel.setState(initial);
        viewManagerModel.firePropertyChange();

        // initial preferred size and pack BEFORE showing
        final Dimension initSize = viewSizes.get(initial);
        if (initSize != null) {
            application.setPreferredSize(initSize);
        }
        cardLayout.show(cardPanel, initial);
        application.pack();
        application.setLocationRelativeTo(null);

        // minimum size so tiny views don’t collapse
        application.setMinimumSize(new Dimension(400, 300));

        return application;
    }

}
