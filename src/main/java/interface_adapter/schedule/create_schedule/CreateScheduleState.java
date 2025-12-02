package interface_adapter.schedule.create_schedule;

public class CreateScheduleState {

    private boolean[][] selectedSlots;
    private boolean openModal;
    private String error = "";

    public CreateScheduleState() {
        this.selectedSlots = new boolean[7][12];

        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 12; col++) {
                selectedSlots[row][col] = false;
            }
        }
        error = "";
    }

    public boolean[][] getSelectedSlots() {
        return selectedSlots;
    }

    public boolean getOpenModal() {
        return openModal;
    }

    public String getError() {
        return error;
    }

    public void setMasterSchedule(boolean[][] newSelectedSlots) {
        this.selectedSlots = newSelectedSlots;
    } 

    public void setOpenModal(boolean modalState) {
        this.openModal = modalState;
    }

    public void setError(String error) {
        this.error = error;
    }
}
