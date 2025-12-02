package interface_adapter.schedule.create_schedule;

import interface_adapter.ViewManagerModel;
import interface_adapter.schedule.view_schedule.ScheduleTabViewModel;
import use_case.create_schedule.CreateScheduleOutputBoundary;
import use_case.create_schedule.CreateScheduleOutputData;

public class CreateSchedulePresenter implements CreateScheduleOutputBoundary {
    private final CreateScheduleViewModel createScheduleViewModel;
    private final ViewManagerModel viewManagerModel;
    private final ScheduleTabViewModel groupScheduleViewModel;

    public CreateSchedulePresenter(CreateScheduleViewModel createScheduleViewModel, ViewManagerModel viewManagerModel,
        ScheduleTabViewModel groupScheduleViewModel
    ) {
        this.createScheduleViewModel = createScheduleViewModel;
        this.viewManagerModel = viewManagerModel;
        this.groupScheduleViewModel = groupScheduleViewModel;
    }

    @Override
    public void prepareSuccessView(CreateScheduleOutputData response) {
        createScheduleViewModel.getState().setOpenModal(false);
        createScheduleViewModel.firePropertyChange("openModal");

        createScheduleViewModel.setState(new CreateScheduleState());
        viewManagerModel.firePropertyChange("view");

    }

    @Override
    public void prepareFailView(String error) {
        final CreateScheduleState state = createScheduleViewModel.getState();
        state.setError(error);

        createScheduleViewModel.setState(state);
        createScheduleViewModel.firePropertyChange("state");
    }

    @Override
    public void openCreateScheduleModal() {
        createScheduleViewModel.getState().setOpenModal(true);
        createScheduleViewModel.firePropertyChange("openModal");
    }

}
