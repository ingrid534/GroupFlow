package interface_adapter.schedule.view_schedule;

import java.awt.Color;

public class ScheduleTabState {

    private Color[][] masterSchedule;
    private int groupSize;
    private String error = "";

    public ScheduleTabState() {
        masterSchedule = new Color[12][7];

        // all cells for group schedule display are initially white
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 7; j++) {
                masterSchedule[i][j] = Color.WHITE;
            }
        }
        error = "";
    }

    public Color[][] getMasterSchedule() {
        return masterSchedule;
    }

    public void setMasterSchedule(Color[][] masterSchedule) {
        this.masterSchedule = masterSchedule;
    }

    public int getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(int groupSize) {
        this.groupSize = groupSize;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
