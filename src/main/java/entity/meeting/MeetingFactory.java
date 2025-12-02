package entity.meeting;

public class MeetingFactory {
    /**
     * Create a new meeting instance with a deadline.
     *
     * @param meetingID   The unique ID for this meeting
     * @param description The text description of the meeting
     * @param groupID     The group to which this meeting belongs
     * @return A meeting object with a due date
     */
    public Meeting createWithDeadline(String meetingID,
                                   String description,
                                   String groupID) {
        return new Meeting(meetingID, description, groupID);
    }

}
