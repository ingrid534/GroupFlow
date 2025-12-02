package use_case.create_meeting;

import java.util.List;

/**
 * Input data required to create a new task in a meeting.
 */
public class CreateMeetingInputData {
    private final String description;
    private final String date;
    private final String meetingId;

    /**
     * Creates new input data for creating a task in a meeting.
     *
     * @param description the description of the task
     * @param date     the due date in yyyy-MM-dd format
     * @param meetingId   the meeting id
     */
    public CreateMeetingInputData(String description,
                                  String date,
                                  String meetingId) {
        this.description = description;
        this.date = date;
        this.meetingId = meetingId;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getMeetingId() {
        return meetingId;
    }
}
