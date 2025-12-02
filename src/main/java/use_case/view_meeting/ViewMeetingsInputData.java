package use_case.view_meeting;

/**
 * Input data for the ViewMeetings use case.
 */
public class ViewMeetingsInputData {
    private final String groupId;

    public ViewMeetingsInputData(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }
}
