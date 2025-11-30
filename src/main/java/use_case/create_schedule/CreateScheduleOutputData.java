package use_case.create_schedule;

public class CreateScheduleOutputData {
    private final int[][] masterSchedule;
    private final int groupSize;

    public CreateScheduleOutputData(int[][] masterSchedule, int groupSize) {
        this.masterSchedule = masterSchedule;
        this.groupSize = groupSize;
    }

    public int[][] getMasterSchedule() {
        return masterSchedule;
    }

    public int getGroupSize() {
        return groupSize;
    }

}
