package use_case.create_meeting;

import data_access.DBGroupDataAccessObject;
import data_access.DBMeetingDataAccessObject;
import entity.group.Group;
import entity.meeting.Meeting;
import entity.meeting.MeetingFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Interactor for creating a new meeting inside a group.
 * Any member can create a meeting.
 */
public class CreateMeetingInteractor implements CreateMeetingInputBoundary {

    private final DBGroupDataAccessObject groupDataAccess;
    private final DBMeetingDataAccessObject meetingDataAccess;
    private final CreateMeetingOutputBoundary presenter;
    private final MeetingFactory meetingFactory;

    /**
     * Constructs a CreateMeetingInteractor.
     *
     * @param groupDataAccess Data access for group data
     * @param meetingDataAccess Data access for meeting persistence
     * @param presenter Output boundary for presenting results
     * @param meetingFactory Factory for creating Meeting entities
     */
    public CreateMeetingInteractor(DBGroupDataAccessObject groupDataAccess,
                                   DBMeetingDataAccessObject meetingDataAccess,
                                   CreateMeetingOutputBoundary presenter,
                                   MeetingFactory meetingFactory) {
        this.groupDataAccess = groupDataAccess;
        this.meetingDataAccess = meetingDataAccess;
        this.presenter = presenter;
        this.meetingFactory = meetingFactory;
    }

    @Override
    public void execute(CreateMeetingInputData inputData) {
        String groupId = inputData.getMeetingId();
        String meetingId = generateRandomHex(24);

        Group group = groupDataAccess.getGroup(groupId);
        if (group == null) {
            presenter.present(new CreateMeetingOutputData(false, "Group not found."));
            return;
        }

        String dueStr = inputData.getDate();
        LocalDateTime due = null;

        if (dueStr != null && !dueStr.trim().isEmpty()) {
            try {
                due = LocalDateTime.parse(
                        dueStr.trim(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                );
            } catch (DateTimeParseException exception) {
                presenter.present(new CreateMeetingOutputData(false, "Invalid date format. Use: yyyy-MM-dd HH:mm"));
                return;
            }
        } else {
            presenter.present(new CreateMeetingOutputData(false, "Date is required."));
            return;
        }

        Meeting meeting = meetingFactory.createWithDeadline(
                meetingId,
                inputData.getDescription(),
                groupId
        );

        System.out.println(groupId + " " + meetingId + " " + due);

        // Save the meeting to the meetings collection
        meetingDataAccess.upsertMeeting(meeting);

        presenter.present(new CreateMeetingOutputData(true,
                "Meeting created successfully."));
    }

    /**
     * Generates a random hex string of the specified length.
     *
     * @param length The length of the hex string to generate
     * @return A random hex string
     */
    private String generateRandomHex(int length) {
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < length; i++) {
            sb.append(Integer.toHexString(random.nextInt(16)));
        }
        return sb.toString();
    }
}
