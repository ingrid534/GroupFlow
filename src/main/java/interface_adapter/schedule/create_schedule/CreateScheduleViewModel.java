package interface_adapter.schedule.create_schedule;

import interface_adapter.ViewModel;

public class CreateScheduleViewModel extends ViewModel<CreateScheduleState> {
    
    public CreateScheduleViewModel() {
        super("group schedule");
        setState(new CreateScheduleState());
    }
}
