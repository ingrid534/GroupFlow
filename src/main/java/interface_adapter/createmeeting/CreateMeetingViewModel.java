package interface_adapter.createmeeting;

import interface_adapter.ViewModel;

/**
 * ViewModel for the CreateMeeting use case.
 * Holds a {@link CreateMeetingState} and notifies the view on updates.
 */
public class CreateMeetingViewModel extends ViewModel {

    /**
     * Constructs a CreateMeetingViewModel with an empty initial state.
     */
    public CreateMeetingViewModel() {
        super("create_meeting");
        setState(new CreateMeetingState());
    }

    @Override
    public CreateMeetingState getState() {
        return (CreateMeetingState) super.getState();
    }

    public void setState(CreateMeetingState state) {
        super.setState(state);
    }
}
