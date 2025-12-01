package app;

import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import data_access.DBGroupDataAccessObject;
import data_access.DBMembershipDataAccessObject;
import data_access.DBUserDataAccessObject;
import data_access.DBTaskDataAccessObject;
import entity.group.GroupFactory;
import entity.membership.MembershipFactory;
import entity.task.TaskFactory;
import entity.user.UserFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.create_group.CreateGroupController;
import interface_adapter.create_group.CreateGroupPresenter;
import interface_adapter.create_group.CreateGroupViewModel;
import interface_adapter.creategrouptasks.CreateGroupTasksController;
import interface_adapter.creategrouptasks.CreateGroupTasksPresenter;
import interface_adapter.creategrouptasks.CreateGroupTasksViewModel;
import interface_adapter.create_schedule.CreateScheduleController;
import interface_adapter.create_schedule.CreateSchedulePresenter;
import interface_adapter.create_schedule.CreateScheduleViewModel;
import interface_adapter.dashboard.DashboardViewModel;
import interface_adapter.editgrouptask.EditGroupTaskController;
import interface_adapter.editgrouptask.EditGroupTaskPresenter;
import interface_adapter.editgrouptask.EditGroupTaskViewModel;
import interface_adapter.logged_in.ChangePasswordController;
import interface_adapter.logged_in.ChangePasswordPresenter;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.logout.LogoutPresenter;
import interface_adapter.manage_members.PeopleTabViewModel;
import interface_adapter.manage_members.remove_member.RemoveMemberControllerFactory;
import interface_adapter.manage_members.respond_request.RespondRequestControllerFactory;
import interface_adapter.manage_members.update_role.UpdateRoleControllerFactory;
import interface_adapter.manage_members.view_members.ViewMembersControllerFactory;
import interface_adapter.manage_members.view_pending.ViewPendingControllerFactory;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupViewModel;
import interface_adapter.view_group_schedule.GroupSchedulePresenter;
import interface_adapter.view_group_schedule.GroupScheduleViewModel;
import interface_adapter.viewgrouptasks.ViewGroupTasksController;
import interface_adapter.viewgrouptasks.ViewGroupTasksPresenter;
import interface_adapter.viewgrouptasks.ViewGroupTasksViewModel;
import interface_adapter.viewtasks.ViewTasksController;
import interface_adapter.viewtasks.ViewTasksPresenter;
import interface_adapter.viewtasks.ViewTasksViewModel;
import use_case.change_password.ChangePasswordInputBoundary;
import use_case.change_password.ChangePasswordInteractor;
import use_case.change_password.ChangePasswordOutputBoundary;
import use_case.create_group.CreateGroupInputBoundary;
import use_case.create_group.CreateGroupInteractor;
import use_case.create_group.CreateGroupOutputBoundary;
import use_case.create_schedule.CreateScheduleInputBoundary;
import use_case.create_schedule.CreateScheduleOutputBoundary;
import use_case.create_schedule.CreateScheduleInteractor;
import use_case.creategrouptask.CreateGroupTaskInputBoundary;
import use_case.creategrouptask.CreateGroupTaskInteractor;
import use_case.creategrouptask.CreateGroupTaskOutputBoundary;
import use_case.editgrouptasks.EditGroupTasksInputBoundary;
import use_case.editgrouptasks.EditGroupTasksInteractor;
import use_case.editgrouptasks.EditGroupTasksOutputBoundary;
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;
import use_case.login.LoginOutputBoundary;
import use_case.logout.LogoutInputBoundary;
import use_case.logout.LogoutInteractor;
import use_case.logout.LogoutOutputBoundary;
import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;
import use_case.viewgrouptasks.ViewGroupTasksInputBoundary;
import use_case.viewgrouptasks.ViewGroupTasksInteractor;
import use_case.viewgrouptasks.ViewGroupTasksOutputBoundary;
import use_case.viewtasks.ViewTasksInputBoundary;
import use_case.viewtasks.ViewTasksInteractor;
import use_case.viewtasks.ViewTasksOutputBoundary;
import view.*;

/**
 * Class for setting up application.
 */
