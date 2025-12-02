package use_case.create_meeting;

import entity.membership.Membership;

/**
 * Data access interface for membership information in meetings.
 */
public interface CreateMeetingMembershipDataAccessInterface {

    /**
     * Retrieves membership of a user in a meeting.
     *
     * @param username  the username
     * @param meetingId the meeting id
     * @return the membership or null if none exists
     */
    Membership get(String username, String meetingId);
}