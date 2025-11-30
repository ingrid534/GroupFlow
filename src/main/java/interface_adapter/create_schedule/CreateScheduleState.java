package interface_adapter.create_schedule;

public class CreateScheduleState {
    private boolean[][] masterSchedule;
    private boolean openModal;
    private String error = "";

    public boolean[][] getMasterSched() {
        return masterSchedule;
    }

    public boolean getOpenModal() {
        return openModal;
    }

    public String getError() {
        return error;
    }

    public void setMasterSchedule(boolean[][] masterSchedule) {
        this.masterSchedule = masterSchedule;
    } 

    public void setOpenModal(boolean modalState) {
        this.openModal = modalState;
    }

    public void setError(String error) {
        this.error = error;
    }
}
