package entity.schedule;

public class Schedule {
    private String userID;
    private boolean[][] availabilityGrid;

    public Schedule(String userID, boolean[][] availabilityGrid) {
        this.userID = userID;
        this.availabilityGrid = availabilityGrid;
    }

    public String getUserID() {
        return userID;
    }

    public boolean[][] getAvailability() {
        return availabilityGrid;
    }
}
