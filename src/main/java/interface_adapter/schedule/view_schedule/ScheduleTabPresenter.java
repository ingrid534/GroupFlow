package interface_adapter.schedule.view_schedule;

import java.awt.Color;

import use_case.create_schedule.CreateScheduleOutputData;
import use_case.create_schedule.ViewScheduleOutputBoundary;

public class ScheduleTabPresenter implements ViewScheduleOutputBoundary {
    private final ScheduleTabViewModel groupScheduleViewModel;

    static final Color NO_COLOR = new Color(255, 255, 255);
    static final Color LIGHT_GREEN = new Color(180, 255, 180);
    static final Color MED_GREEN = new Color(0, 150, 0);
    static final Color DARK_GREEN = new Color(0, 100, 0);

    public ScheduleTabPresenter(ScheduleTabViewModel groupScheduleViewModel) {
        this.groupScheduleViewModel = groupScheduleViewModel;
    }

    @Override
    public void prepareSuccessView(CreateScheduleOutputData response) {
        int[][] masterSchedule = response.getMasterSchedule();
        int groupSize = response.getGroupSize();
        Color[][] colorSchedule = new Color[masterSchedule.length][masterSchedule[0].length];

        for (int i = 0; i < masterSchedule.length; i++) {
            for (int j = 0; j < masterSchedule[i].length; j++) {
                colorSchedule[i][j] = pickColor(masterSchedule[i][j], groupSize);
            }
        }

        ScheduleTabState state = groupScheduleViewModel.getState();
        state.setMasterSchedule(colorSchedule);
        state.setGroupSize(groupSize);

        groupScheduleViewModel.setState(state);
        groupScheduleViewModel.firePropertyChange("schedule");

    }

    /**
     * Algorithm for what color to fill each time slot.
     * @param num the number of users available in the group at this time slot
     * @param groupSize the size of the group (for calculating percent)
     * @return the color to fill the time slot with
     */
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
        final ScheduleTabState state = groupScheduleViewModel.getState();
        state.setError(error);

        groupScheduleViewModel.setState(state);
        groupScheduleViewModel.firePropertyChange("schedule");
    }
}
