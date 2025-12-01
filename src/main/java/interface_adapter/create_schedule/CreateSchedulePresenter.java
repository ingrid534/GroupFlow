package interface_adapter.create_schedule;

import interface_adapter.ViewManagerModel;
import interface_adapter.view_group_schedule.GroupScheduleState;
import interface_adapter.view_group_schedule.GroupScheduleViewModel;
import use_case.create_schedule.CreateScheduleOutputBoundary;
import use_case.create_schedule.CreateScheduleOutputData;

import java.awt.Color;

public class CreateSchedulePresenter implements CreateScheduleOutputBoundary {
    private final CreateScheduleViewModel createScheduleViewModel;
    private final ViewManagerModel viewManagerModel;
    private final GroupScheduleViewModel groupScheduleViewModel;

    // TODO: move these to the common schedule presenter
    static final Color NO_COLOR = new Color(255, 255, 255);
    static final Color LIGHT_GREEN = new Color(180, 255, 180);
    static final Color MED_GREEN = new Color(0, 150, 0);
    static final Color DARK_GREEN = new Color(0, 100, 0);

    public CreateSchedulePresenter(CreateScheduleViewModel createScheduleViewModel, ViewManagerModel viewManagerModel,
        GroupScheduleViewModel groupScheduleViewModel
    ) {
        this.createScheduleViewModel = createScheduleViewModel;
        this.viewManagerModel = viewManagerModel;
        this.groupScheduleViewModel = groupScheduleViewModel;
    }

    @Override
    public void prepareSuccessView(CreateScheduleOutputData response) {
        createScheduleViewModel..getState().setOpenModal(false);
        createScheduleViewModel.firePropertyChange("openModal");

        // TODO: move this to common schedule presenter
        int[][] masterSchedule = response.getMasterSchedule();
        int groupSize = response.getGroupSize();
        Color[][] colorSchedule = new Color[masterSchedule.length][masterSchedule[0].length];

        for (int i = 0; i < masterSchedule.length; i++) {
            for (int j = 0; j < masterSchedule[i].length; j++) {
                colorSchedule[i][j] = pickColor(masterSchedule[i][j], groupSize);
            }
        }

        createScheduleViewModel.setState(new CreateScheduleState());

        // TODO: move this to common schedule presenter
        // state.setMasterSchedule(colorSchedule);

        GroupScheduleState scheduleState = groupScheduleViewModel.getState();
        scheduleState.getMasterSchedule(response.getMasterSchedule());
        scheduleState.setGroupSize(response.getGroupSize());
        // not sure if i need this
        scheduleState.setGroupID(response.getGroupID());

        groupScheduleViewModel.setState(scheduleState);
        groupScheduleViewModel.firePropertyChange("schedule");

        viewManagerModel.setActiveView(groupScheduleViewModel.getViewName());
        viewManagerModel.firePropertyChange("view");

    }

    // TODO: move this to common schedule presenter
    private static Color pickColor(int num, int groupSize) {
        int percent = (num * 100) / groupSize;
        Color color;
        if (percent >= 75) {
            color = DARK_GREEN;
        } else if (percent >= 50) {
            color = MED_GREEN;
        } else if (percent >= 25) {
            color = LIGHT_GREEN;
        } else {
            color = NO_COLOR;
        }

        return color;
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
