package interface_adapter.createmeeting;

import use_case.create_meeting.CreateMeetingInputBoundary;
import use_case.create_meeting.CreateMeetingInputData;
import use_case.view_meeting.ViewMeetingsInputBoundary;
import use_case.view_meeting.ViewMeetingsInputData;

/**
 * Controller for the CreateMeeting use case.
 * Handles user input and forwards it to the interactor.
 */
public class CreateMeetingController {

    private final CreateMeetingInputBoundary createMeetingInteractor;
    private final ViewMeetingsInputBoundary viewMeetingsInteractor;

    /**
     * Constructs a CreateMeetingController.
     *
     * @param createMeetingInteractor the interactor for creating meetings
     * @param viewMeetingsInteractor the interactor for viewing meetings
     */
    public CreateMeetingController(CreateMeetingInputBoundary createMeetingInteractor,
                                   ViewMeetingsInputBoundary viewMeetingsInteractor) {
        this.createMeetingInteractor = createMeetingInteractor;
        this.viewMeetingsInteractor = viewMeetingsInteractor;
    }

    /**
     * Executes the CreateMeeting use case, then refreshes the meetings list.
     *
     * @param description Description of the meeting
     * @param date        Date string (nullable)
     * @param groupId     The group Id
     */
    public void execute(String description,
                        String date,
                        String groupId) {

        // 1. Create meeting
        CreateMeetingInputData inputData =
                new CreateMeetingInputData(description, date, groupId);
        createMeetingInteractor.execute(inputData);

        // 2. Refresh meetings after successful creation
        viewMeetingsInteractor.execute(new ViewMeetingsInputData(groupId));
    }
}
