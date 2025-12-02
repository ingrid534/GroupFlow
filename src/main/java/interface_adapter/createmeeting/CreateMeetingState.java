package interface_adapter.createmeeting;

import use_case.view_meeting.ViewMeetingsOutputData;
import java.util.List;

/**
 * State for the CreateMeeting use case.
 * Holds current form values, result message, modal open flag, and meetings list.
 */
public class CreateMeetingState {
    private String description;
    private String date;
    private boolean success;
    private String message;
    private boolean openModal;
    private List<ViewMeetingsOutputData.MeetingDTO> meetings;

    public CreateMeetingState() {
        this.description = "";
        this.date = "";
        this.success = false;
        this.message = "";
        this.openModal = false;
        this.meetings = null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getOpenModal() {
        return openModal;
    }

    public void setOpenModal(boolean openModal) {
        this.openModal = openModal;
    }

    public List<ViewMeetingsOutputData.MeetingDTO> getMeetings() {
        return meetings;
    }

    public void setMeetings(List<ViewMeetingsOutputData.MeetingDTO> meetings) {
        this.meetings = meetings;
    }
}