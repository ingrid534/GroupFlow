package use_case.view_meeting;

import data_access.DBMeetingDataAccessObject;
import entity.meeting.Meeting;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Interactor for viewing meetings in a group.
 * Retrieves all meetings for a group and presents them.
 */
public class ViewMeetingsInteractor implements ViewMeetingsInputBoundary {
    private final DBMeetingDataAccessObject meetingDataAccess;
    private final ViewMeetingsOutputBoundary presenter;

    public ViewMeetingsInteractor(DBMeetingDataAccessObject meetingDataAccess,
                                  ViewMeetingsOutputBoundary presenter) {
        this.meetingDataAccess = meetingDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(ViewMeetingsInputData inputData) {
        List<Meeting> meetings = meetingDataAccess.getMeetingsForGroup(inputData.getGroupId());
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<ViewMeetingsOutputData.MeetingDTO> dtos = new ArrayList<>();

        for (Meeting meeting : meetings) {
            String dateString = meeting.hasDate() && meeting.getDate().isPresent()
                    ? meeting.getDate().get().format(fmt)
                    : "No date";
            dtos.add(new ViewMeetingsOutputData.MeetingDTO(
                    meeting.getID(),
                    meeting.getDescription(),
                    dateString
            ));
        }

        presenter.present(new ViewMeetingsOutputData(dtos));
    }
}