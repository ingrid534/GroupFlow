package use_case.create_meeting;

import entity.group.Group;

/**
 * Data access interface for retrieving and saving meetings.
 */
public interface CreateMeetingGroupDataAccessInterface {

    /**
     * Gets a meeting by its id.
     *
     * @param meetingId the meeting id
     * @return the meeting group or null if not found
     */
    Group getMeeting(String meetingId);

    /**
     * Saves the updated meeting.
     *
     * @param meeting the meeting to save
     */
    void save(Group meeting);
}
