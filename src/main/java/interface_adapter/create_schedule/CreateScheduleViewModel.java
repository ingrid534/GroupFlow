package interface_adapter.create_schedule;

import interface_adapter.ViewModel;

public class CreateScheduleViewModel extends ViewModel<CreateScheduleState> {
    
    public CreateScheduleViewModel() {
        super("create schedule");
        setState(new CreateScheduleState());
    }
}
