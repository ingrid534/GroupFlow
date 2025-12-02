package interface_adapter.schedule.create_schedule;

import interface_adapter.ViewManagerModel;
import interface_adapter.schedule.view_schedule.ScheduleTabViewModel;
import interface_adapter.view_group_schedule.GroupSchedulePresenter;
import interface_adapter.view_group_schedule.GroupScheduleViewModel;
import use_case.create_schedule.CreateScheduleGroupDataAccessInterface;
import use_case.create_schedule.CreateScheduleInputBoundary;
import use_case.create_schedule.CreateScheduleUserDataAccessInterface;
import use_case.create_schedule.CreateScheduleOutputBoundary;
import use_case.create_schedule.CreateScheduleInteractor;
import use_case.create_schedule.CreateScheduleMembershipDataAccessInterface;

public class CreateScheduleControllerFactory {

    private final CreateScheduleUserDataAccessInterface userDao;
    private final CreateScheduleGroupDataAccessInterface groupDao;
    private final CreateScheduleMembershipDataAccessInterface membershipDao;
    private final ViewManagerModel viewManagerModel;
    private final CreateScheduleViewModel createScheduleViewModel;
    private final GroupScheduleViewModel groupScheduleViewModel;

    public CreateScheduleControllerFactory(
            CreateScheduleUserDataAccessInterface userDao,
            CreateScheduleGroupDataAccessInterface groupDao,
            CreateScheduleMembershipDataAccessInterface membershipDao,
            ViewManagerModel viewManagerModel,
            CreateScheduleViewModel createScheduleViewModel,
            GroupScheduleViewModel groupScheduleViewModel
    ) {
        this.userDao = userDao;
        this.groupDao = groupDao;
        this.membershipDao = membershipDao;
        this.viewManagerModel = viewManagerModel;
        this.createScheduleViewModel = createScheduleViewModel;
        this.groupScheduleViewModel = groupScheduleViewModel;
    }

    /**
     * Creates a fully wired CreateScheduleController for the given ScheduleTabViewModel.
     * This method sets up the presenter and interactor so that the controller can
     * retrieve members for a specific group and update the People tab UI.
     *
     * @param viewModel the view model that will be updated with retrieved member data
     * @return a new RemoveMemberController wired to the appropriate interactor and presenter
     */
    public CreateScheduleController create(ScheduleTabViewModel viewModel) {

        CreateScheduleOutputBoundary presenter =
                new CreateSchedulePresenter(
                        createScheduleViewModel,
                        viewManagerModel,
                        groupScheduleViewModel
                );

        GroupSchedulePresenter groupSchedulePresenter =
                new GroupSchedulePresenter(viewManagerModel, groupScheduleViewModel);

        CreateScheduleInputBoundary interactor =
                new CreateScheduleInteractor(
                        userDao,
                        groupDao,
                        presenter,
                        membershipDao,
                        groupSchedulePresenter
                );

        return new CreateScheduleController(interactor);
    }
}

