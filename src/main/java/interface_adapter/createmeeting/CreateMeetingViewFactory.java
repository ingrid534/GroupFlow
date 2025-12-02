package interface_adapter.createmeeting;

import data_access.DBGroupDataAccessObject;
import data_access.DBMeetingDataAccessObject;
import use_case.create_meeting.CreateMeetingInputBoundary;
import use_case.create_meeting.CreateMeetingInteractor;
import use_case.create_meeting.CreateMeetingOutputBoundary;
import use_case.view_meeting.ViewMeetingsInputBoundary;
import use_case.view_meeting.ViewMeetingsInteractor;
import use_case.view_meeting.ViewMeetingsOutputBoundary;
import view.CreateMeetingView;

/**
 * Factory for creating CreateMeetingView instances per group.
 * Handles all wiring: presenters, interactors, controllers.
 */
public class CreateMeetingViewFactory {

    private final DBGroupDataAccessObject groupDataAccessObject;
    private final DBMeetingDataAccessObject meetingDataAccessObject;

    public CreateMeetingViewFactory(DBGroupDataAccessObject groupDataAccessObject,
                                    DBMeetingDataAccessObject meetingDataAccessObject) {
        this.groupDataAccessObject = groupDataAccessObject;
        this.meetingDataAccessObject = meetingDataAccessObject;
    }

    public CreateMeetingView create(String groupId, String groupName) {
        CreateMeetingViewModel viewModel = new CreateMeetingViewModel();
        ViewMeetingsOutputBoundary viewMeetingsPresenter =
                new ViewMeetingsPresenter(viewModel);

        ViewMeetingsInputBoundary viewMeetingsInteractor =
                new ViewMeetingsInteractor(
                        meetingDataAccessObject,
                        viewMeetingsPresenter
                );

        CreateMeetingOutputBoundary createMeetingPresenter =
                new CreateMeetingPresenter(
                        viewModel,
                        viewMeetingsInteractor,
                        groupId
                );

        CreateMeetingInputBoundary createMeetingInteractor =
                new CreateMeetingInteractor(
                        groupDataAccessObject,
                        meetingDataAccessObject,
                        createMeetingPresenter,
                        new entity.meeting.MeetingFactory()
                );

        CreateMeetingController controller =
                new CreateMeetingController(createMeetingInteractor, viewMeetingsInteractor);

        CreateMeetingView view = new CreateMeetingView(
                groupId,
                groupName,
                controller,
                viewModel,
                meetingDataAccessObject
        );

        view.setCreateMeetingController(controller, groupId);

        System.out.println("DEBUG: CreateMeetingView wired for group: " + groupId);

        return view;
    }
}
