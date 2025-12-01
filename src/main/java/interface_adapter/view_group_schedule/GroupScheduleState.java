package interface_adapter.view_group_schedule;

import java.awt.Color;

public class GroupScheduleState {

    private Color[][] masterSchedule;
    private int groupSize;
    private String error = "";

    public GroupScheduleState() {
        masterSchedule = new Color[7][12];

        // all cells for group schedule display are initially white
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 12; j++) {
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
