package interface_adapter.createmeeting;

import use_case.view_meeting.ViewMeetingsOutputBoundary;
import use_case.view_meeting.ViewMeetingsOutputData;

/**
 * Presenter for the ViewMeetings use case.
 * Updates the CreateMeetingViewModel state with meetings data.
 */
public class ViewMeetingsPresenter implements ViewMeetingsOutputBoundary {
    private final CreateMeetingViewModel viewModel;

    public ViewMeetingsPresenter(CreateMeetingViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(ViewMeetingsOutputData response) {
        CreateMeetingState state = viewModel.getState();
        state.setMeetings(response.getMeetings());
        viewModel.setState(state);
        viewModel.firePropertyChange("meetings_updated");
    }
}