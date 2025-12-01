package use_case.create_schedule;

public class CreateScheduleInputData {
    private final boolean[][] availabilityGrid;

    public CreateScheduleInputData(boolean[][] availabilityGrid) {
        this.availabilityGrid = availabilityGrid;
    }

    public boolean[][] getAvailabilityGrid() {
        return availabilityGrid;
    }
}