public class AppBuilder {
    private final JPanel cardPanel = new JPanel();
    private final CardLayout cardLayout = new CardLayout();
    final UserFactory userFactory = new UserFactory();
    final GroupFactory groupFactory = new GroupFactory();
    final MembershipFactory membershipFactory = new MembershipFactory();
    final TaskFactory taskFactory = new TaskFactory();
    final ViewManagerModel viewManagerModel = new ViewManagerModel();
    final PeopleTabViewModel peopleTabViewModel = new PeopleTabViewModel();
    ViewManager viewManager = new ViewManager(cardPanel, cardLayout, viewManagerModel);
    CreateScheduleViewModel createScheduleViewModel = new CreateScheduleViewModel();
    GroupScheduleViewModel groupScheduleViewModel = new GroupScheduleViewModel();

    private final java.util.Map<String, Dimension> viewSizes = new java.util.HashMap<>();

    // set which data access implementation to use, can be any
    // of the classes from the data_access package

    // DAO version using MongoDB
    private final String mongoDBConnectionString =
            "mongodb+srv://data_access:WCV3cDtZas1zWFTg@cluster0.pdhhga4.mongodb.net/?appName=Cluster0";

    private final String dbName = "group_flow";

    final DBGroupDataAccessObject groupDataAccessObject =
            new DBGroupDataAccessObject(groupFactory, membershipFactory, mongoDBConnectionString, dbName);

    final DBUserDataAccessObject userDataAccessObject =
            new DBUserDataAccessObject(userFactory,
            mongoDBConnectionString, dbName);

    final DBMembershipDataAccessObject membershipDataAccessObject =
            new DBMembershipDataAccessObject(membershipFactory,
                    mongoDBConnectionString, dbName);

    final DBTaskDataAccessObject taskDataAccessObject =
            new DBTaskDataAccessObject(
                    taskFactory,
                    mongoDBConnectionString,
                    dbName
            );

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
    private PeopleTabView peopleTabView;
    private GroupTasksView groupTasksView;
    private ViewGroupTasksViewModel viewGroupTasksViewModel;
    private EditGroupTaskViewModel editGroupTaskViewModel;
    private CreateGroupTasksViewModel createGroupTasksViewModel;
    private CreateScheduleView createScheduleView;
    private GroupSchedulePresenter groupSchedulePresenter;

