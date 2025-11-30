package interface_adapter.create_schedule;

import java.awt.Color;

public class CreateScheduleState {
    private Color[][] masterSchedule;
    private boolean openModal;
    private String error = "";

    public Color[][] getMasterSched() {
        return masterSchedule;
    }

    public boolean getOpenModal() {
        return openModal;
    }

    public String getError() {
        return error;
    }

    public void setMasterSchedule(Color[][] masterSchedule) {
        this.masterSchedule = masterSchedule;
    } 

    public void setOpenModal(boolean modalState) {
        this.openModal = modalState;
    }

    public void setError(String error) {
        this.error = error;
    }
}
