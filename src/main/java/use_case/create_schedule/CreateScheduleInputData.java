package use_case.create_schedule;

public class CreateScheduleInputData {
    private final String groupID;
    private final boolean[][] availabilityGrid;

    public CreateScheduleInputData(String groupID, boolean[][] availabilityGrid) {
        this.groupID = groupID;
        this.availabilityGrid = availabilityGrid;
    }

    public boolean[][] getAvailabilityGrid() {
        return availabilityGrid;
    }

    public String getGroupID() {
        return groupID;
    }
}