    private JFrame application;
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
        dashboardView = new DashboardView(dashboardViewModel, viewTasksView);
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
        ViewTasksInputBoundary interactor = new ViewTasksInteractor(taskDataAccessObject, presenter,
                userDataAccessObject);
        ViewTasksController viewTasksController = new ViewTasksController(interactor);
        viewTasksView = new ViewTasksView(viewTasksViewModel, viewTasksController);
        return this;
    }

    /**
     * Method to add the TaskView to dashboard.
     *
     * @return App builder
     */
    public AppBuilder addGroupTasksUseCases() {
        viewGroupTasksViewModel = new ViewGroupTasksViewModel();
        ViewGroupTasksOutputBoundary viewPresenter =
                new ViewGroupTasksPresenter(viewGroupTasksViewModel);

        ViewGroupTasksInputBoundary viewInteractor =
                new ViewGroupTasksInteractor(taskDataAccessObject, viewPresenter, groupDataAccessObject);

        ViewGroupTasksController viewController =
                new ViewGroupTasksController(viewInteractor);

        editGroupTaskViewModel = new EditGroupTaskViewModel();
        EditGroupTasksOutputBoundary editPresenter =
                new EditGroupTaskPresenter(editGroupTaskViewModel, viewTasksViewModel);

        EditGroupTasksInputBoundary editInteractor =
                new EditGroupTasksInteractor(taskDataAccessObject, editPresenter, userDataAccessObject,
                        membershipDataAccessObject);

        EditGroupTaskController editController =
                new EditGroupTaskController(editInteractor);

        createGroupTasksViewModel = new CreateGroupTasksViewModel();
        CreateGroupTaskOutputBoundary createPresenter =
                new CreateGroupTasksPresenter(createGroupTasksViewModel, viewTasksViewModel);

        CreateGroupTaskInputBoundary createInteractor =
                new CreateGroupTaskInteractor(taskDataAccessObject, createPresenter, taskFactory,
                        userDataAccessObject, groupDataAccessObject, membershipDataAccessObject);

        CreateGroupTasksController createController =
                new CreateGroupTasksController(createInteractor);

        this.dashboardView.setViewGroupTasksController(viewController);
        this.dashboardView.setEditGroupTaskController(editController);
        this.dashboardView.setCreateGroupTasksController(createController);
        this.dashboardView.setCreateGroupTasksViewModel(createGroupTasksViewModel);
        this.dashboardView.setEditGroupTasksViewModel(editGroupTaskViewModel);
        this.dashboardView.setViewGroupTasksViewModel(viewGroupTasksViewModel);
        return this;
    }

    /**
     * Method to add the Create Group View.
     *
     * @return App Builder
     */
    public AppBuilder addCreateGroupView() {
        createGroupViewModel = new CreateGroupViewModel();
        createGroupView = new CreateGroupView(createGroupViewModel);
        return this;
    }

    /**
     * Method to add the Create Group Use Case.
     *
     * @return App Builder
     */
    public AppBuilder addCreateGroupUseCase() {
        final CreateGroupOutputBoundary createGroupOutputBoundary = new CreateGroupPresenter(viewManagerModel,
                dashboardViewModel, createGroupViewModel);
        final CreateGroupInputBoundary createGroupInteractor = new CreateGroupInteractor(
                groupDataAccessObject, userDataAccessObject, membershipDataAccessObject, createGroupOutputBoundary,
                groupFactory, membershipFactory);

        CreateGroupController createGroupController = new CreateGroupController(createGroupInteractor);
        createGroupView.setCreateGroupController(createGroupController);
        dashboardView.setCreateGroupController(createGroupController);

        createGroupView.hookCreateGroupModalOpen(application);

        return this;
    }

    /**
     * Method to add the View Members Use case.
     *
     * @return App builder.
     */
    public AppBuilder addViewMembersUseCase() {
        ViewMembersControllerFactory factory =
                new ViewMembersControllerFactory(membershipDataAccessObject);

        dashboardView.setViewMembersControllerFactory(factory);

        return this;
    }

    /**
     * Method to add the View Pending Use case.
     *
     * @return App builder.
     */
    public AppBuilder addViewPendingUseCase() {
        ViewPendingControllerFactory factory =
                new ViewPendingControllerFactory(membershipDataAccessObject);

        dashboardView.setViewPendingControllerFactory(factory);

        return this;
    }

    /**
     * Method to add the Remove Member Use case.
     *
     * @return App builder.
     */
    public AppBuilder addRemoveMemberUseCase() {
        RemoveMemberControllerFactory factory =
                new RemoveMemberControllerFactory(membershipDataAccessObject);

        dashboardView.setRemoveMemberControllerFactory(factory);

        return this;
    }

    /**
     * Method to add the Respond Request Use case.
     *
     * @return App builder.
     */
    public AppBuilder addRespondRequestUseCase() {
        RespondRequestControllerFactory factory =
                new RespondRequestControllerFactory(membershipDataAccessObject);

        dashboardView.setRespondRequestControllerFactory(factory);

        return this;
    }

    /**
     * Method to add the Update Role Use case.
     *
     * @return App builder.
     */
    public AppBuilder addUpdateRoleUseCase() {
        UpdateRoleControllerFactory factory =
                new UpdateRoleControllerFactory(membershipDataAccessObject);

        dashboardView.setUpdateRoleControllerFactory(factory);

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
                dashboardViewModel, loginViewModel, signupViewModel, viewTasksViewModel);
        final LoginInputBoundary loginInteractor = new LoginInteractor(
                userDataAccessObject, groupDataAccessObject, loginOutputBoundary);

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
     * Method to add the Create Group Use Case.
     *
     * @return App Builder
     */
    public AppBuilder addCreateScheduleUseCase() {
        final CreateScheduleOutputBoundary createScheduleOutputBoundary = 
                new CreateSchedulePresenter(createScheduleViewModel, viewManagerModel, groupScheduleViewModel);
        final CreateScheduleInputBoundary createScheduleInteractor = 
                new CreateScheduleInteractor(userDataAccessObject, 
                                        groupDataAccessObject, 
                                        createScheduleOutputBoundary, 
                                        membershipDataAccessObject, 
                                        groupSchedulePresenter);

        CreateScheduleController createScheduleController = new CreateScheduleController(createScheduleInteractor);
        createScheduleView.setCreateScheduleController(createScheduleController);
        createScheduleView.hookCreateScheduleModalOpen(application);

        return this;
    }

    /**
     * Build the JFrame.
     *
     * @return The JFrame.
     */
    public JFrame build() {
        application = new JFrame("Dashboard");
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
