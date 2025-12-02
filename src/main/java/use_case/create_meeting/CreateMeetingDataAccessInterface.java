package use_case.create_meeting;

import entity.meeting.Meeting;

/**
 * Data access interface for persisting tasks created in meetings.
 */
public interface CreateMeetingDataAccessInterface {

    /**
     * Inserts or updates a task in storage.
     *
     * @param task the task to save
     */
    void upsertTask(Meeting task);
}