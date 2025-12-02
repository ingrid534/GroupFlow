package interface_adapter.createmeeting;

import use_case.create_meeting.CreateMeetingOutputBoundary;
import use_case.create_meeting.CreateMeetingOutputData;
import use_case.view_meeting.ViewMeetingsInputBoundary;
import use_case.view_meeting.ViewMeetingsInputData;

/**
 * Presenter for the CreateMeeting use case.
 * Updates the ViewModel state and triggers viewing meetings on success.
 */
public class CreateMeetingPresenter implements CreateMeetingOutputBoundary {
    private final CreateMeetingViewModel viewModel;
    private final ViewMeetingsInputBoundary viewMeetingsInteractor;
    private final String groupId;

    public CreateMeetingPresenter(CreateMeetingViewModel viewModel,
                                  ViewMeetingsInputBoundary viewMeetingsInteractor,
                                  String groupId) {
        this.viewModel = viewModel;
        this.viewMeetingsInteractor = viewMeetingsInteractor;
        this.groupId = groupId;
    }

    @Override
    public void present(CreateMeetingOutputData response) {
        CreateMeetingState state = viewModel.getState();
        state.setSuccess(response.isSuccess());
        state.setMessage(response.getMessage());
        viewModel.setState(state);

        if (response.isSuccess()) {
            viewMeetingsInteractor.execute(new ViewMeetingsInputData(groupId));

            viewModel.firePropertyChange("meetings_updated");
        }

        viewModel.firePropertyChange("state");
    }
}
