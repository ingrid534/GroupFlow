package use_case.create_schedule;

public class CreateScheduleInputData {
    private final String userID;
    private final String groupID;
    private final boolean[][] availabilityGrid;

    public CreateScheduleInputData(String userID, String groupID, boolean[][] availabilityGrid) {
        this.userID = userID;
        this.groupID = groupID;
        this.availabilityGrid = availabilityGrid;
    }

    public String getUserID() {
        return userID;
    }

    public String getGroupID() {
        return groupID;
    }

    public boolean[][] getAvailabilityGrid() {
        return availabilityGrid;
    }
}
