package use_case.view_meeting;

import entity.meeting.Meeting;
import java.util.List;

public class ViewMeetingsOutputData {
    private final List<MeetingDTO> meetings;

    public ViewMeetingsOutputData(List<MeetingDTO> meetings) {
        this.meetings = meetings;
    }

    public List<MeetingDTO> getMeetings() {
        return meetings;
    }

    public static class MeetingDTO {
        private final String id;
        private final String description;
        private final String dateString;

        public MeetingDTO(String id, String description, String dateString) {
            this.id = id;
            this.description = description;
            this.dateString = dateString;
        }

        public String getId() {
            return id;
        }

        public String getDescription() {
            return description;
        }

        public String getDateString() {
            return dateString;
        }
    }
}
